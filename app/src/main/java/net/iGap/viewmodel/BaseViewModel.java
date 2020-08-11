package net.iGap.viewmodel;

import net.iGap.api.apiService.BaseAPIViewModel;

public class BaseViewModel extends BaseAPIViewModel implements BaseViewHolder {


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
