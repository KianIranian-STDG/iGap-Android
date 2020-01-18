package net.iGap.mobileBank.viewmoedel;


import android.view.View;

import androidx.databinding.ObservableBoolean;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;

import net.iGap.Config;
import net.iGap.R;
import net.iGap.api.apiService.ResponseCallback;
import net.iGap.mobileBank.repository.MobileBankRepository;
import net.iGap.mobileBank.repository.model.BankAuth;
import net.iGap.mobileBank.repository.model.BaseMobileBankResponse;
import net.iGap.mobileBank.repository.model.LoginResponse;
import net.iGap.mobileBank.repository.util.RSACipher;
import net.iGap.module.SingleLiveEvent;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class MobileBankLoginViewModel extends BaseMobileBankViewModel {

    private SingleLiveEvent<Integer> showErrorMessage = new SingleLiveEvent<>();
    private ObservableBoolean isEnableButton = new ObservableBoolean(true);
    private SingleLiveEvent<String> onLoginResponse = new SingleLiveEvent<>();

    private MobileBankRepository repository;
    private static final String TAG = "MobileBankLoginViewMode";

    public MobileBankLoginViewModel() {
        repository = MobileBankRepository.getInstance();
    }

    public void onLoginClicked(String userName, String password) {
        if (userName.length() > 0) {
            if (password.length() > 0) {
                setLoaderState(true);
                String tempAuth = null;
                BankAuth auth = new BankAuth(userName, password);
                try {
                    RSACipher cipher = new RSACipher();
                    tempAuth = cipher.encrypt(new Gson().toJson(auth), RSACipher.stringToPublicKey(Config.PUBLIC_PARSIAN_KEY_CLIENT));
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                    showRequestErrorMessage.setValue("Bad Encryption");
                    return;
                } catch (NoSuchPaddingException e) {
                    e.printStackTrace();
                    showRequestErrorMessage.setValue("Bad Encryption");
                    return;
                } catch (InvalidKeyException e) {
                    e.printStackTrace();
                    showRequestErrorMessage.setValue("Bad Encryption");
                    return;
                } catch (IllegalBlockSizeException e) {
                    e.printStackTrace();
                    showRequestErrorMessage.setValue("Bad Encryption");
                    return;
                } catch (BadPaddingException e) {
                    e.printStackTrace();
                    showRequestErrorMessage.setValue("Bad Encryption");
                    return;
                }
                repository.mobileBankLogin(tempAuth, this, new ResponseCallback<BaseMobileBankResponse<LoginResponse>>() {
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

    private void setLoaderState(boolean state) {
        if (state) {
            showLoading.set(View.VISIBLE);
            isEnableButton.set(false);
        } else {
            showLoading.set(View.GONE);
            isEnableButton.set(true);
        }
    }
}
