package net.iGap.dialog.payment.paybills;

import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableInt;
import androidx.lifecycle.ViewModel;

import net.iGap.R;
import net.iGap.dialog.payment.CompleteListener;

public class PayBillsViewModel extends ViewModel {

    public ObservableBoolean mobileBillsModeIsSelected = new ObservableBoolean(false);
    public ObservableInt phoneOrId = new ObservableInt();
    public ObservableBoolean phoneOrIdEnableError = new ObservableBoolean(false);
    public ObservableInt phoneOrIdErrorMessage = new ObservableInt(R.string.empty_error_message);
    public ObservableInt operatorOrId = new ObservableInt();
    public ObservableBoolean operatorOrIdMessageEnable = new ObservableBoolean(false);
    public ObservableInt operatorOrIdErrorMessage = new ObservableInt(R.string.empty_error_message);

    private CompleteListener completeListener;

    public PayBillsViewModel(CompleteListener completeListener) {
        this.completeListener = completeListener;
    }

    public void onActionInfoClick() {

    }

    public void onMobileBillsClick() {
        if (!mobileBillsModeIsSelected.get()) {
            mobileBillsModeIsSelected.set(true);
            phoneOrId.set(R.string.phone_number);
            operatorOrId.set(R.string.operator);
        }
    }

    public void onBillsClick() {
        if (mobileBillsModeIsSelected.get()) {
            mobileBillsModeIsSelected.set(false);
            phoneOrId.set(R.string.billing_id);
            operatorOrId.set(R.string.payment_code);
        }
    }

    public void onPersonIconClick() {

    }

    public void onScanBarcodeClick() {

    }

    public void onContinueButtonClick(String field1, String field2) {
        if (field1.isEmpty()) {
            phoneOrIdEnableError.set(true);
            phoneOrIdErrorMessage.set(R.string.error);
        } else {
            phoneOrIdEnableError.set(false);
            phoneOrIdErrorMessage.set(R.string.empty_error_message);
            if (field2.isEmpty()) {
                operatorOrIdMessageEnable.set(true);
                operatorOrIdErrorMessage.set(R.string.error);
            } else {
                operatorOrIdMessageEnable.set(false);
                operatorOrIdErrorMessage.set(R.string.empty_error_message);
                completeListener.onCompleted();
            }
        }
    }
}
