package net.iGap.model.repository;

import android.os.Handler;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import net.iGap.AccountManager;
import net.iGap.DbManager;
import net.iGap.G;
import net.iGap.helper.HelperLogout;
import net.iGap.helper.HelperPreferences;
import net.iGap.helper.HelperString;
import net.iGap.helper.HelperTracker;
import net.iGap.interfaces.OnInfoCountryResponse;
import net.iGap.interfaces.OnReceiveInfoLocation;
import net.iGap.interfaces.OnReceivePageInfoTOS;
import net.iGap.interfaces.OnUserInfoResponse;
import net.iGap.interfaces.OnUserLogin;
import net.iGap.interfaces.OnUserRegistration;
import net.iGap.interfaces.OnUserVerification;
import net.iGap.interfaces.TwoStepVerificationGetPasswordDetail;
import net.iGap.interfaces.TwoStepVerificationVerifyPassword;
import net.iGap.model.AccountUser;
import net.iGap.model.GoToMainFromRegister;
import net.iGap.model.LocationModel;
import net.iGap.model.UserPasswordDetail;
import net.iGap.module.BotInit;
import net.iGap.module.SHP_SETTING;
import net.iGap.module.SingleLiveEvent;
import net.iGap.proto.ProtoGlobal;
import net.iGap.proto.ProtoRequest;
import net.iGap.proto.ProtoUserRegister;
import net.iGap.proto.ProtoUserVerify;
import net.iGap.realm.RealmUserInfo;
import net.iGap.request.RequestInfoCountry;
import net.iGap.request.RequestInfoLocation;
import net.iGap.request.RequestInfoPage;
import net.iGap.request.RequestQueue;
import net.iGap.request.RequestUserInfo;
import net.iGap.request.RequestUserLogin;
import net.iGap.request.RequestUserTwoStepVerificationGetPasswordDetail;
import net.iGap.request.RequestUserTwoStepVerificationVerifyPassword;
import net.iGap.request.RequestWrapper;

import java.util.List;

public class RegisterRepository {

    private static RegisterRepository instance;

    private String token;
    private String phoneNumber;
    private String userName;
    private String authorHash;
    private long userId;
    private boolean newUser;
    private String regex;
    private int callingCode;
    private String isoCode = "IR";
    private String countryName = "";
    private String pattern = "";
    private String regexFetchCodeVerification;
    private boolean forgetTwoStepVerification = false;

    private SingleLiveEvent<GoToMainFromRegister> goToMainPage = new SingleLiveEvent<>();
    private SingleLiveEvent<Boolean> loginExistUser = new SingleLiveEvent<>();
    private SingleLiveEvent<Long> goToWelcomePage = new SingleLiveEvent<>();

    //if need sharePreference pass it in constructor
    private RegisterRepository() {

    }

    public static RegisterRepository getInstance() {
        if (instance == null) {
            instance = new RegisterRepository();
        }
        return instance;
    }

    public void clearInstance() {
        Log.wtf(this.getClass().getName(), "clearInstance");
        instance = null;
    }

    public SingleLiveEvent<GoToMainFromRegister> getGoToMainPage() {
        return goToMainPage;
    }

    public SingleLiveEvent<Boolean> getLoginExistUser() {
        return loginExistUser;
    }

    public SingleLiveEvent<Long> getGoToWelcomePage() {
        return goToWelcomePage;
    }

