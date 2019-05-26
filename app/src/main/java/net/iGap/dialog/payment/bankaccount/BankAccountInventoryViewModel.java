package net.iGap.dialog.payment.bankaccount;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import net.iGap.R;
import net.iGap.dialog.payment.CompleteListener;

public class BankAccountInventoryViewModel extends ViewModel {

    public MutableLiveData<Boolean> inventoryModeIsSelected = new MutableLiveData<>();
    public MutableLiveData<Boolean> cardNumberEnableError = new MutableLiveData<>();
    public MutableLiveData<Integer> cardNumberErrorMessage = new MutableLiveData<>();
    public MutableLiveData<Boolean> secondsPasswordMessageEnable = new MutableLiveData<>();
    public MutableLiveData<Integer> secondsPasswordErrorMessage = new MutableLiveData<>();

    private CompleteListener completeListener;

    public BankAccountInventoryViewModel(CompleteListener completeListener) {
        this.completeListener = completeListener;
        inventoryModeIsSelected.setValue(true);
    }


    public void onActionInfoClick() {

    }

    public void onLastTransactionClick() {
        if (inventoryModeIsSelected.getValue() != null) {
            if (inventoryModeIsSelected.getValue()) {
                inventoryModeIsSelected.setValue(false);
            }
        }
    }

    public void onInventoryClick() {
        if (inventoryModeIsSelected.getValue() != null) {
            if (!inventoryModeIsSelected.getValue()) {
                inventoryModeIsSelected.setValue(true);
            }
        }
    }

    public void onCardIconClick() {

    }

    public void onContinueButtonClick(String cardNumber, String pin) {
        if (cardNumber.isEmpty()) {
            cardNumberErrorMessage.setValue(R.string.error);
            cardNumberEnableError.setValue(true);
        } else {
            cardNumberErrorMessage.setValue(R.string.is_empty);
            cardNumberEnableError.setValue(false);
            if (pin.isEmpty()) {
                secondsPasswordErrorMessage.setValue(R.string.error);
                secondsPasswordMessageEnable.setValue(true);
            } else {
                secondsPasswordErrorMessage.setValue(R.string.is_empty);
                secondsPasswordMessageEnable.setValue(false);
                completeListener.onCompleted();
            }
        }
    }
}