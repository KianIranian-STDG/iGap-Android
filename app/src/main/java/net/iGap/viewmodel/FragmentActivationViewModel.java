package net.iGap.viewmodel;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.os.CountDownTimer;
import android.text.format.DateUtils;
import android.util.Log;

import net.iGap.BuildConfig;
import net.iGap.Config;
import net.iGap.G;
import net.iGap.R;
import net.iGap.dialog.DefaultRoundDialog;
import net.iGap.helper.HelperLogout;
import net.iGap.helper.HelperString;
import net.iGap.helper.HelperTracker;
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
    public MutableLiveData<WaitTimeModel> showWaitDialog = new MutableLiveData<>();
    public MutableLiveData<Integer> showEnteredCodeErrorServer = new MutableLiveData<>();
    public MutableLiveData<Boolean> showLoading = new MutableLiveData<>();
    public MutableLiveData<Boolean> closeKeyword = new MutableLiveData<>();
    public MutableLiveData<Boolean> clearActivationCode = new MutableLiveData<>();
    public MutableLiveData<Long> goToTwoStepVerificationPage = new MutableLiveData<>();

    private CountDownTimer countDownTimer;
    private ProtoUserRegister.UserRegisterResponse.Method methodForReceiveCode = ProtoUserRegister.UserRegisterResponse.Method.VERIFY_CODE_SMS;
    private String userName;
    private String authorHash;
    public long userId;
    private boolean newUser;
    private String token;
    private String phoneNumber;
    private String regexFetchCodeVerification;
    private int sendRequestRegister = 0;

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
                timerValue.setValue(String.format(Locale.getDefault(), "%02d:%02d", 0, 0));
                currentTimePosition.setValue(60 * 6);
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
        closeKeyword.setValue(true);
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
            showLoading.setValue(true);
            try {
                ProtoUserVerify.UserVerify.Builder userVerify = ProtoUserVerify.UserVerify.newBuilder();
                userVerify.setCode(Integer.parseInt(verificationCode
                        .replaceAll("[^0-9]", "")
                        .replaceAll("[\u0000-\u001f]", "")));
                userVerify.setUsername(userName);
                RequestWrapper requestWrapper = new RequestWrapper(101, userVerify, new OnUserVerification() {
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
                                G.handler.post(()->goToTwoStepVerificationPage.setValue(userId));
                            } else if (majorCode == 102 && minorCode == 1) {
                                G.handler.post(() -> {
                                    showLoading.setValue(false);
                                    errorVerifySms(Reason.INVALID_CODE);
                                });
                            } else if (majorCode == 102 && minorCode == 2) {
                                //empty
                            } else if (majorCode == 103) {
                                //empty
                            } else if (majorCode == 104) {
                                G.handler.post(() -> {
                                    // There is no registered user with given username
                                    showLoading.setValue(false);
                                    //Todo:move this code to view
                                    if (!G.fragmentActivity.isFinishing()) {
                                        new DefaultRoundDialog(G.fragmentActivity).setTitle(R.string.USER_VERIFY_GIVEN_USERNAME).setMessage(R.string.Toast_Number_Block).setPositiveButton(R.string.B_ok, null).show();
                                    }
                                });
                            } else if (majorCode == 105) {
                                G.handler.post(() -> {
                                    // User is blocked , You cannot verify the user
                                    showLoading.setValue(false);
                                    //Todo:move this code to view
                                    if (!G.fragmentActivity.isFinishing()) {
                                        new DefaultRoundDialog(G.fragmentActivity).setTitle(R.string.USER_VERIFY_BLOCKED_USER).setMessage(R.string.Toast_Number_Block).setPositiveButton(R.string.B_ok, null).show();
                                    }
                                });
                            } else if (majorCode == 106) {
                                G.handler.post(() -> {
                                    // Verification code is invalid
                                    showLoading.setValue(false);
                                    errorVerifySms(Reason.INVALID_CODE);
                                });
                            } else if (majorCode == 107) {
                                G.handler.post(() -> {
                                    // Verification code is expired
                                    showLoading.setValue(false);
                                    //Todo:move this code to view
                                    if (!G.fragmentActivity.isFinishing()) {
                                        new DefaultRoundDialog(G.fragmentActivity).setTitle(R.string.USER_VERIFY_EXPIRED).setMessage(R.string.Toast_Number_Block).setPositiveButton(R.string.B_ok, null).show();
                                    }
                                });
                            } else if (majorCode == 108) {
                                G.handler.post(() -> {
                                    showLoading.setValue(false);
                                    // Verification code is locked for a while due to too many tries
                                    showWaitDialog.setValue(new WaitTimeModel(R.string.USER_VERIFY_MANY_TRIES, time, majorCode));
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
                });
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

    private void userLogin(final String token) {
        G.onUserLogin = new OnUserLogin() {
            @Override
            public void onLogin() {
                G.handler.post(() -> {
                    showLoading.setValue(false);
                    Realm realm = Realm.getDefaultInstance();
                    realm.executeTransaction(realm1 -> RealmUserInfo.putOrUpdate(realm1, userId, userName, phoneNumber, token, authorHash));
                    if (newUser) {
                        G.handler.post(() -> isNewUser.setValue(true));
                    } else {
                        // get user info for set nick name and after from that go to ActivityMain
                        getUserInfo();
                        requestUserInfo();
                        HelperTracker.sendTracker(HelperTracker.TRACKER_REGISTRATION_USER);
                    }
                    realm.close();
                });
            }

            @Override
            public void onLoginError(int majorCode, int minorCode) {
                if (majorCode == 111 && minorCode == 4) {
                    G.handler.post(() -> {
                        showLoading.setValue(false);
                        HelperLogout.logout();
                    });
                } else if (majorCode == 111) {
                    G.handler.post(() -> {
                        //requestLogin();
                        showLoading.setValue(false);
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
            RequestWrapper requestWrapper = new RequestWrapper(100, builder, new OnUserRegistration() {
                @Override
                public void onRegister(final String userNameR, final long userIdR, final ProtoUserRegister.UserRegisterResponse.Method methodValue, final List<Long> smsNumbersR, String regex, int verifyCodeDigitCount, final String authorHashR, boolean callMethodSupported) {
                    Log.wtf("view model", "onRegister");
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
                            G.handler.post(() -> {
                                //Invalid countryCode
                            });
                        } else if (majorCode == 100 && minorCode == 2) {
                            G.handler.post(() -> {
                                //Invalid phoneNumber
                            });
                        } else if (majorCode == 101) {
                            G.handler.post(() -> {
                                //Invalid phoneNumber
                            });
                        } else if (majorCode == 135) {
                            G.handler.post(() -> {
                                new DefaultRoundDialog(G.fragmentActivity).setTitle(R.string.USER_VERIFY_BLOCKED_USER).setMessage(R.string.Toast_Number_Block).setPositiveButton(R.string.B_ok, null).show();
                                /*new MaterialDialog.Builder(G.fragmentActivity).title(R.string.USER_VERIFY_BLOCKED_USER).content(R.string.Toast_Number_Block).positiveText(R.string.B_ok).show();*/
                            });
                        } else if (majorCode == 136) {
                            G.handler.post(() -> showWaitDialog.setValue(new WaitTimeModel(R.string.USER_VERIFY_MANY_TRIES, time, majorCode)));
                        } else if (majorCode == 137) {
                            G.handler.post(() -> showWaitDialog.setValue(new WaitTimeModel(R.string.USER_VERIFY_MANY_TRIES_SEND, time, majorCode)));
                        } else if (majorCode == 5 && minorCode == 1) { // timeout
                            if (sendRequestRegister <= 2) {
                                requestRegister();
                                sendRequestRegister++;
                            }
                        } else if (majorCode == 10) {
                            G.handler.post(() -> showWaitDialog.setValue(new WaitTimeModel(R.string.IP_blocked, time, majorCode)));
                        }
                    } catch (RuntimeException e) {
                        e.printStackTrace();
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                }
            });

            try {
                RequestQueue.sendRequest(requestWrapper);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        } else {
            G.handler.postDelayed(() -> requestRegister(), 1000);
        }
    }

    private void errorVerifySms(Reason reason) { //when don't receive sms and open rg_dialog for enter code

        boolean isNeedTimer = true;
        int message = 0;

        if (reason == Reason.SOCKET) {
            message = R.string.verify_socket_message;
            isNeedTimer = false;
            enabledResendCodeButton.setValue(false);
        } else if (reason == Reason.TIME_OUT) {
            message = R.string.verify_time_out_message;
            isNeedTimer = true;
            enabledResendCodeButton.setValue(false);
        } else if (reason == Reason.INVALID_CODE) {
            message = R.string.verify_invalid_code_message;
            isNeedTimer = false;
            enabledResendCodeButton.setValue(true);
        }

        showEnteredCodeErrorServer.setValue(message);

        clearActivationCode.setValue(true);

        if (isNeedTimer) {
            counterTimer();
        }
    }

    public void timerFinished() {
        enabledResendCodeButton.setValue(true);
    }

    public enum Reason {
        SOCKET, TIME_OUT, INVALID_CODE
    }
}
