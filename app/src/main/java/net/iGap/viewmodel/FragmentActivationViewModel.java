package net.iGap.viewmodel;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.text.format.DateUtils;
import android.util.Log;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import net.iGap.BuildConfig;
import net.iGap.Config;
import net.iGap.G;
import net.iGap.R;
import net.iGap.dialog.DefaultRoundDialog;
import net.iGap.helper.HelperLogout;
import net.iGap.helper.HelperString;
import net.iGap.interfaces.OnUserInfoResponse;
import net.iGap.interfaces.OnUserLogin;
import net.iGap.interfaces.OnUserRegistration;
import net.iGap.interfaces.OnUserVerification;
import net.iGap.proto.ProtoGlobal;
import net.iGap.proto.ProtoRequest;
import net.iGap.proto.ProtoUserRegister;
import net.iGap.proto.ProtoUserVerify;
import net.iGap.realm.RealmUserInfo;
import net.iGap.request.RequestQueue;
import net.iGap.request.RequestUserInfo;
import net.iGap.request.RequestUserLogin;
import net.iGap.request.RequestUserTwoStepVerificationGetPasswordDetail;
import net.iGap.request.RequestWrapper;

import java.util.List;
import java.util.Locale;
import io.realm.Realm;

import static net.iGap.viewmodel.FragmentRegisterViewModel.isoCode;

public class FragmentActivationViewModel extends ViewModel /*implements OnSecurityCheckPassword, OnRecoverySecurityPassword*/ {

    public MutableLiveData<String> timerValue = new MutableLiveData<>();
    public MutableLiveData<String> verifyCode = new MutableLiveData<>();
    public MutableLiveData<Boolean> enabledResendCodeButton = new MutableLiveData<>();
    public MutableLiveData<Boolean> showEnteredCodeError = new MutableLiveData<>();
    public MutableLiveData<Integer> currentTimePosition = new MutableLiveData<>();
    public MutableLiveData<Boolean> isNewUser = new MutableLiveData<>();
    private CountDownTimer countDownTimer;
    private ProtoUserRegister.UserRegisterResponse.Method methodForReceiveCode = ProtoUserRegister.UserRegisterResponse.Method.VERIFY_CODE_SMS;
    private String userName;
    private String authorHash;
    public long userId;
    private boolean newUser;
    private String token;
    private String phoneNumber;
    private String regexFetchCodeVerification;
    private int sendRequestRegister=0;

    public FragmentActivationViewModel() {
        timerValue.setValue("1:00");
        enabledResendCodeButton.setValue(false);
        counterTimer();
    }

    public void setUserData(String userName, Long userId, String authorHash, String phoneNumber) {
        this.userName = userName;
        this.userId = userId;
        this.authorHash = authorHash;
        this.phoneNumber = phoneNumber;
    }

