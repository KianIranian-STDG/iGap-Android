package net.iGap.fragments.qrCodePayment.viewModels;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import net.iGap.module.SingleLiveEvent;

public class QRCodePaymentViewModel extends ViewModel {

    private SingleLiveEvent<Boolean> mConfirmButtonClick = new SingleLiveEvent<>();

    public SingleLiveEvent<Boolean> getConfirmButtonClick() {
        return mConfirmButtonClick;
    }

    public QRCodePaymentViewModel(Context context) {
    }

    public void onConfirmButtonClick(){
        mConfirmButtonClick.setValue(true);
    }

}
