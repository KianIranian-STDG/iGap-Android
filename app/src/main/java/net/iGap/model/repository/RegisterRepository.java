package net.iGap.model.repository;

import android.os.Handler;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import net.iGap.BuildConfig;
import net.iGap.G;
import net.iGap.WebSocketClient;
import net.iGap.helper.HelperString;
import net.iGap.helper.HelperTracker;
import net.iGap.model.GoToMainFromRegister;
import net.iGap.model.LocationModel;
import net.iGap.model.UserPasswordDetail;
import net.iGap.module.BotInit;
import net.iGap.module.SingleLiveEvent;
import net.iGap.module.accountManager.AccountManager;
import net.iGap.module.accountManager.DbManager;
import net.iGap.observers.interfaces.OnInfoCountryResponse;
import net.iGap.observers.interfaces.OnReceiveInfoLocation;
import net.iGap.observers.interfaces.OnUserInfoResponse;
import net.iGap.observers.interfaces.OnUserLogin;
import net.iGap.observers.interfaces.OnUserProfileSetNickNameResponse;
import net.iGap.observers.interfaces.OnUserProfileSetRepresentative;
import net.iGap.observers.interfaces.OnUserRegistration;
import net.iGap.observers.interfaces.OnUserVerification;
import net.iGap.observers.interfaces.TwoStepVerificationGetPasswordDetail;
import net.iGap.observers.interfaces.TwoStepVerificationVerifyPassword;
import net.iGap.proto.ProtoGlobal;
import net.iGap.proto.ProtoRequest;
import net.iGap.proto.ProtoUserRegister;
import net.iGap.proto.ProtoUserVerify;
import net.iGap.realm.RealmAvatar;
import net.iGap.realm.RealmUserInfo;
import net.iGap.request.RequestInfoCountry;
import net.iGap.request.RequestInfoLocation;
import net.iGap.request.RequestInfoPage;
import net.iGap.request.RequestQueue;
import net.iGap.request.RequestUserInfo;
import net.iGap.request.RequestUserLogin;
import net.iGap.request.RequestUserProfileSetNickname;
import net.iGap.request.RequestUserProfileSetRepresentative;
import net.iGap.request.RequestUserTwoStepVerificationGetPasswordDetail;
import net.iGap.request.RequestUserTwoStepVerificationVerifyPassword;
import net.iGap.request.RequestWrapper;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class RegisterRepository {

    private static RegisterRepository instance;

    private String token;
    private String phoneNumber;
    private String userName;
    private String authorHash;
    private long userId;
    private boolean newUser;
    private String regex = "^9\\d{9}$";
    private int callingCode;
    private String isoCode = "IR";
    private String countryName = "";
    private String pattern = "";
    private int infoRetryCount;
    private String regexFetchCodeVerification;
    private boolean forgetTwoStepVerification = false;
    private ProtoUserRegister.UserRegisterResponse.Method method;
    private String countryCode;
    private long resendDelayTime;
    private SingleLiveEvent<GoToMainFromRegister> goToMainPage = new SingleLiveEvent<>();
    private SingleLiveEvent<Long> goToSyncContactPageForNewUser = new SingleLiveEvent<>();
    private SingleLiveEvent<Boolean> loginExistUser = new SingleLiveEvent<>();
    private SingleLiveEvent<Long> goToWelcomePage = new SingleLiveEvent<>();

    //if need sharePreference pass it in constructor
    private RegisterRepository() {
        getInfoLocation();
    }

    public synchronized static RegisterRepository getInstance() {
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

    public SingleLiveEvent<Long> getGoToSyncContactPageForNewUser() {
        return goToSyncContactPageForNewUser;
    }

    public long getResendDelayTime() {
        if (resendDelayTime <= 0) {
            resendDelayTime = 60;
        }
        return resendDelayTime;
    }

    public void setResendDelayTime(long resendDelayTime) {
        this.resendDelayTime = resendDelayTime;
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

    public void setIsoCode(String isoCode) {
        this.isoCode = isoCode;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public ProtoUserRegister.UserRegisterResponse.Method getMethod() {
        return method;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public void setForgetTwoStepVerification(boolean forgetTwoStepVerification) {
        this.forgetTwoStepVerification = forgetTwoStepVerification;
    }

    public void removeUserAvatar() {
        DbManager.getInstance().doRealmTask(realm -> {
            RealmUserInfo realmUserInfo = realm.where(RealmUserInfo.class).findFirst();
            if (realmUserInfo != null) {
                RealmAvatar.deleteAvatarWithOwnerId(userId);
            }
        });
    }

    public void inRegisterMode(MutableLiveData<Boolean> hideDialogQRCode, MutableLiveData<Long> goToTwoStepVerificationPage) {
        G.onPushLoginToken = (tokenQrCode, userNameR, userIdR, authorHashR) -> {
            if (AccountManager.getInstance().isExistThisAccount(userIdR)) {
                Log.wtf(this.getClass().getName(), "Exist");
                loginExistUser.postValue(true);
            } else {
                Log.wtf(this.getClass().getName(), "not Exist");
                token = tokenQrCode;
                userName = userNameR;
                userId = userIdR;
                authorHash = authorHashR;
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
                userName = userNameR;
                userId = userIdR;
                authorHash = authorHashR;
                goToTwoStepVerificationPage.postValue(userIdR);
            }
        };
    }

    public void getTermsOfServiceBody(RepositoryCallback<String> callback) {
        new RequestInfoPage().infoPageAgreementDiscovery(BuildConfig.TOS, new RequestInfoPage.OnInfoPage() {
            @Override
            public void onInfo(String body) {
                callback.onSuccess(body);
            }

            @Override
            public void onError(int major, int minor) {
                callback.onError();
            }
        });
    }

    public void getInfoLocation() {
        new RequestInfoLocation().infoLocation(new OnReceiveInfoLocation() {
            @Override
            public void onReceive(String isoCodeR, int callingCodeR, String countryNameR, String patternR, String regexR) {
                isoCode = isoCodeR;
                callingCode = callingCodeR;
                countryName = countryNameR;
                pattern = patternR;
                regex = regexR;
            }

            @Override
            public void onError(int majorCode, int minorCode) {
                if (infoRetryCount < 3) {
                    infoRetryCount++;
                    getInfoLocation();
                }
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
                callback.onSuccess(new LocationModel(callingCode, name, pattern));
            }

            @Override
            public void onError(int majorCode, int minorCode) {
                callback.onError();
            }
        });
    }

    public void registration(String phoneNumber, RepositoryCallbackWithError<ErrorWithWaitTime> callback) {
        requestRegister(phoneNumber, callback);
    }

    //basically it is send request resend activation code and send this request for getting new activation code
    public void registration(RepositoryCallbackWithError<ErrorWithWaitTime> callback) {
        // check for re-use
        requestRegister(phoneNumber, callback);
    }

    private void requestRegister(String phoneNumber, RepositoryCallbackWithError<ErrorWithWaitTime> callback) {
        if (phoneNumber == null)
            return;
        this.phoneNumber = phoneNumber.replace("-", "");
        ProtoUserRegister.UserRegister.Builder builder = ProtoUserRegister.UserRegister.newBuilder();
        builder.setCountryCode(isoCode);
        builder.setPhoneNumber(Long.parseLong(this.phoneNumber));
        builder.setPreferenceMethodValue(ProtoUserRegister.UserRegisterResponse.Method.VERIFY_CODE_SMS.getNumber());
        builder.setRequest(ProtoRequest.Request.newBuilder().setId(HelperString.generateKey()));
        builder.setAppId(BuildConfig.APP_ID);
        RequestWrapper requestWrapper = new RequestWrapper(100, builder, new OnUserRegistration() {
            @Override
            public void onRegister(String userNameR, long userIdR, ProtoUserRegister.UserRegisterResponse.Method methodValue, List<Long> smsNumbersR, String regex, int verifyCodeDigitCount, String authorHashR, boolean callMethodSupported, long resendCodeDelay) {
                /*isCallMethodSupported = callMethodSupported;*/
                //because is new ui verification code number is 5 and number not not use it more
                /*digitCount = verifyCodeDigitCount;*/
                regexFetchCodeVerification = regex;
                userName = userNameR;
                userId = userIdR;
                authorHash = authorHashR;
                G.smsNumbers = smsNumbersR;
                method = methodValue;
                resendDelayTime = resendCodeDelay;
                callback.onSuccess();
            }

            @Override
            public void onRegisterError(int majorCode, int minorCode, int getWait) {
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
                G.onUserLogin = null;
                DbManager.getInstance().doRealmTask(realm -> {

                    AccountManager.getInstance().addNewUser(userId, phoneNumber);

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
                if (majorCode == 5 && minorCode == 1) {
                    requestLogin();
                }
            }
        };
        requestLogin();
    }

    public void setNickName(String name, String lastName, String countryCode, String reagentPhoneNumber, RepositoryCallbackWithError<ErrorWithWaitTime> callback) {
        new RequestUserProfileSetNickname().userProfileNickName(name + " " + lastName, new OnUserProfileSetNickNameResponse() {
            @Override
            public void onUserProfileNickNameResponse(final String nickName, String initials) {
                if ((reagentPhoneNumber == null || reagentPhoneNumber.isEmpty())) {
                    getUserInfo();
                    requestUserInfo();
                } else {
                    setReagent(countryCode + reagentPhoneNumber, callback);
                }
            }

            @Override
            public void onUserProfileNickNameError(int majorCode, int minorCode) {
                callback.onError(null);
            }

            @Override
            public void onUserProfileNickNameTimeOut() {
                callback.onError(null);
            }
        });
    }

    private void requestLogin() {
        if (WebSocketClient.getInstance().isConnect()) {
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

    public void userVerify(@NotNull String verificationCode, RepositoryCallbackWithError<ErrorWithWaitTime> callback) {
        try {
            ProtoUserVerify.UserVerify.Builder userVerify = ProtoUserVerify.UserVerify.newBuilder();
            userVerify.setCode(Integer.parseInt(verificationCode
                    .replaceAll("[^0-9]", "")
                    .replaceAll("[\u0000-\u001f]", "")));
            if (userName != null)
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
                    callback.onError(new ErrorWithWaitTime(majorCode, minorCode, time));
                }
            });
            RequestQueue.sendRequest(requestWrapper);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private void getUserInfo() {
        Log.wtf(this.getClass().getName(), "getUserInfo");
        G.onUserInfoResponse = new OnUserInfoResponse() {
            @Override
            public void onUserInfo(final ProtoGlobal.RegisteredUser user, String identity) {
                if (user.getId() == userId) {
                    AccountManager.getInstance().updateCurrentNickName(user.getDisplayName());
                    AccountManager.getInstance().updatePhoneNumber(String.valueOf(user.getPhone()));
                    DbManager.getInstance().doRealmTask(realm -> {
                        realm.executeTransactionAsync(realm1 -> RealmUserInfo.putOrUpdate(realm1, user), () -> G.onUserInfoResponse = null);
                    });
                    if (newUser) {
                        goToSyncContactPageForNewUser.postValue(userId);
                    } else {
                        goToMainPage.postValue(new GoToMainFromRegister(forgetTwoStepVerification, userId));
                    }
                }

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

    private void setReagent(String reagentPhoneNumber, RepositoryCallbackWithError<ErrorWithWaitTime> callback) {
        new RequestUserProfileSetRepresentative().userProfileSetRepresentative(
                reagentPhoneNumber,
                new OnUserProfileSetRepresentative() {
                    @Override
                    public void onSetRepresentative(String phone) {
                        DbManager.getInstance().doRealmTask(realm -> {
                            RealmUserInfo realmUserInfo = realm.where(RealmUserInfo.class).findFirst();
                            RealmUserInfo.setRepresentPhoneNumber(realm, realmUserInfo, phone);
                        });
                        getUserInfo();
                        requestUserInfo();
                    }

                    @Override
                    public void onErrorSetRepresentative(int majorCode, int minorCode) {
                        callback.onError(new ErrorWithWaitTime(majorCode, minorCode, 0));
                    }
                });
    }

    private void requestUserInfo() {
        if (WebSocketClient.getInstance().isConnect()) {
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
                callback.onSuccess(new UserPasswordDetail(questionOne, questionTwo, hint, hasConfirmedRecoveryEmail, unconfirmedEmailPattern));
            }

            @Override
            public void errorGetPasswordDetail(int majorCode, int minorCode) {
                callback.onError();
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
                callback.onError(new ErrorWithWaitTime(major, minor, wait));
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
