package net.iGap.viewmodel.mobileBank;

import androidx.lifecycle.MutableLiveData;

import net.iGap.observers.interfaces.ResponseCallback;
import net.iGap.api.errorhandler.ErrorModel;
import net.iGap.repository.MobileBankRepository;

public class BaseMobileBankMainAndHistoryViewModel extends BaseMobileBankViewModel {

    private MutableLiveData<String> OTPmessage = new MutableLiveData<>();

    public void getOTP(String cardNumber) {
        MobileBankRepository.getInstance().getOTP(cardNumber, this, new ResponseCallback<ErrorModel>() {
            @Override
            public void onSuccess(ErrorModel data) {
                OTPmessage.setValue(data.getMessage());
            }

            @Override
            public void onError(String error) {
                OTPmessage.setValue("-1");
                showRequestErrorMessage.setValue(error);
            }

            @Override
            public void onFailed() {
                OTPmessage.setValue("-1");
            }
        });
    }

    public MutableLiveData<String> getOTPmessage() {
        return OTPmessage;
    }
}
