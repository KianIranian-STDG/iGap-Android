package net.iGap.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

public class DailNumberViewModel extends ViewModel {

    private final MutableLiveData<String> mCurrentDialNumber = new MutableLiveData<>();

    public DailNumberViewModel(){
        mCurrentDialNumber.setValue("");
    }

    public LiveData<String> getCurrentDialNumber(){
        return mCurrentDialNumber;
    }

    public void addNumber(String number){
        mCurrentDialNumber.setValue(mCurrentDialNumber.getValue().concat(number));
    }

    public void removeNumber(){
        mCurrentDialNumber.setValue(mCurrentDialNumber.getValue().replaceFirst(".$",""));
    }

    public void voiceButtonOnClick(){

    }

    public void videoButtonOnClick(){

    }

    public void addContactButtonOnClick(){

    }
}