    private void counterTimer() {
        long time;
        if (BuildConfig.DEBUG) {
            time = 60/*2*/ * DateUtils.SECOND_IN_MILLIS;
        } else {
            time = 60 * DateUtils.SECOND_IN_MILLIS;
        }

        countDownTimer = new CountDownTimer(time, Config.COUNTER_TIMER_DELAY) {
            public void onTick(long millisUntilFinished) {
                int seconds = (int) ((millisUntilFinished) / 1000);
                int minutes = seconds / 60;
                seconds = seconds % 60;
                timerValue.setValue(String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds));
                currentTimePosition.setValue(seconds * 6);
            }

            public void onFinish() {
                enabledResendCodeButton.setValue(true);
            }
        };
        countDownTimer.start();
    }

    public void cancelTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
        }
    }


    public void resentActivationCodeClick() {
        /*methodForReceiveCode = ProtoUserRegister.UserRegisterResponse.Method.VERIFY_CODE_SMS;*/
        enabledResendCodeButton.setValue(false);
        userRegister();
        counterTimer();
    }

    public void loginButtonOnClick(String enteredCode) {
        if (enteredCode.length() > 0) {
            userVerify(userName, enteredCode);
        } else {
            showEnteredCodeError.setValue(true);
        }
    }

    public void receiveVerifySms(String message) {
        verifyCode.setValue(HelperString.regexExtractValue(message, regexFetchCodeVerification));
    }

    private void userVerify(final String userName, final String verificationCode) {
        if (G.socketConnection) {
            try {
                userVerifyResponse(verificationCode);
                ProtoUserVerify.UserVerify.Builder userVerify = ProtoUserVerify.UserVerify.newBuilder();
                userVerify.setCode(Integer.parseInt(verificationCode));
                userVerify.setUsername(userName);
                RequestWrapper requestWrapper = new RequestWrapper(101, userVerify);
                RequestQueue.sendRequest(requestWrapper);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        } else {
            /**
             * return view for step one and two because now start registration again from step one
             */
            /*
            // return step one
            prgVerifyConnectVisibility.set(View.VISIBLE);
            txtIconVerifyConnectVisibility.set(View.GONE);
            // clear step two
            prgVerifySmsVisibility.set(View.GONE);
            imgVerifySmsVisibility.set(View.INVISIBLE);
            txtVerifySmsColor.set(G.context.getResources().getColor(R.color.rg_text_verify));

            //rg_txt_verify_sms.setTextAppearance(G.context, Typeface.NORMAL);
            */

            requestRegister();
        }
    }

    private void userVerifyResponse(final String verificationCode) {
        G.onUserVerification = new OnUserVerification() {
            @Override
            public void onUserVerify(final String tokenR, final boolean newUserR) {
                G.handler.post(() -> {
                    newUser = newUserR;
                    token = tokenR;
                    userLogin(token);
                });
            }

            @Override
            public void onUserVerifyError(final int majorCode, int minorCode, final int time) {
                try {
                    if (majorCode == 184 && minorCode == 1) {
                        checkPassword(verificationCode, false);
                    } else if (majorCode == 102 && minorCode == 1) {
                        G.handler.post(new Runnable() {
                            @Override
                            public void run() {
                                //ToDo: fixed error handler in new form
                                /*errorVerifySms(FragmentRegister.Reason.INVALID_CODE);*/
                            }
                        });
                    } else if (majorCode == 102 && minorCode == 2) {
                        //empty
                    } else if (majorCode == 103) {
                        //empty
                    } else if (majorCode == 104) {
                        G.handler.post(new Runnable() {
                            @Override
                            public void run() {
                                // There is no registered user with given username
                                if (!G.fragmentActivity.isFinishing()) {
                                    new DefaultRoundDialog(G.fragmentActivity).setTitle(R.string.USER_VERIFY_GIVEN_USERNAME).setMessage(R.string.Toast_Number_Block).setPositiveButton(R.string.B_ok, null).show();
                                }
                            }
                        });
                    } else if (majorCode == 105) {
                        G.handler.post(new Runnable() {
                            @Override
                            public void run() {
                                // User is blocked , You cannot verify the user
                                if (!G.fragmentActivity.isFinishing()) {
                                    new DefaultRoundDialog(G.fragmentActivity).setTitle(R.string.USER_VERIFY_BLOCKED_USER).setMessage(R.string.Toast_Number_Block).setPositiveButton(R.string.B_ok, null).show();
                                }
                            }
                        });
                    } else if (majorCode == 106) {
                        G.handler.post(new Runnable() {
                            @Override
                            public void run() {
                                // Verification code is invalid
                                //ToDo: change code
                                /*errorVerifySms(FragmentRegister.Reason.INVALID_CODE);*/
                            }
                        });
                    } else if (majorCode == 107) {
                        G.handler.post(new Runnable() {
                            @Override
                            public void run() {
                                // Verification code is expired
                                if (!G.fragmentActivity.isFinishing()) {
                                    new DefaultRoundDialog(G.fragmentActivity).setTitle(R.string.USER_VERIFY_EXPIRED).setMessage(R.string.Toast_Number_Block).setPositiveButton(R.string.B_ok, null).show();
                                }
                            }
                        });
                    } else if (majorCode == 108) {
                        G.handler.post(new Runnable() {
                            @Override
                            public void run() {
                                // Verification code is locked for a while due to too many tries
                                Toast.makeText(G.fragmentActivity,"error: "+majorCode,Toast.LENGTH_LONG).show();
                                /*dialogWaitTime(R.string.USER_VERIFY_MANY_TRIES, time, majorCode);*/
                            }
                        });
                    } else if (majorCode == 5 && minorCode == 1) {
                        userVerify(userName, verifyCode.getValue());
                    }
                } catch (RuntimeException e) {
                    e.printStackTrace();
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        };
    }

    private void userLogin(final String token) {
        G.onUserLogin = new OnUserLogin() {
            @Override
            public void onLogin() {
                G.handler.post(() -> {
                    Realm realm = Realm.getDefaultInstance();
                    realm.executeTransaction(realm1 -> RealmUserInfo.putOrUpdate(realm1, userId, userName, phoneNumber, token, authorHash));
                    if (newUser) {
                        G.handler.post(() -> isNewUser.setValue(true));
                    } else {
                        // get user info for set nick name and after from that go to ActivityMain
                        getUserInfo();
                        requestUserInfo();
                    }
                    realm.close();
                });
            }

            @Override
            public void onLoginError(int majorCode, int minorCode) {
                if (majorCode == 111 && minorCode == 4) {
                    G.handler.post(HelperLogout::logout);
                } else if (majorCode == 111) {
                    G.handler.post(() -> {
                        //requestLogin();
                    });
                } else if (majorCode == 5 && minorCode == 1) {
                    requestLogin();
                }
            }
        };
        requestLogin();
    }

    private void requestLogin() {
        if (G.socketConnection) {
            if (token == null) {
                Realm realm = Realm.getDefaultInstance();
                RealmUserInfo realmUserInfo = realm.where(RealmUserInfo.class).findFirst();
                if (realmUserInfo == null) {
                    //finish();
                } else {
                    token = realmUserInfo.getToken();
                }
                realm.close();
            }
            new RequestUserLogin().userLogin(token);
        } else {
            G.handler.postDelayed(this::requestLogin, 1000);
        }
    }

    private void getUserInfo() {
        G.onUserInfoResponse = new OnUserInfoResponse() {
            @Override
            public void onUserInfo(final ProtoGlobal.RegisteredUser user, String identity) {
                Realm realm = Realm.getDefaultInstance();
                realm.executeTransaction(realm1 -> {
                    G.displayName = user.getDisplayName();
                    RealmUserInfo.putOrUpdate(realm1, user);
                    G.handler.post(() -> {
                        G.onUserInfoResponse = null;
                        isNewUser.setValue(false);
                    });
                });
                realm.close();
            }

            @Override
            public void onUserInfoTimeOut() {
                requestUserInfo();
            }

            @Override
            public void onUserInfoError(int majorCode, int minorCode) {

            }
        };
    }

    private void requestUserInfo() {
        if (G.socketConnection) {
            if (userId == 0) {
                Realm realm = Realm.getDefaultInstance();
                RealmUserInfo realmUserInfo = realm.where(RealmUserInfo.class).findFirst();
                if (realmUserInfo == null) {
                    //finish();
                } else {
                    userId = realmUserInfo.getUserId();
                }
                realm.close();
            }
            new RequestUserInfo().userInfo(userId);
        } else {
            G.handler.postDelayed(this::requestUserInfo, 1000);
        }
    }

    private void userRegister() {
        G.onUserRegistration = new OnUserRegistration() {
            @Override
            public void onRegister(final String userNameR, final long userIdR, final ProtoUserRegister.UserRegisterResponse.Method methodValue, final List<Long> smsNumbersR, String regex, int verifyCodeDigitCount, final String authorHashR, boolean callMethodSupported) {
                Log.wtf("view model", "onRegister");
                G.onUserRegistration = null;
                /*isCallMethodSupported = callMethodSupported;
                digitCount = verifyCodeDigitCount;*/
                regexFetchCodeVerification = regex;
                G.handler.post(() -> {
                    userName = userNameR;
                    userId = userIdR;
                    authorHash = authorHashR;
                    G.smsNumbers = smsNumbersR;
                    counterTimer();
                });
            }

            @Override
            public void onRegisterError(final int majorCode, int minorCode, int getWait) {
                try {
                    final long time = getWait;
                    if (majorCode == 100 && minorCode == 1) {
                        G.handler.post(new Runnable() {
                            @Override
                            public void run() {
                                //Invalid countryCode
                            }
                        });
                    } else if (majorCode == 100 && minorCode == 2) {
                        G.handler.post(new Runnable() {
                            @Override
                            public void run() {
                                //Invalid phoneNumber
                            }
                        });
                    } else if (majorCode == 101) {
                        G.handler.post(new Runnable() {
                            @Override
                            public void run() {
                                //Invalid phoneNumber
                            }
                        });
                    } else if (majorCode == 135) {
                        G.handler.post(new Runnable() {
                            @Override
                            public void run() {
                                new DefaultRoundDialog(G.fragmentActivity).setTitle(R.string.USER_VERIFY_BLOCKED_USER).setMessage(R.string.Toast_Number_Block).setPositiveButton(R.string.B_ok, null).show();
                                /*new MaterialDialog.Builder(G.fragmentActivity).title(R.string.USER_VERIFY_BLOCKED_USER).content(R.string.Toast_Number_Block).positiveText(R.string.B_ok).show();*/
                            }
                        });
                    } else if (majorCode == 136) {
                        G.handler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(G.fragmentActivity,"error: "+majorCode,Toast.LENGTH_LONG).show();
                                /*dialogWaitTime(R.string.USER_VERIFY_MANY_TRIES, time, majorCode);*/
                            }
                        });
                    } else if (majorCode == 137) {
                        G.handler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(G.fragmentActivity,"error: "+majorCode,Toast.LENGTH_LONG).show();
                                /*dialogWaitTime(R.string.USER_VERIFY_MANY_TRIES_SEND, time, majorCode);*/
                            }
                        });
                    } else if (majorCode == 5 && minorCode == 1) { // timeout

                        if (sendRequestRegister <= 2) {
                            requestRegister();
                            sendRequestRegister++;
                        }
                    } else if (majorCode == 10) {
                        G.handler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(G.fragmentActivity,"error: "+majorCode,Toast.LENGTH_LONG).show();
                                /*dialogWaitTime(R.string.IP_blocked, time, majorCode);*/
                            }
                        });
                    }
                } catch (RuntimeException e) {
                    e.printStackTrace();
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        };
        requestRegister();
    }

    private void requestRegister() {

        if (G.socketConnection) {
            phoneNumber = phoneNumber.replace("-", "");
            ProtoUserRegister.UserRegister.Builder builder = ProtoUserRegister.UserRegister.newBuilder();
            builder.setCountryCode(isoCode);
            builder.setPhoneNumber(Long.parseLong(phoneNumber));
            builder.setPreferenceMethodValue(methodForReceiveCode.getNumber());
            builder.setRequest(ProtoRequest.Request.newBuilder().setId(HelperString.generateKey()));
            RequestWrapper requestWrapper = new RequestWrapper(100, builder);

            try {
                RequestQueue.sendRequest(requestWrapper);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        } else {
            G.handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    requestRegister();
                }
            }, 1000);
        }
    }

    private void checkPassword(final String verificationCode, final boolean isQrCode) {
        new RequestUserTwoStepVerificationGetPasswordDetail().getPasswordDetail();
        G.handler.post(new Runnable() {
            @Override
            public void run() {

                if (!isQrCode) {
                    /*callBackTxtVerifySms.set((G.fragmentActivity.getResources().getString(R.string.rg_verify_register2)));
                    prgVerifySmsVisibility.set(View.GONE);
                    imgVerifySmsVisibility.set(View.VISIBLE);
                    //imgVerifySmsColor.set(R.mipmap.check);
                    imgVerifySmsColor.set(G.context.getResources().getColor(R.color.rg_text_verify));
                    txtVerifySmsColor.set(G.context.getResources().getColor(R.color.rg_text_verify));*/


                    //newUser = newUserR;
                    //token = tokenR;

                    /*prgVerifyKeyVisibility.set(View.GONE);
                    imgVerifyKeyVisibility.set(View.VISIBLE);
                    txtVerifyKeColor.set(G.context.getResources().getColor(R.color.rg_text_verify));*/
                    //userLogin(token);

                }
                /*rootMainLayoutVisibility.set(View.GONE);
                rootCheckPasswordVisibility.set(View.VISIBLE);
                txtOkVisibility.set(View.VISIBLE);
                qrCodeVisibility.set(View.GONE);*/

            }
        });
    }
}
