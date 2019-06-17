package net.iGap.viewmodel;
/*
 * This is the source code of iGap for Android
 * It is licensed under GNU AGPL v3.0
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright © 2017 , iGap - www.iGap.net
 * iGap Messenger | Free, Fast and Secure instant messaging application
 * The idea of the Kianiranian Company - www.kianiranian.com
 * All rights reserved.
 */

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.content.SharedPreferences;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;
import android.net.Uri;
import android.support.v4.text.HtmlCompat;
import android.view.View;

import net.iGap.G;
import net.iGap.R;
import net.iGap.dialog.DefaultRoundDialog;
import net.iGap.helper.HelperLogout;
import net.iGap.helper.HelperSaveFile;
import net.iGap.helper.HelperString;
import net.iGap.interfaces.OnInfoCountryResponse;
import net.iGap.interfaces.OnReceiveInfoLocation;
import net.iGap.interfaces.OnReceivePageInfoTOS;
import net.iGap.interfaces.OnUserInfoResponse;
import net.iGap.interfaces.OnUserLogin;
import net.iGap.interfaces.OnUserRegistration;
import net.iGap.module.AndroidUtils;
import net.iGap.module.BotInit;
import net.iGap.module.CountryListComparator;
import net.iGap.module.CountryReader;
import net.iGap.module.structs.StructCountry;
import net.iGap.proto.ProtoGlobal;
import net.iGap.proto.ProtoRequest;
import net.iGap.proto.ProtoUserRegister;
import net.iGap.realm.RealmUserInfo;
import net.iGap.request.RequestInfoCountry;
import net.iGap.request.RequestInfoLocation;
import net.iGap.request.RequestInfoPage;
import net.iGap.request.RequestQrCodeNewDevice;
import net.iGap.request.RequestQueue;
import net.iGap.request.RequestUserInfo;
import net.iGap.request.RequestUserLogin;
import net.iGap.request.RequestWrapper;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.realm.Realm;

public class FragmentRegisterViewModel extends ViewModel /*implements OnSecurityCheckPassword, OnRecoverySecurityPassword*/ {

    //view callback
    public MutableLiveData<Boolean> closeKeyword = new MutableLiveData<>();
    public MutableLiveData<Boolean> showConditionErrorDialog = new MutableLiveData<>();
    public MutableLiveData<Boolean> goNextStep = new MutableLiveData<>();
    public MutableLiveData<Boolean> showEnteredPhoneNumberStartWithZeroError = new MutableLiveData<>();
    public MutableLiveData<Boolean> showEnteredPhoneNumberError = new MutableLiveData<>();
    public MutableLiveData<Boolean> showChooseCountryDialog = new MutableLiveData<>();
    public MutableLiveData<Boolean> showConfirmPhoneNumberDialog = new MutableLiveData<>();
    public MutableLiveData<Boolean> showConnectionErrorDialog = new MutableLiveData<>();
    public MutableLiveData<Boolean> goToMainPage = new MutableLiveData<>();
    public MutableLiveData<Long> goToWelcomePage = new MutableLiveData<>();
    public MutableLiveData<Long> goToTwoStepVerificationPage = new MutableLiveData<>();
    public MutableLiveData<WaitTimeModel> showDialogWaitTime = new MutableLiveData<>();
    public MutableLiveData<Boolean> showErrorMessageEmptyErrorPhoneNumberDialog = new MutableLiveData<>();
    public MutableLiveData<Integer> showDialogQrCode = new MutableLiveData<>();
    public MutableLiveData<Uri> shareQrCodeIntent = new MutableLiveData<>();
    public MutableLiveData<Boolean> hideDialogQRCode = new MutableLiveData<>();

