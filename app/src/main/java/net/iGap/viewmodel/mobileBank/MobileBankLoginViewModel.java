package net.iGap.viewmodel.mobileBank;


import android.util.Log;
import android.view.View;

import androidx.databinding.ObservableBoolean;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;

import net.iGap.BuildConfig;
import net.iGap.Config;
import net.iGap.R;
import net.iGap.model.mobileBank.BankAuth;
import net.iGap.model.mobileBank.BaseMobileBankResponse;
import net.iGap.model.mobileBank.LoginResponse;
import net.iGap.module.SingleLiveEvent;
import net.iGap.module.mobileBank.RSACipher;
import net.iGap.observers.interfaces.ResponseCallback;
import net.iGap.repository.MobileBankRepository;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class MobileBankLoginViewModel extends BaseMobileBankViewModel {

    private SingleLiveEvent<Integer> showErrorMessage = new SingleLiveEvent<>();
    private ObservableBoolean isEnableButton = new ObservableBoolean(true);
    private SingleLiveEvent<Boolean> onLoginResponse = new SingleLiveEvent<>();

    private MobileBankRepository repository;

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
                        if(BuildConfig.DEBUG) Log.e("bank_token", data.getData().getAccessToken());
                        repository.setAccessToken(data.getData().getAccessToken());
                        onLoginResponse.postValue(true);
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
                showErrorMessage.setValue(R.string.please_enter_your_password);
            }
        } else {
            showErrorMessage.setValue(R.string.enter_username);
        }
    }

    public MutableLiveData<Integer> getShowErrorMessage() {
        return showErrorMessage;
    }

    public ObservableBoolean getIsEnableButton() {
        return isEnableButton;
    }

    public SingleLiveEvent<Boolean> getOnLoginResponse() {
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
