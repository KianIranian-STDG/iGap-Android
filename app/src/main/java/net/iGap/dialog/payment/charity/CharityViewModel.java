package net.iGap.dialog.payment.charity;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import net.iGap.R;
import net.iGap.dialog.payment.CompleteListener;

public class CharityViewModel extends ViewModel {

    private MutableLiveData<Boolean> charityNameEnableError;
    private MutableLiveData<Integer> charityNameErrorMessage;
    private MutableLiveData<Boolean> amountMessageEnable;
    private MutableLiveData<Integer> amountErrorMessage;
    private CompleteListener completeListener;

    public CharityViewModel(CompleteListener completeListener){
        this.completeListener = completeListener;
    }

    public LiveData<Boolean> getCharityNameEnableError() {
        return charityNameEnableError;
    }

    public LiveData<Integer> getCharityNameErrorMessage() {
        return charityNameErrorMessage;
    }

    public LiveData<Boolean> getAmountMessageEnable() {
        return amountMessageEnable;
    }

    public LiveData<Integer> getAmountErrorMessage() {
        return amountErrorMessage;
    }

    public void onActionInfoClick() {

    }

    public void onPersonIconClick() {

    }

    public void onContinueButtonClick(String charityName, String amount) {
        if (charityName.isEmpty()) {
            charityNameEnableError.setValue(true);
            charityNameErrorMessage.setValue(R.string.error);
        } else {
            charityNameEnableError.setValue(false);
            charityNameErrorMessage.setValue(R.string.is_empty);
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
