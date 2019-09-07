package net.iGap.viewmodel;

import androidx.lifecycle.ViewModel;

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
