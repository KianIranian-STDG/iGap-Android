package net.iGap.mobileBank.viewmoedel;

import net.iGap.api.apiService.BaseAPIViewModel;
import net.iGap.module.SingleLiveEvent;

public class MobileBankHomeViewModel extends BaseAPIViewModel {

    public SingleLiveEvent<Boolean> onMoneyTransferListener = new SingleLiveEvent<>();
    public SingleLiveEvent<Boolean> onTempPassListener = new SingleLiveEvent<>();
    public SingleLiveEvent<Boolean> onTransactionListener = new SingleLiveEvent<>();

    public void OnTransferMoneyClicked(){
        onMoneyTransferListener.setValue(true);
    }

    public void OnTransactionsClicked() {
        onTransactionListener.setValue(true);
    }

    public void OnTemporaryPassClicked(){
        onTempPassListener.postValue(true);
    }

    public void OnShebaClicked(){}
}
