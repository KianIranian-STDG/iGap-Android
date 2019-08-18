package net.iGap.viewmodel;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

public class BaseViewModel extends ViewModel implements BaseViewHolder {


    public BaseViewModel() {
        onCreateViewModel();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        onDestroyViewModel();
    }

    @Override
    public void onCreateViewModel() {

    }
}
