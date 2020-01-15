package net.iGap.mobileBank.viewmoedel;


import android.view.View;

import androidx.databinding.ObservableBoolean;
import androidx.lifecycle.MutableLiveData;

import net.iGap.R;
import net.iGap.api.apiService.ResponseCallback;
import net.iGap.mobileBank.repository.MobileBankRepository;
import net.iGap.mobileBank.repository.model.BaseMobileBankResponse;
import net.iGap.mobileBank.repository.model.LoginResponse;
import net.iGap.module.SingleLiveEvent;

public class MobileBankLoginViewModel extends BaseMobileBankViewModel {

    private SingleLiveEvent<Integer> showErrorMessage = new SingleLiveEvent<>();
    private SingleLiveEvent<Boolean> goToMainPage = new SingleLiveEvent<>();
    private ObservableBoolean isEnableButton = new ObservableBoolean(true);

    private MobileBankRepository repository;

    public MobileBankLoginViewModel() {
        repository = new MobileBankRepository();
    }

    public void onLoginClicked(String userName, String password) {
        if (userName.length() > 0) {
            if (password.length() > 0) {
                showLoading.set(View.VISIBLE);
                isEnableButton.set(false);
                repository.mobileBankLogin(userName, password, this, new ResponseCallback<BaseMobileBankResponse<LoginResponse>>() {
                    @Override
                    public void onSuccess(BaseMobileBankResponse<LoginResponse> data) {
                        repository.setAccessToken(data.getData().getAccessToken());
                    }

                    @Override
                    public void onError(String error) {
                        showRequestErrorMessage.setValue(error);
                    }

                    @Override
                    public void onFailed() {

                    }
                });
            } else {
                showErrorMessage.setValue(R.string.error);
            }
        } else {
            showErrorMessage.setValue(R.string.error);
        }
    }

    public MutableLiveData<Integer> getShowErrorMessage() {
        return showErrorMessage;
    }

    public ObservableBoolean getIsEnableButton() {
        return isEnableButton;
    }

    public SingleLiveEvent<Boolean> getGoToMainPage() {
        return goToMainPage;
    }
}
