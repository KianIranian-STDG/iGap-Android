package net.iGap.dialog.payment.charity;

import androidx.lifecycle.ViewModel;
import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableInt;

import net.iGap.R;
import net.iGap.dialog.payment.CompleteListener;

public class CharityViewModel extends ViewModel {

    private ObservableBoolean charityNameEnableError = new ObservableBoolean(false);
    private ObservableInt charityNameErrorMessage = new ObservableInt(R.string.empty_error_message);
    private ObservableBoolean amountMessageEnable = new ObservableBoolean(false);
    private ObservableInt amountErrorMessage = new ObservableInt(R.string.empty_error_message);
    private CompleteListener completeListener;

    public CharityViewModel(CompleteListener completeListener){
        this.completeListener = completeListener;
    }

    public ObservableBoolean getCharityNameEnableError() {
        return charityNameEnableError;
    }

    public ObservableInt getCharityNameErrorMessage() {
        return charityNameErrorMessage;
    }

    public ObservableBoolean getAmountMessageEnable() {
        return amountMessageEnable;
    }

    public ObservableInt getAmountErrorMessage() {
        return amountErrorMessage;
    }

    public void onActionInfoClick() {

    }

    public void onPersonIconClick() {

    }

    public void onContinueButtonClick(String charityName, String amount) {
        if (charityName.isEmpty()) {
            charityNameEnableError.set(true);
            charityNameErrorMessage.set(R.string.error);
        } else {
            charityNameEnableError.set(false);
            charityNameErrorMessage.set(R.string.empty_error_message);
            if (amount.isEmpty()) {
                amountMessageEnable.set(true);
                amountErrorMessage.set(R.string.error);
            } else {
                amountMessageEnable.set(false);
                amountErrorMessage.set(R.string.empty_error_message);
                completeListener.onCompleted();
            }
        }
    }
}
