package net.iGap.viewmodel;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

public class BaseViewModel extends ViewModel implements BaseViewHolder {

    private MutableLiveData<Boolean> progressLiveData = new MutableLiveData<>();

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

    public MutableLiveData<Boolean> getProgressLiveData() {
        return progressLiveData;
    }

    public void setProgressLiveData(MutableLiveData<Boolean> progressLiveData) {
        this.progressLiveData = progressLiveData;
    }
}
