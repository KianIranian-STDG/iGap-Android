package net.iGap.dialog.payment.paybills;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import net.iGap.R;
import net.iGap.dialog.payment.CompleteListener;

public class PayBillsViewModel extends ViewModel {

    public MutableLiveData<Boolean> mobileBillsModeIsSelected = new MutableLiveData<>();
    public MutableLiveData<Integer> phoneOrId = new MutableLiveData<>();
    public MutableLiveData<Boolean> phoneOrIdEnableError = new MutableLiveData<>();
    public MutableLiveData<Integer> phoneOrIdErrorMessage = new MutableLiveData<>();
    public MutableLiveData<Integer> operatorOrId = new MutableLiveData<>();
    public MutableLiveData<Boolean> operatorOrIdMessageEnable = new MutableLiveData<>();
    public MutableLiveData<Integer> operatorOrIdErrorMessage = new MutableLiveData<>();

    private CompleteListener completeListener;

    public PayBillsViewModel(CompleteListener completeListener) {
        mobileBillsModeIsSelected.setValue(false);
        this.completeListener = completeListener;
    }

    public void onActionInfoClick() {

    }

    public void onMobileBillsClick() {
        if (mobileBillsModeIsSelected.getValue() != null) {
            if (!mobileBillsModeIsSelected.getValue()) {
                mobileBillsModeIsSelected.setValue(true);
                phoneOrId.setValue(R.string.phone_number);
                operatorOrId.setValue(R.string.operator);
            }
        }
    }

    public void onBillsClick() {
        if (mobileBillsModeIsSelected.getValue() != null) {
            if (mobileBillsModeIsSelected.getValue()) {
                mobileBillsModeIsSelected.setValue(false);
                phoneOrId.setValue(R.string.billing_id);
                operatorOrId.setValue(R.string.payment_code);
            }
        }
    }

    public void onPersonIconClick() {

    }

    public void onScanBarcodeClick(){

    }

    public void onContinueButtonClick(String field1, String field2) {
        if (field1.isEmpty()) {
            phoneOrIdEnableError.setValue(true);
            phoneOrIdErrorMessage.setValue(R.string.error);
        } else {
            phoneOrIdEnableError.setValue(false);
            phoneOrIdErrorMessage.setValue(R.string.empty_error_message);
            if (field2.isEmpty()) {
                operatorOrIdMessageEnable.setValue(true);
                operatorOrIdErrorMessage.setValue(R.string.error);
            } else {
                operatorOrIdMessageEnable.setValue(false);
                operatorOrIdErrorMessage.setValue(R.string.empty_error_message);
                completeListener.onCompleted();
            }
        }
    }
}
