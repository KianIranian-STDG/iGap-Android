package net.iGap.viewmodel;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;
import android.os.Handler;
import android.view.View;

import net.iGap.G;
import net.iGap.R;
import net.iGap.helper.HelperLogout;
import net.iGap.interfaces.OnRecoverySecurityPassword;
import net.iGap.interfaces.OnSecurityCheckPassword;
import net.iGap.interfaces.OnUserInfoResponse;
import net.iGap.interfaces.OnUserLogin;
import net.iGap.model.SecurityRecoveryModel;
import net.iGap.module.enums.Security;
import net.iGap.proto.ProtoGlobal;
import net.iGap.realm.RealmUserInfo;
import net.iGap.request.RequestUserInfo;
import net.iGap.request.RequestUserLogin;
import net.iGap.request.RequestUserTwoStepVerificationGetPasswordDetail;
import net.iGap.request.RequestUserTwoStepVerificationVerifyPassword;

import io.realm.Realm;

public class TwoStepVerificationViewModel extends ViewModel {

    //view callback
    public MutableLiveData<Integer> showErrorMessage = new MutableLiveData<>();
    public MutableLiveData<Long> showDialogWaitTime = new MutableLiveData<>();
    public MutableLiveData<Boolean> isHideKeyword = new MutableLiveData<>();
    public MutableLiveData<Boolean> goToMainPage = new MutableLiveData<>();
    public MutableLiveData<Integer> showDialogForgotPassword = new MutableLiveData<>();
    public MutableLiveData<SecurityRecoveryModel> goToSecurityRecoveryPage = new MutableLiveData<>();

    private ObservableField<String> passwordHint = new ObservableField<>("");
    private ObservableField<String> password = new ObservableField<>("");
    private ObservableInt isShowLoading = new ObservableInt(View.VISIBLE);

    private boolean isRecoveryByEmail = false;
    private boolean isConfirmedRecoveryEmail;
    private String securityPasswordQuestionOne = "";
    private String securityPasswordQuestionTwo = "";
    private String securityPaternEmail = "";
    private String token;
    public long userId;
    public String userName;
    public String authorHash;
    private boolean forgetPassword = false;

    public TwoStepVerificationViewModel(long userId) {
        this.userId = userId;
        G.onSecurityCheckPassword = new OnSecurityCheckPassword() {
            @Override
            public void getDetailPassword(String questionOne, String questionTwo, String hint, boolean hasConfirmedRecoveryEmail, String unconfirmedEmailPattern) {
                G.handler.post(() -> {
                    passwordHint.set(hint);
                    isShowLoading.set(View.GONE);
                });
                securityPasswordQuestionOne = questionOne;
                securityPasswordQuestionTwo = questionTwo;
                isConfirmedRecoveryEmail = hasConfirmedRecoveryEmail;
                String unconfirmedEmailPattern1 = unconfirmedEmailPattern;
            }

            @Override
            public void verifyPassword(String tokenR) {
                token = tokenR;
                G.handler.post(() -> {
                    isShowLoading.set(View.GONE);
                    isHideKeyword.setValue(true);
                    userLogin(token);
                });
            }

            @Override
            public void errorVerifyPassword(int wait) {
                G.handler.post(() -> {
                    isShowLoading.set(View.GONE);
                    showDialogWaitTime.setValue((long) wait);
                });
            }

            @Override
            public void errorInvalidPassword() {
                G.handler.post(() -> {
                    isHideKeyword.setValue(true);
                    isShowLoading.set(View.GONE);
                });
            }
        };
        G.onRecoverySecurityPassword = new OnRecoverySecurityPassword() {
            @Override
            public void recoveryByEmail(String tokenR) {
                G.handler.post(() -> {
                    isShowLoading.set(View.GONE);
                    token = tokenR;
                    forgetPassword = true;
                    userLogin(token);
                });
            }

            @Override
            public void getEmailPatern(String patern) {

            }

            @Override
            public void errorRecoveryByEmail() {
                G.handler.post(() -> {
                    isShowLoading.set(View.GONE);
                    isHideKeyword.setValue(true);
                });
            }

            @Override
            public void recoveryByQuestion(String tokenR) {
                G.handler.post(() -> {
                    isShowLoading.set(View.GONE);
                    token = tokenR;
                    userLogin(token);
                });
            }

            @Override
            public void errorRecoveryByQuestion() {
                G.handler.post(() -> {
                    isShowLoading.set(View.GONE);
                    isHideKeyword.setValue(true);
                });
            }
        };
        new RequestUserTwoStepVerificationGetPasswordDetail().getPasswordDetail();
    }

    public ObservableField<String> getPassword() {
        return password;
    }

    public ObservableField<String> getPasswordHint() {
        return passwordHint;
    }

    public ObservableInt getIsShowLoading() {
        return isShowLoading;
    }

    public void onSubmitPasswordClick() {
        if (password.get().length() > 0) {
            isShowLoading.set(View.VISIBLE);
            new RequestUserTwoStepVerificationVerifyPassword().verifyPassword(password.get());
        } else {
            showErrorMessage.setValue(R.string.please_enter_code);
        }
    }

    public void onClickForgotPassword() {
        int item;
        if (isConfirmedRecoveryEmail) {
            item = R.array.securityRecoveryPassword;
        } else {
            item = R.array.securityRecoveryPasswordWithoutEmail;
        }
        showDialogForgotPassword.setValue(item);
    }

    public void selectedRecoveryType(boolean isRecoveryByEmail) {
        this.isRecoveryByEmail = isRecoveryByEmail;
        goToSecurityRecoveryPage.setValue(new SecurityRecoveryModel(Security.REGISTER, securityPasswordQuestionOne, securityPasswordQuestionTwo, securityPaternEmail, isRecoveryByEmail, isConfirmedRecoveryEmail));
    }

    private void userLogin(final String token) {
        G.onUserLogin = new OnUserLogin() {
            @Override
            public void onLogin() {
                Realm realm = Realm.getDefaultInstance();
                realm.executeTransaction(realm1 -> RealmUserInfo.putOrUpdate(realm1, userId, userName, ""/*phoneNumber*/, token, authorHash));
                //newUser state is set when user verify is call with phone and activation code
                if (false/*newUser*/) {
                    /*G.handler.post(() -> goToWelcomePage.setValue(userId));*/
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
                if (realmUserInfo != null) {
                    token = realmUserInfo.getToken();
                }
                realm.close();
            }
            new RequestUserLogin().userLogin(token);
        } else {
            new Handler().postDelayed(this::requestLogin, 1000);
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
                        goToMainPage.setValue(!forgetPassword);
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
                if (realmUserInfo != null) {
                    userId = realmUserInfo.getUserId();
                }
                realm.close();
            }
            new RequestUserInfo().userInfo(userId);
        } else {
            G.handler.postDelayed(this::requestUserInfo, 1000);
        }
    }
}
