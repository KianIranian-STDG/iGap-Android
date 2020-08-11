package net.iGap.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class FinancialServicesViewModel extends ViewModel {

    private MutableLiveData<String> walletValue = new MutableLiveData<>();
    //ui
    public MutableLiveData<Integer> walletPointerPosition = new MutableLiveData<>();

    public LiveData<String> getWalletValue() {
        return walletValue;
    }

    public void onScanQRCodeClick() {

    }

    public void onChargeButtonClick() {

    }

    public void onCashOutClick() {

    }

    public void onAddCardButtonClick() {

    }
}
