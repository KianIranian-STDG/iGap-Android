package net.iGap.dialog.payment.getmoney;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import net.iGap.R;
import net.iGap.dialog.payment.CompleteListener;

public class GetMoneyViewModel extends ViewModel {

    public MutableLiveData<Integer> nameOrPhoneErrorMessage = new MutableLiveData<>();
    public MutableLiveData<Boolean> nameOrPhoneEnableError = new MutableLiveData<>();
    public MutableLiveData<Integer> amountErrorMessage = new MutableLiveData<>();
    public MutableLiveData<Boolean> amountEnableError = new MutableLiveData<>();
    private MutableLiveData<Integer> forDetailErrorMessage = new MutableLiveData<>();
    private MutableLiveData<Boolean> forDetailEnableError = new MutableLiveData<>();

    private CompleteListener completeListener;

    public GetMoneyViewModel(CompleteListener completeListener) {
        this.completeListener = completeListener;
    }

    public LiveData<Integer> forDetailErrorMessage(){
        return forDetailErrorMessage;
    }

    public LiveData<Boolean> forDetailEnableError(){
        return forDetailEnableError;
    }

    public void onActionInfoClick() {

    }

    public void onCardIconClick() {

    }

    public void onPersonIconClick() {

    }

    public void onContinueButtonClick(String nameOrPhoneNumber, String amount, String forDetail) {
        if (nameOrPhoneNumber.isEmpty()) {
            nameOrPhoneEnableError.setValue(true);
            nameOrPhoneErrorMessage.setValue(R.string.error);
        } else {
            nameOrPhoneEnableError.setValue(false);
            nameOrPhoneErrorMessage.setValue(R.string.is_empty);
            if (amount.isEmpty()) {
                amountErrorMessage.setValue(R.string.error);
                amountEnableError.setValue(true);
            } else {
                amountErrorMessage.setValue(R.string.is_empty);
                amountEnableError.setValue(false);
                if (forDetail.isEmpty()) {
                    forDetailErrorMessage.setValue(R.string.error);
                    forDetailEnableError.setValue(true);
                } else {
                    forDetailErrorMessage.setValue(R.string.is_empty);
                    forDetailEnableError.setValue(false);
                    //every thing is ok and go next step
                    completeListener.onCompleted();
                }
            }
        }
    }
}