package net.iGap.dialog.payment.sendmoney;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import net.iGap.R;
import net.iGap.dialog.payment.CompleteListener;

public class SendMoneyViewModel extends ViewModel {

    public MutableLiveData<Boolean> shebaModeIsSelected = new MutableLiveData<>();
    public MutableLiveData<Integer> destinationInfo = new MutableLiveData<>();
    public MutableLiveData<Boolean> destinationEnableError = new MutableLiveData<>();
    public MutableLiveData<Integer> destinationErrorMessage = new MutableLiveData<>();
    public MutableLiveData<Integer> amountErrorMessage = new MutableLiveData<>();
    public MutableLiveData<Boolean> amountErrorMessageEnable = new MutableLiveData<>();

    private CompleteListener completeListener;

    public SendMoneyViewModel(CompleteListener completeListener) {
        shebaModeIsSelected.setValue(false);
        this.completeListener = completeListener;
    }

    public void onActionInfoClick() {

    }

    public void onShebaClick() {
        if (shebaModeIsSelected.getValue() != null) {
            if (!shebaModeIsSelected.getValue()) {
                shebaModeIsSelected.setValue(true);
                destinationInfo.setValue(R.string.destination_sheba_number);
            }
        }
    }

    public void onCardClick() {
        if (shebaModeIsSelected.getValue() != null) {
            if (shebaModeIsSelected.getValue()) {
                shebaModeIsSelected.setValue(false);
                destinationInfo.setValue(R.string.destination_card);
            }
        }
    }

    public void onCardIconClick() {

    }

    public void onPersonIconClick() {

    }

    public void onContinueButtonClick(String destinationInfo, String amount) {
        if (destinationInfo.isEmpty()) {
            destinationErrorMessage.setValue(R.string.error);
            destinationEnableError.setValue(true);
        } else {
            destinationErrorMessage.setValue(R.string.empty_error_message);
            destinationEnableError.setValue(false);
            if (amount.isEmpty()) {
                amountErrorMessage.setValue(R.string.error);
                amountErrorMessageEnable.setValue(true);
            } else {
                amountErrorMessage.setValue(R.string.empty_error_message);
                amountErrorMessageEnable.setValue(false);
                // every things ok do your jobs
                completeListener.onCompleted();
            }
        }
    }
}
