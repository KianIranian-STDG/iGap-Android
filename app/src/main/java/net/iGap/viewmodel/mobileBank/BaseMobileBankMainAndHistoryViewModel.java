package net.iGap.viewmodel.mobileBank;

import androidx.lifecycle.MutableLiveData;

import net.iGap.model.mobileBank.BankCardDepositsModel;
import net.iGap.model.mobileBank.BaseMobileBankResponse;
import net.iGap.module.SingleLiveEvent;
import net.iGap.observers.interfaces.ResponseCallback;
import net.iGap.api.errorhandler.ErrorModel;
import net.iGap.repository.MobileBankRepository;

import java.util.List;

public class BaseMobileBankMainAndHistoryViewModel extends BaseMobileBankViewModel {

    private MutableLiveData<String> OTPmessage = new MutableLiveData<>();
    private SingleLiveEvent<String> cardDepositResponse =new SingleLiveEvent<>();

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

    public void getCardDeposits(String card){
        MobileBankRepository.getInstance().getCardDeposits(card , this , new ResponseCallback<BaseMobileBankResponse<List<BankCardDepositsModel>>>() {
            @Override
            public void onSuccess(BaseMobileBankResponse<List<BankCardDepositsModel>> data) {
                for (int i = 0 ; i< data.getData().size() ; i++){
                    if(data.getData().get(i).getType().equals("MAIN")) { //return just main deposit until 13 esfand 98
                        cardDepositResponse.setValue(data.getData().get(i).getDeposit());
                        return;
                    }
                }
                cardDepositResponse.setValue("-1");
            }

            @Override
            public void onError(String error) {
                showRequestErrorMessage.setValue(error);
                cardDepositResponse.setValue("-1");
            }

            @Override
            public void onFailed() {
                cardDepositResponse.setValue("-1");
            }
        });
    }

    public MutableLiveData<String> getOTPmessage() {
        return OTPmessage;
    }

    public SingleLiveEvent<String> getCardDepositResponse() {
        return cardDepositResponse;
    }
}
