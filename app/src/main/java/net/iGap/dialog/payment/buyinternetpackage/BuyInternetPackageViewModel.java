package net.iGap.dialog.payment.buyinternetpackage;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import net.iGap.R;
import net.iGap.dialog.payment.CompleteListener;

public class BuyInternetPackageViewModel extends ViewModel {

    private MutableLiveData<Boolean> phoneEnableError = new MutableLiveData<>();
    private MutableLiveData<Integer> phoneErrorMessage = new MutableLiveData<>();
    private MutableLiveData<Boolean> operatorMessageEnable = new MutableLiveData<>();
    private MutableLiveData<Integer> operatorErrorMessage = new MutableLiveData<>();

    private CompleteListener completeListener;

    public BuyInternetPackageViewModel(CompleteListener completeListener){
        this.completeListener = completeListener;
    }

    public LiveData<Boolean> getPhoneEnableError(){
        return phoneEnableError;
    }

    public LiveData<Integer> phoneErrorMessage(){
        return phoneErrorMessage;
    }

    public LiveData<Boolean> operatorMessageEnable(){
        return operatorMessageEnable;
    }

    public LiveData<Integer> operatorErrorMessage(){
        return operatorErrorMessage;
    }

    public void onActionInfoClick(){

    }

    public void onPersonIconClick(){

    }

    public void onContinueButtonClick(String phoneNumber,String operator){
        if (phoneNumber.isEmpty()){
            phoneEnableError.setValue(true);
            phoneErrorMessage.setValue(R.string.error);
        }
        else{
            phoneEnableError.setValue(false);
            phoneErrorMessage.setValue(R.string.is_empty);
            if (operator.isEmpty()){
                operatorMessageEnable.setValue(true);
                operatorErrorMessage.setValue(R.string.error);
            }
            else{
                operatorMessageEnable.setValue(false);
                operatorErrorMessage.setValue(R.string.is_empty);
                completeListener.onCompleted();
            }
        }
    }
}
