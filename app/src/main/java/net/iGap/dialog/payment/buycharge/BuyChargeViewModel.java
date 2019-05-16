package net.iGap.dialog.payment.buycharge;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import net.iGap.R;
import net.iGap.dialog.payment.CompleteListener;

public class BuyChargeViewModel extends ViewModel {

    public MutableLiveData<Boolean> phoneEnableError = new MutableLiveData<>();
    public MutableLiveData<Integer> phoneErrorMessage = new MutableLiveData<>();
    public MutableLiveData<Boolean> operatorMessageEnable = new MutableLiveData<>();
    public MutableLiveData<Integer> operatorErrorMessage = new MutableLiveData<>();
    public MutableLiveData<Boolean> amountMessageEnable = new MutableLiveData<>();
    public MutableLiveData<Integer> amountErrorMessage = new MutableLiveData<>();

    private CompleteListener completeListener;

    public BuyChargeViewModel(CompleteListener completeListener) {
        this.completeListener = completeListener;
    }


    public void onActionInfoClick() {

    }

    public void onPersonIconClick() {

    }

    public void onContinueButtonClick(String phoneNumber, String operator, String amount) {
        if (phoneNumber.isEmpty()) {
            phoneEnableError.setValue(true);
            phoneErrorMessage.setValue(R.string.error);
        } else {
            phoneEnableError.setValue(false);
            phoneErrorMessage.setValue(R.string.is_empty);
            if (operator.isEmpty()) {
                operatorMessageEnable.setValue(true);
                operatorErrorMessage.setValue(R.string.error);
            } else {
                operatorMessageEnable.setValue(false);
                operatorErrorMessage.setValue(R.string.is_empty);
                if (amount.isEmpty()) {
                    amountMessageEnable.setValue(true);
                    amountErrorMessage.setValue(R.string.error);
                } else {
                    amountMessageEnable.setValue(false);
                    amountErrorMessage.setValue(R.string.is_empty);
                    completeListener.onCompleted();
                }
            }
        }
    }
}
