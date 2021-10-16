package net.iGap.fragments.qrCodePayment.viewModels;

import android.content.Context;

import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import net.iGap.module.SingleLiveEvent;

import io.reactivex.Single;

public class ScanCodeQRCodePaymentViewModel extends ViewModel {

    private Context mContext;
    private SingleLiveEvent<Boolean> mManuallyEnterButtonClick = new SingleLiveEvent<>();

    public SingleLiveEvent<Boolean> getManuallyEnterButtonClick() {
        return mManuallyEnterButtonClick;
    }

    public ScanCodeQRCodePaymentViewModel(Context context) {
        mContext = context;
    }

    public void onManuallyEnterButtonClick(){
        mManuallyEnterButtonClick.setValue(true);
    }
}
