package net.iGap.dialog.payment.buycharge;

import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableInt;
import androidx.lifecycle.ViewModel;

import net.iGap.R;
import net.iGap.dialog.payment.CompleteListener;

public class BuyChargeViewModel extends ViewModel {

    public ObservableBoolean phoneEnableError = new ObservableBoolean(false);
    public ObservableInt phoneErrorMessage = new ObservableInt(R.string.empty_error_message);
    public ObservableBoolean operatorMessageEnable = new ObservableBoolean(false);
    public ObservableInt operatorErrorMessage = new ObservableInt(R.string.empty_error_message);
    public ObservableBoolean amountMessageEnable = new ObservableBoolean(false);
    public ObservableInt amountErrorMessage = new ObservableInt(R.string.empty_error_message);

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
            phoneEnableError.set(true);
            phoneErrorMessage.set(R.string.error);
        } else {
            phoneEnableError.set(false);
            phoneErrorMessage.set(R.string.empty_error_message);
            if (operator.isEmpty()) {
                operatorMessageEnable.set(true);
                operatorErrorMessage.set(R.string.error);
            } else {
                operatorMessageEnable.set(false);
                operatorErrorMessage.set(R.string.empty_error_message);
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
}