    public ObservableField<String> callbackBtnChoseCountry = new ObservableField<>("Iran");
    public ObservableField<String> callbackEdtCodeNumber = new ObservableField<>("+98");
    public ObservableField<String> callBackEdtPhoneNumber = new ObservableField<>("");
    public ObservableField<String> edtPhoneNumberMask = new ObservableField<>("###-###-####");
    public ObservableInt edtPhoneNumberMaskMaxCount = new ObservableInt(11);
    public ObservableInt isShowLoading = new ObservableInt(View.GONE);
    public ObservableBoolean btnChoseCountryEnable = new ObservableBoolean(true);
    public ObservableBoolean edtPhoneNumberEnable = new ObservableBoolean(true);
    public ObservableBoolean btnStartEnable = new ObservableBoolean(true);

    //Todo: fixed this
    public static String isoCode = "IR";

    public String regex;
    public boolean isVerify = false;
    public String _resultQrCode;
    private Uri image_uriQrCode;

    private String agreementDescription;
    private boolean termsAndConditionIsChecked = false;

    public ObservableField<String> callBackEdtCheckPassword = new ObservableField<>("");

    private boolean isCallMethodSupported;
    public ArrayList<StructCountry> structCountryArrayList = new ArrayList<>();
    public String phoneNumber;
    public String userName;
    public String authorHash;
    private String token;
    private String regexFetchCodeVerification;
    public long userId;
    private boolean newUser;
    private int digitCount;
    private String verifyCode;

    private int sendRequestRegister = 0;
    public int ONETIME = 1;
    private String countryName = "", pattern = "";
    private boolean locationFound;
    private int callingCode;
    private SharedPreferences sharedPreferences;

    public FragmentRegisterViewModel(SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
        showConditionErrorDialog.setValue(false);
    }

    public String getAgreementDescription() {
        return agreementDescription;
    }

    public void onTextChanged(String s) {
        if (s.startsWith("0")) {
            callBackEdtPhoneNumber.set("");
            showEnteredPhoneNumberStartWithZeroError.setValue(true);
        }
    }

    public void onClickChoseCountry() {
        showChooseCountryDialog.setValue(true);
    }

    public void termsOnCheckChange(boolean isChecked) {
        termsAndConditionIsChecked = isChecked;
    }

    public void onClicksStart() {
        phoneNumber = callBackEdtPhoneNumber.get();
        if (phoneNumber == null) {
            phoneNumber = "";
        }
        if (phoneNumber.length() > 0 && regex.equals("") || (!regex.equals("") && phoneNumber.replace("-", "").matches(regex))) {
            if (termsAndConditionIsChecked) {
                showConfirmPhoneNumberDialog.setValue(true);
            } else {
                showConditionErrorDialog.setValue(true);
            }
        } else {
            if (phoneNumber.length() == 0) {
                showErrorMessageEmptyErrorPhoneNumberDialog.setValue(true);
            } else {
                showEnteredPhoneNumberStartWithZeroError.setValue(phoneNumber.replace("-", "").matches(regex));
            }
        }
    }

    public void confirmPhoneNumber() {
        isVerify = true;
        checkVerify();
    }

    public void saveInstance(String codeNumber, String phoneNumberMask, String phoneNumber, String countryName, String agreement, String regex, int oneTime) {
        callbackEdtCodeNumber.set(codeNumber);
        edtPhoneNumberMask.set(phoneNumberMask);
        callBackEdtPhoneNumber.set(phoneNumber);
        callbackBtnChoseCountry.set(countryName);
        agreementDescription = agreement;
        this.regex = regex;
        ONETIME = oneTime;
        if (ONETIME != 1) {
            getInfo();
        }
    }

    private void checkVerify() {
        if (G.socketConnection) { //connection ok
            btnStartEnable.set(false);
            userRegister();
        } else { // connection error
            edtPhoneNumberEnable.set(true);
            showConnectionErrorDialog.setValue(true);
        }
    }

    private void userRegister() {
        isShowLoading.set(View.VISIBLE);
        requestRegister();
    }

    //Todo:Move to repository
    private void getTermsOfServiceBody() {
        G.onReceivePageInfoTOS = new OnReceivePageInfoTOS() {
            @Override
            public void onReceivePageInfo(final String bodyR) {
                if (bodyR != null) {
                    agreementDescription = HtmlCompat.fromHtml(bodyR, HtmlCompat.FROM_HTML_MODE_LEGACY).toString();
                }
                getInfoLocation();
            }

            @Override
            public void onError(int majorCode, int minorCode) {
                getTermsOfServiceBody();
            }
        };
        new RequestInfoPage().infoPage("TOS");
    }

