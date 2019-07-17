package net.iGap.viewmodel;

import android.arch.lifecycle.ViewModel;

public class BaseViewModel extends ViewModel implements BaseViewHolder {


    public BaseViewModel() {
        onCreateViewModel();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        onDestroy();
    }

    @Override
    public void onCreateViewModel() {

    }

    @Override
    public void onDestroy() {

    }
}
