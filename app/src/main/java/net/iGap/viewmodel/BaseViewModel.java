package net.iGap.viewmodel;

import android.arch.lifecycle.ViewModel;

public abstract class BaseViewModel extends ViewModel {


    public BaseViewModel() {
        onCreate();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        onDestroy();
    }

    public abstract void onCreate();

    public abstract void onStart();

    public abstract void onDestroy();

    public abstract void onPause();

    public abstract void onResume();
}