    //Todo:Move To repository
    private void getInfoLocation() {
        new RequestInfoLocation().infoLocation(new OnReceiveInfoLocation() {
            @Override
            public void onReceive(String isoCodeR, final int callingCodeR, final String countryNameR, String patternR, String regexR) {
                locationFound = true;
                isoCode = isoCodeR;
                callingCode = callingCodeR;
                countryName = countryNameR;
                pattern = patternR;
                regex = regexR;
                G.handler.post(() -> {
                    callbackEdtCodeNumber.set("+" + callingCode);
                    callbackBtnChoseCountry.set(countryName);
                    if (pattern != null && !pattern.equals("")) {
                        edtPhoneNumberMask.set(pattern.replace("X", "#").replace(" ", "-"));
                    } else {
                        edtPhoneNumberMask.set("##################");
                    }
                });
            }

            @Override
            public void onError(int majorCode, int minorCode) {
                if (majorCode == 500 && minorCode == 1) {
                    G.handler.post(() -> locationFound = false);
                }
            }
        });
    }

    public void setCountry(StructCountry country) {
        isShowLoading.set(View.VISIBLE);
        new RequestInfoCountry().infoCountry(country.getAbbreviation(), new OnInfoCountryResponse() {
            @Override
            public void onInfoCountryResponse(final int callingCode, final String name, final String pattern, final String regexR) {
                G.handler.post(() -> {
                    isShowLoading.set(View.GONE);
                    callbackEdtCodeNumber.set("+" + callingCode);
                    if (pattern.equals("")) {
                        edtPhoneNumberMask.set("##################");
                    } else {
                        edtPhoneNumberMask.set(pattern.replace("X", "#").replace(" ", "-"));
                    }
                    regex = regexR;
                    callbackBtnChoseCountry.set(name);
                    btnStartEnable.set(true);
                });
            }

            @Override
            public void onError(int majorCode, int minorCode) {
                //empty
                isShowLoading.set(View.GONE);
            }
        });
        callBackEdtPhoneNumber.set("");
    }

