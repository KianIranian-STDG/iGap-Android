package net.iGap.viewmodel;

import android.os.Handler;

import androidx.lifecycle.ViewModel;

import net.iGap.module.SingleLiveEvent;

public class WelcomeFragmentViewModel extends ViewModel {

    private SingleLiveEvent<Boolean> goToRegistrationNicknamePage = new SingleLiveEvent<>();

    public WelcomeFragmentViewModel() {
        new Handler().postDelayed(() -> goToRegistrationNicknamePage.postValue(true), 1000);
    }

    public SingleLiveEvent<Boolean> getGoToRegistrationNicknamePage() {
        return goToRegistrationNicknamePage;
    }
}
