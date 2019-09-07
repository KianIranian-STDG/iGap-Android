package net.iGap.dialog.payment.charity;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import net.iGap.dialog.payment.CompleteListener;

public class CharityPaymentViewModel extends ViewModel {

    private MutableLiveData<Boolean> pinEnableError = new MutableLiveData<>();
    private MutableLiveData<Integer> pinErrorMessage = new MutableLiveData<>();
    private MutableLiveData<Boolean> cvv2ErrorEnable = new MutableLiveData<>();
    private MutableLiveData<Integer> cvv2ErrorMessage = new MutableLiveData<>();
    private CompleteListener completeListener;

    CharityPaymentViewModel(CompleteListener completeListener) {
        this.completeListener = completeListener;
    }

    public LiveData<Boolean> getPinEnableError() {
        return pinEnableError;
    }

    public LiveData<Integer> getPinErrorMessage() {
        return pinErrorMessage;
    }

    public LiveData<Boolean> getCvv2MessageEnable() {
        return cvv2ErrorEnable;
    }

    public LiveData<Integer> getCvv2ErrorMessage() {
        return cvv2ErrorMessage;
    }

    public void onActionInfoClick() {

    }

    public void onPersonIconClick() {

    }

    public void onContinueButtonClick(String pin, String cvv2) {

    }
}
