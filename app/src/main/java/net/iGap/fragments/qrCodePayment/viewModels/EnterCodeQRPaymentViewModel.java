package net.iGap.fragments.qrCodePayment.viewModels;

import androidx.lifecycle.ViewModel;

import net.iGap.module.SingleLiveEvent;

public class EnterCodeQRPaymentViewModel extends ViewModel {

    private SingleLiveEvent<Boolean> mCodeRegistrationClicked = new SingleLiveEvent<>();

    public SingleLiveEvent<Boolean> getCodeRegistrationClicked() {
        return mCodeRegistrationClicked;
    }

    public void onCodeRegistrationClick(){
        mCodeRegistrationClicked.setValue(true);
    }
}