    public int getCallingCode() {
        return callingCode;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getPattern() {
        return pattern;
    }

    public String getCountryName() {
        return countryName;
    }

    public String getRegex() {
        return regex;
    }

    public String getRegexFetchCodeVerification() {
        return regexFetchCodeVerification;
    }

    public long getUserId() {
        return userId;
    }

    public void setRegex(String regex) {
        this.regex = regex;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setForgetTwoStepVerification(boolean forgetTwoStepVerification) {
        this.forgetTwoStepVerification = forgetTwoStepVerification;
    }

    public void saveInstance(int callingCode, String pattern, String phoneNumber, String countryName, String regex) {
        this.callingCode = callingCode;
        this.pattern = pattern;
        this.phoneNumber = phoneNumber;
        this.countryName = countryName;
        this.regex = regex;
    }

    public void inRegisterMode(MutableLiveData<Boolean> hideDialogQRCode, MutableLiveData<Long> goToTwoStepVerificationPage) {
        G.onPushLoginToken = (tokenQrCode, userNameR, userIdR, authorHashR) -> {
            if (AccountManager.getInstance().isExistThisAccount(userIdR)) {
                Log.wtf(this.getClass().getName(), "Exist");
                loginExistUser.postValue(true);
            } else {
                Log.wtf(this.getClass().getName(), "not Exist");
                token = tokenQrCode;
                G.displayName = userName = userNameR;
                new HelperPreferences().putString(SHP_SETTING.FILE_NAME, SHP_SETTING.REGISTER_USERNAME, userName);
                G.userId = userId = userIdR;
                G.authorHash = authorHash = authorHashR;
                hideDialogQRCode.postValue(true);
                userLogin(token);
            }
        };

        G.onPushTwoStepVerification = (userNameR, userIdR, authorHashR) -> {
            if (AccountManager.getInstance().isExistThisAccount(userIdR)) {
                Log.wtf(this.getClass().getName(), "Exist");
                loginExistUser.postValue(true);
            } else {
                Log.wtf(this.getClass().getName(), "not Exist");
                G.displayName = userName = userNameR;
                new HelperPreferences().putString(SHP_SETTING.FILE_NAME, SHP_SETTING.REGISTER_USERNAME, userName);
                G.userId = userId = userIdR;
                G.authorHash = authorHash = authorHashR;
                goToTwoStepVerificationPage.postValue(userIdR);
            }
        };
    }

    public void getTermsOfServiceBody(RepositoryCallback<String> callback) {
        G.onReceivePageInfoTOS = new OnReceivePageInfoTOS() {
            @Override
            public void onReceivePageInfo(String bodyR) {
                callback.onSuccess(bodyR);
            }

            @Override
            public void onError(int majorCode, int minorCode) {
                //todo: fixed it and handle is Secure
                /*G.handler.postDelayed(()->new RequestInfoPage().infoPage("TOS"),2000);*/
                callback.onError();
            }
        };
        new RequestInfoPage().infoPage("TOS");
    }

    public void getInfoLocation(RepositoryCallback<LocationModel> callback) {
        new RequestInfoLocation().infoLocation(new OnReceiveInfoLocation() {
            @Override
            public void onReceive(String isoCodeR, int callingCodeR, String countryNameR, String patternR, String regexR) {
                isoCode = isoCodeR;
                callingCode = callingCodeR;
                countryName = countryNameR;
                pattern = patternR;
                regex = regexR;
                callback.onSuccess(new LocationModel(callingCode, countryName, pattern));
            }

            @Override
            public void onError(int majorCode, int minorCode) {
                callback.onError();
            }
        });
    }

    public void getCountryInfo(String countryAbbreviation, RepositoryCallback<LocationModel> callback) {
        new RequestInfoCountry().infoCountry(countryAbbreviation, new OnInfoCountryResponse() {
            @Override
            public void onInfoCountryResponse(int callingCode, String name, String pattern, String regexR) {
                regex = regexR;
                RegisterRepository.this.callingCode = callingCode;
                RegisterRepository.this.countryName = name;
                RegisterRepository.this.pattern = pattern;
                G.handler.post(() -> callback.onSuccess(new LocationModel(callingCode, name, pattern)));
            }

            @Override
            public void onError(int majorCode, int minorCode) {
                //empty
                G.handler.post(callback::onError);
            }
        });
    }

    public void registration(String phoneNumber, RepositoryCallbackWithError<ErrorWithWaitTime> callback) {
        requestRegister(phoneNumber, callback);
    }

    //basically it is send request resend activation code and send this request for getting new activation code
    public void registration(RepositoryCallbackWithError<ErrorWithWaitTime> callback) {
        // check for re-use
        if (phoneNumber == null || phoneNumber.isEmpty())
            phoneNumber = new HelperPreferences().readString(SHP_SETTING.FILE_NAME, SHP_SETTING.REGISTER_NUMBER);
        requestRegister(phoneNumber, callback);
    }

    private void requestRegister(String phoneNumber, RepositoryCallbackWithError<ErrorWithWaitTime> callback) {
        this.phoneNumber = phoneNumber.replace("-", "");
        new HelperPreferences().putString(SHP_SETTING.FILE_NAME, SHP_SETTING.REGISTER_NUMBER, this.phoneNumber);
        ProtoUserRegister.UserRegister.Builder builder = ProtoUserRegister.UserRegister.newBuilder();
        builder.setCountryCode(isoCode);
        builder.setPhoneNumber(Long.parseLong(this.phoneNumber));
        builder.setPreferenceMethodValue(ProtoUserRegister.UserRegisterResponse.Method.VERIFY_CODE_SMS.getNumber());
        builder.setRequest(ProtoRequest.Request.newBuilder().setId(HelperString.generateKey()));
        RequestWrapper requestWrapper = new RequestWrapper(100, builder, new OnUserRegistration() {
            @Override
            public void onRegister(String userNameR, long userIdR, ProtoUserRegister.UserRegisterResponse.Method methodValue, List<Long> smsNumbersR, String regex, int verifyCodeDigitCount, String authorHashR, boolean callMethodSupported) {
                if (AccountManager.getInstance().isExistThisAccount(userId)) {
                    loginExistUser.setValue(true);
                } else {
                    /*isCallMethodSupported = callMethodSupported;*/
                    //because is new ui verification code number is 5 and number not not use it more
                    /*digitCount = verifyCodeDigitCount;*/
                    regexFetchCodeVerification = regex;
                    userName = userNameR;
                    new HelperPreferences().putString(SHP_SETTING.FILE_NAME, SHP_SETTING.REGISTER_USERNAME, userName);
                    userId = userIdR;
                    authorHash = authorHashR;
                    G.smsNumbers = smsNumbersR;
                /*SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt("callingCode", callingCode);
                editor.putString("countryName", countryName);
                editor.putString("pattern", pattern);
                editor.putString("regex", regex);
                editor.apply();*/
                    callback.onSuccess();
                }
            }

            @Override
            public void onRegisterError(final int majorCode, int minorCode, int getWait) {
                G.handler.post(() -> callback.onError(new ErrorWithWaitTime(majorCode, minorCode, getWait)));
            }
        });
        try {
            RequestQueue.sendRequest(requestWrapper);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public void userLogin(String token) {
        G.onUserLogin = new OnUserLogin() {
            @Override
            public void onLogin() {
                DbManager.getInstance().doRealmTask(realm -> {
                    if (userName == null || userName.isEmpty())
                        userName = new HelperPreferences().readString(SHP_SETTING.FILE_NAME, SHP_SETTING.REGISTER_USERNAME);
                    realm.executeTransaction(realm1 -> RealmUserInfo.putOrUpdate(realm1, userId, userName, phoneNumber, token, authorHash));
                    BotInit.setCheckDrIgap(true);
                    if (newUser) {
                        goToWelcomePage.postValue(userId);
                        HelperTracker.sendTracker(HelperTracker.TRACKER_REGISTRATION_NEW_USER);
                    } else {
                        // get user info for set nick name and after from that go to ActivityMain
                        getUserInfo();
                        requestUserInfo();
                        HelperTracker.sendTracker(HelperTracker.TRACKER_REGISTRATION_USER);
                    }
                });
            }

            @Override
            public void onLoginError(int majorCode, int minorCode) {
                if (majorCode == 111 && minorCode == 4) {
                    //ToDo: handle have logOut result
                    if (new HelperLogout().logoutUser()) {

                    } else {

                    }
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
                DbManager.getInstance().doRealmTask(realm -> {
                    RealmUserInfo realmUserInfo = realm.where(RealmUserInfo.class).findFirst();
                    if (realmUserInfo != null) {
                        token = realmUserInfo.getToken();
                    }
                });
            }
            new RequestUserLogin().userLogin(token);
        } else {
            new Handler().postDelayed(this::requestLogin, 1000);
        }
    }

    public void userVerify(String verificationCode, RepositoryCallbackWithError<ErrorWithWaitTime> callback) {
        try {
            ProtoUserVerify.UserVerify.Builder userVerify = ProtoUserVerify.UserVerify.newBuilder();
            userVerify.setCode(Integer.parseInt(verificationCode
                    .replaceAll("[^0-9]", "")
                    .replaceAll("[\u0000-\u001f]", "")));
            if (userName == null || userName.isEmpty())
                userName = new HelperPreferences().readString(SHP_SETTING.FILE_NAME, SHP_SETTING.REGISTER_USERNAME);
            userVerify.setUsername(userName);
            RequestWrapper requestWrapper = new RequestWrapper(101, userVerify, new OnUserVerification() {
                @Override
                public void onUserVerify(String tokenR, boolean newUserR) {
                    newUser = newUserR;
                    token = tokenR;
                    userLogin(token);
                }

                @Override
                public void onUserVerifyError(int majorCode, int minorCode, int time) {
                    G.handler.post(() -> callback.onError(new ErrorWithWaitTime(majorCode, minorCode, time)));
                }
            });
            RequestQueue.sendRequest(requestWrapper);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private void getUserInfo() {
        G.onUserInfoResponse = new OnUserInfoResponse() {
            @Override
            public void onUserInfo(final ProtoGlobal.RegisteredUser user, String identity) {
                DbManager.getInstance().doRealmTask(realm -> {
                    realm.executeTransactionAsync(realm1 -> RealmUserInfo.putOrUpdate(realm1, user), () -> {
                        G.displayName = user.getDisplayName();
                        G.userId = user.getId();
                        G.onUserInfoResponse = null;
                        if (AccountManager.getInstance().isExistThisAccount(user.getId())) {
                            loginExistUser.setValue(true);
                        } else {
                            AccountManager.getInstance().addAccount(new AccountUser(
                                    user.getId(),
                                    null,
                                    user.getDisplayName(),
                                    String.valueOf(user.getPhone()),
                                    null,
                                    user.getInitials(),
                                    user.getColor(),
                                    0,
                                    true));
                            goToMainPage.postValue(new GoToMainFromRegister(forgetTwoStepVerification, userId));
                        }
                    });
                });

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
                DbManager.getInstance().doRealmTask(realm -> {
                    RealmUserInfo realmUserInfo = realm.where(RealmUserInfo.class).findFirst();
                    if (realmUserInfo != null) {
                        userId = realmUserInfo.getUserId();
                    }
                });
            }
            new RequestUserInfo().userInfo(userId);
        } else {
            new Handler().postDelayed(this::requestUserInfo, 1000);
        }
    }

    public void getTwoStepVerificationPasswordDetail(RepositoryCallback<UserPasswordDetail> callback) {
        new RequestUserTwoStepVerificationGetPasswordDetail().getPasswordDetail(new TwoStepVerificationGetPasswordDetail() {
            @Override
            public void getDetailPassword(String questionOne, String questionTwo, String hint, boolean hasConfirmedRecoveryEmail, String unconfirmedEmailPattern) {
                G.handler.post(() -> callback.onSuccess(new UserPasswordDetail(questionOne, questionTwo, hint, hasConfirmedRecoveryEmail, unconfirmedEmailPattern)));
            }

            @Override
            public void errorGetPasswordDetail(int majorCode, int minorCode) {

            }
        });
    }

    public void submitPassword(String password, RepositoryCallbackWithError<ErrorWithWaitTime> callback) {
        new RequestUserTwoStepVerificationVerifyPassword().verifyPassword(password, new TwoStepVerificationVerifyPassword() {
            @Override
            public void verifyPassword(String tokenR) {
                token = tokenR;
                userLogin(token);
            }

            @Override
            public void errorVerifyPassword(int major, int minor, int wait) {
                G.handler.post(() -> callback.onError(new ErrorWithWaitTime(major, minor, wait)));
            }
        });
    }

    public interface RepositoryCallback<T> {
        void onSuccess(T data);

        void onError();
    }

    public interface RepositoryCallbackWithError<T> {
        void onSuccess();

        void onError(T error);
    }
}