    public void shareQr() {
        if (_resultQrCode == null) {
            return;
        }
        try {
            File file = new File(_resultQrCode);
            if (file.exists()) {
                shareQrCodeIntent.setValue(Uri.fromFile(file));
            }
        } catch (RuntimeException e) {
            e.printStackTrace();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }

    public void saveQr() {
        if (_resultQrCode == null) {
            return;
        }
        File file = new File(_resultQrCode);
        if (file.exists()) {
            HelperSaveFile.savePicToGallery(_resultQrCode, true);
        }
    }

    public void onClickQrCode() {
        isShowLoading.set(View.VISIBLE);
        new RequestQrCodeNewDevice().qrCodeNewDevice((codeImage, expireTime) -> {
            _resultQrCode = G.DIR_TEMP + "/" + "QrCode" + ".jpg";
            File f = new File(_resultQrCode);
            if (f.exists()) {
                f.delete();
            }
            AndroidUtils.writeBytesToFile(_resultQrCode, codeImage.toByteArray());
            image_uriQrCode = Uri.parse("file://" + _resultQrCode);
            G.handler.post(() -> {
                isShowLoading.set(View.GONE);
                showDialogQrCode.setValue(expireTime);
            });
        });
    }

    public void getInfo() {

        G.onPushLoginToken = (tokenQrCode, userNameR, userIdR, authorHashR) -> {
            token = tokenQrCode;
            G.displayName = userName = userNameR;
            G.userId = userId = userIdR;
            G.authorHash = authorHash = authorHashR;
            G.handler.post(() -> hideDialogQRCode.setValue(true));
            userLogin(token);
        };

        G.onPushTwoStepVerification = (userNameR, userIdR, authorHashR) -> {
            userName = userNameR;
            userId = userIdR;
            authorHash = authorHashR;
            G.handler.post(()->goToTwoStepVerificationPage.setValue(userIdR));
        };

        CountryReader countryReade = new CountryReader();
        StringBuilder fileListBuilder = countryReade.readFromAssetsTextFile("country.txt", G.context);

        String list = fileListBuilder.toString();
        // Split line by line Into array
        String[] listArray = list.split("\\r?\\n");
        //Convert array
        for (String s : listArray) {
            StructCountry structCountry = new StructCountry();

            String[] listItem = s.split(";");
            structCountry.setCountryCode(listItem[0]);
            structCountry.setAbbreviation(listItem[1]);
            structCountry.setName(listItem[2]);

            if (listItem.length > 3) {
                structCountry.setPhonePattern(listItem[3]);
            } else {
                structCountry.setPhonePattern(" ");
            }

            structCountryArrayList.add(structCountry);
        }

        Collections.sort(structCountryArrayList, new CountryListComparator());
        if (G.isSecure) {
            getTermsOfServiceBody();
            ONETIME = 1;
        } else {
            getInfo();
        }
    }

    private void requestRegister() {
        if (G.socketConnection) {
            phoneNumber = phoneNumber.replace("-", "");
            ProtoUserRegister.UserRegister.Builder builder = ProtoUserRegister.UserRegister.newBuilder();
            builder.setCountryCode(isoCode);
            builder.setPhoneNumber(Long.parseLong(phoneNumber));
            builder.setPreferenceMethodValue(ProtoUserRegister.UserRegisterResponse.Method.VERIFY_CODE_SMS.getNumber());
            builder.setRequest(ProtoRequest.Request.newBuilder().setId(HelperString.generateKey()));
            RequestWrapper requestWrapper = new RequestWrapper(100, builder, new OnUserRegistration() {
                @Override
                public void onRegister(final String userNameR, final long userIdR, final ProtoUserRegister.UserRegisterResponse.Method methodValue, final List<Long> smsNumbersR, String regex, int verifyCodeDigitCount, final String authorHashR, boolean callMethodSupported) {
                    isCallMethodSupported = callMethodSupported;
                    digitCount = verifyCodeDigitCount;
                    regexFetchCodeVerification = regex;
                    G.handler.post(() -> {
                        isShowLoading.set(View.GONE);
                        userName = userNameR;
                        userId = userIdR;
                        authorHash = authorHashR;
                        G.smsNumbers = smsNumbersR;
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putInt("callingCode", callingCode);
                        editor.putString("countryName", countryName);
                        editor.putString("pattern", pattern);
                        editor.putString("regex", FragmentRegisterViewModel.this.regex);
                        editor.apply();
                        goNextStep.setValue(true);
                    });
                }

                @Override
                public void onRegisterError(final int majorCode, int minorCode, int getWait) {
                    try {
                        G.handler.post(() -> isShowLoading.set(View.GONE));
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
                            G.handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    new DefaultRoundDialog(G.fragmentActivity).setTitle(R.string.USER_VERIFY_BLOCKED_USER).setMessage(R.string.Toast_Number_Block).setPositiveButton(R.string.B_ok, null).show();
                                    /*new MaterialDialog.Builder(G.fragmentActivity).title(R.string.USER_VERIFY_BLOCKED_USER).content(R.string.Toast_Number_Block).positiveText(R.string.B_ok).show();*/
                                }
                            });
                        } else if (majorCode == 136) {
                            G.handler.post(() -> showDialogWaitTime.setValue(new WaitTimeModel(R.string.USER_VERIFY_MANY_TRIES, time, majorCode)));
                        } else if (majorCode == 137) {
                            G.handler.post(() -> showDialogWaitTime.setValue(new WaitTimeModel(R.string.USER_VERIFY_MANY_TRIES_SEND, time, majorCode)));
                        } else if (majorCode == 5 && minorCode == 1) { // timeout
                            if (sendRequestRegister <= 2) {
                                requestRegister();
                                sendRequestRegister++;
                            }
                        } else if (majorCode == 10) {
                            G.handler.post(() -> showDialogWaitTime.setValue(new WaitTimeModel(R.string.IP_blocked, time, majorCode)));
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
            G.handler.postDelayed(this::requestRegister, 1000);
        }
    }

    /**
     * if the connection is established do verify otherwise start registration(step one) again
     */
    //Todo : check it with userVerify in activation fragment
    /*private void userVerify(final String userName, final String verificationCode) {
        if (G.socketConnection) {
            try {
                ProtoUserVerify.UserVerify.Builder userVerify = ProtoUserVerify.UserVerify.newBuilder();
                userVerify.setCode(Integer.parseInt(verificationCode));
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
                                Log.wtf("Register View Model","checkPassword call from userVerify");
                                checkPassword(verificationCode, false);
                            } else if (majorCode == 102 && minorCode == 1) {
                                G.handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        *//*errorVerifySms(FragmentRegister.Reason.INVALID_CODE);*//*
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
                                            new MaterialDialog.Builder(G.fragmentActivity).title(R.string.USER_VERIFY_GIVEN_USERNAME).content(R.string.Toast_Number_Block).positiveText(R.string.B_ok).show();
                                        }
                                    }
                                });
                            } else if (majorCode == 105) {
                                G.handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        // User is blocked , You cannot verify the user
                                        if (!G.fragmentActivity.isFinishing()) {
                                            new MaterialDialog.Builder(G.fragmentActivity).title(R.string.USER_VERIFY_BLOCKED_USER).content(R.string.Toast_Number_Block).positiveText(R.string.B_ok).show();
                                        }
                                    }
                                });
                            } else if (majorCode == 106) {
                                G.handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        // Verification code is invalid
                                        *//*errorVerifySms(FragmentRegister.Reason.INVALID_CODE);*//*
                                    }
                                });
                            } else if (majorCode == 107) {
                                G.handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        // Verification code is expired
                                        if (!G.fragmentActivity.isFinishing()) {
                                            new MaterialDialog.Builder(G.fragmentActivity).title(R.string.USER_VERIFY_EXPIRED)
                                                    .content(R.string.Toast_Number_Block)
                                                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                                                        @Override
                                                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                                        }
                                                    })
                                                    .positiveText(R.string.B_ok)
                                                    .show();
                                        }
                                    }
                                });
                            } else if (majorCode == 108) {
                                G.handler.post(() -> {
                                    // Verification code is locked for a while due to too many tries
                                    showDialogWaitTime.setValue(new WaitTimeModel(R.string.USER_VERIFY_MANY_TRIES, time, majorCode));
                                });
                            } else if (majorCode == 5 && minorCode == 1) {
                                userVerify(userName, verifyCode);
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
            requestRegister();
        }
    }*/
    private void userLogin(final String token) {
        G.onUserLogin = new OnUserLogin() {
            @Override
            public void onLogin() {
                Realm realm = Realm.getDefaultInstance();
                realm.executeTransaction(realm1 -> RealmUserInfo.putOrUpdate(realm1, userId, userName, phoneNumber, token, authorHash));
                BotInit.setCheckDrIgap(true);
                if (newUser) {
                    G.handler.post(() -> goToWelcomePage.setValue(userId));
                } else {
                    // get user info for set nick name and after from that go to ActivityMain
                    getUserInfo();
                    requestUserInfo();
                }
                realm.close();
            }

            @Override
            public void onLoginError(int majorCode, int minorCode) {
                if (majorCode == 111 && minorCode == 4) {
                    G.handler.post(() -> HelperLogout.logout());
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
                        goToMainPage.setValue(true);
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
            G.handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    requestUserInfo();
                }
            }, 1000);
        }
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
            G.handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    requestLogin();
                }
            }, 1000);
        }
    }

    public void timerFinished() {
        btnStartEnable.set(true);
        btnChoseCountryEnable.set(true);
        edtPhoneNumberEnable.set(true);
    }

    public enum Reason {
        SOCKET, TIME_OUT, INVALID_CODE
    }
}