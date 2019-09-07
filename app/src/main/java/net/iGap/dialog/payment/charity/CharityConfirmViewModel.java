package net.iGap.dialog.payment.charity;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import net.iGap.dialog.payment.CompleteListener;

public class CharityConfirmViewModel extends ViewModel {

    private MutableLiveData<String> charityName = new MutableLiveData<>();
    private MutableLiveData<String> charityAmount = new MutableLiveData<>();
    private CompleteListener completeListener;

    public CharityConfirmViewModel(String charityName, String charityAmount, CompleteListener completeListener) {
        this.charityName.setValue(charityName);
        this.charityAmount.setValue(charityAmount);
        this.completeListener = completeListener;
    }

    public LiveData<String> getCharityName() {
        return charityName;
    }

    public LiveData<String> getCharityAmount() {
        return charityAmount;
    }

    public void onContinueButtonClick() {

    }
}
