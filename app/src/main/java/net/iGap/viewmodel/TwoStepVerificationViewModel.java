package net.iGap.viewmodel;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;
import android.view.View;

import net.iGap.R;
import net.iGap.model.SecurityRecoveryModel;
import net.iGap.model.UserPasswordDetail;
import net.iGap.model.repository.ErrorWithWaitTime;
import net.iGap.model.repository.RegisterRepository;
import net.iGap.module.enums.Security;

public class TwoStepVerificationViewModel extends ViewModel {

    //view callback
    public MutableLiveData<Integer> showErrorMessage = new MutableLiveData<>();
    public MutableLiveData<Long> showDialogWaitTime = new MutableLiveData<>();
    public MutableLiveData<Boolean> isHideKeyword = new MutableLiveData<>();
    public MutableLiveData<Integer> showDialogForgotPassword = new MutableLiveData<>();
    public MutableLiveData<SecurityRecoveryModel> goToSecurityRecoveryPage = new MutableLiveData<>();

    private ObservableField<String> passwordHint = new ObservableField<>("");
    private ObservableField<String> password = new ObservableField<>("");
    private ObservableInt isShowLoading = new ObservableInt(View.VISIBLE);

    private RegisterRepository repository;
    private boolean isRecoveryByEmail = false;
    private boolean isConfirmedRecoveryEmail;
    private String securityPasswordQuestionOne = "";
    private String securityPasswordQuestionTwo = "";
    private String securityPaternEmail = "";
    private boolean forgetPassword = false;

    public TwoStepVerificationViewModel(RegisterRepository repository) {
        this.repository = repository;

        this.repository.getTwoStepVerificationPasswordDetail(new RegisterRepository.RepositoryCallback<UserPasswordDetail>() {
            @Override
            public void onSuccess(UserPasswordDetail data) {
                passwordHint.set(data.getHint());
                isShowLoading.set(View.GONE);
                securityPasswordQuestionOne = data.getQuestionOne();
                securityPasswordQuestionTwo = data.getQuestionTwo();
                isConfirmedRecoveryEmail = data.isHasConfirmedRecoveryEmail();
                String unconfirmedEmailPattern1 = data.getUnconfirmedEmailPattern();
            }

            @Override
            public void onError() {
                isShowLoading.set(View.GONE);
                showErrorMessage.setValue(R.string.error);
            }
        });
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
            isHideKeyword.setValue(true);
            repository.submitPassword(password.get(), new RegisterRepository.RepositoryCallbackWithError<ErrorWithWaitTime>() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError(ErrorWithWaitTime error) {
                    if (error.getMajorCode() == 191) {
                        isShowLoading.set(View.GONE);
                        showDialogWaitTime.setValue((long) error.getWaitTime());
                    } else if (error.getMajorCode() == 194 && error.getMinorCode() == 1) {
                        isHideKeyword.setValue(true);
                        isShowLoading.set(View.GONE);
                    }
                }
            });
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
}