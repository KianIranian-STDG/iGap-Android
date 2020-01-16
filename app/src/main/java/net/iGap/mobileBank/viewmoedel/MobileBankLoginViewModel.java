package net.iGap.mobileBank.viewmoedel;


import android.util.Log;
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
    private ObservableBoolean isEnableButton = new ObservableBoolean(true);
    private SingleLiveEvent<String> onLoginResponse = new SingleLiveEvent<>();

    private MobileBankRepository repository;

    public MobileBankLoginViewModel() {
        repository = MobileBankRepository.getInstance();
    }

    public void onLoginClicked(String userName, String password) {
        if (userName.length() > 0) {
            if (password.length() > 0) {
                setLoaderState(true);
                repository.mobileBankLogin(userName, password, this, new ResponseCallback<BaseMobileBankResponse<LoginResponse>>() {
                    @Override
                    public void onSuccess(BaseMobileBankResponse<LoginResponse> data) {
                        setLoaderState(false);
                        repository.setAccessToken(data.getData().getAccessToken());
                        onLoginResponse.postValue(data.getData().getName());
                    }

                    @Override
                    public void onError(String error) {
                        setLoaderState(false);
                        showRequestErrorMessage.setValue(error);
                    }

                    @Override
                    public void onFailed() {
                        setLoaderState(false);
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

    public SingleLiveEvent<String> getOnLoginResponse() {
        return onLoginResponse;
    }

    private void setLoaderState(boolean state){
        if (state){
            showLoading.set(View.VISIBLE);
            isEnableButton.set(false);
        }else {
            showLoading.set(View.GONE);
            isEnableButton.set(true);
        }
    }
}
