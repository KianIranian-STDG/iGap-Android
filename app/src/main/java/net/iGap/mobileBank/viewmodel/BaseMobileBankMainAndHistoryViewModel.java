package net.iGap.mobileBank.viewmodel;

import androidx.lifecycle.MutableLiveData;

import net.iGap.api.apiService.ResponseCallback;
import net.iGap.api.errorhandler.ErrorModel;
import net.iGap.mobileBank.repository.MobileBankRepository;
import net.iGap.mobileBank.repository.model.BankShebaModel;
import net.iGap.mobileBank.repository.model.BaseMobileBankResponse;
import net.iGap.mobileBank.view.MobileBankHomeTabFragment;

import java.util.ArrayList;
import java.util.List;

public class BaseMobileBankMainAndHistoryViewModel extends BaseMobileBankViewModel {

    private MutableLiveData<List<String>> shebaListener = new MutableLiveData<>();
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

    public void getShebaNumber(String cardNumber) {
        if (cardNumber == null) {
            shebaListener.postValue(null);
            return;
        }
        MobileBankRepository.getInstance().getShebaNumber(cardNumber, this, new ResponseCallback<BaseMobileBankResponse<List<String>>>() {
            @Override
            public void onSuccess(BaseMobileBankResponse<List<String>> data) {
                shebaListener.postValue(data.getData());
            }

            @Override
            public void onError(String error) {
                shebaListener.postValue(null);
                showRequestErrorMessage.setValue(error);
            }

            @Override
            public void onFailed() {
                shebaListener.postValue(null);
            }
        });
    }

    public void getShebaNumberByDeposit(String deposit) {
        if (deposit == null) {
            shebaListener.postValue(null);
            return;
        }
        MobileBankRepository.getInstance().getShebaNumberByDeposit(deposit, this, new ResponseCallback<BaseMobileBankResponse<BankShebaModel>>() {
            @Override
            public void onSuccess(BaseMobileBankResponse<BankShebaModel> data) {
                List<String> shebaList = new ArrayList<>();
                shebaList.add(data.getData().getSheba());
                shebaListener.postValue(shebaList);
            }

            @Override
            public void onError(String error) {
                showRequestErrorMessage.setValue(error);
                shebaListener.postValue(null);
            }

            @Override
            public void onFailed() {
                shebaListener.postValue(null);
            }
        });
    }

    public MutableLiveData<List<String>> getShebaListener() {
        return shebaListener;
    }

    public MutableLiveData<String> getOTPmessage() {
        return OTPmessage;
    }
}
