package net.iGap.viewmodel;

import android.os.Handler;

import androidx.lifecycle.ViewModel;

import net.iGap.module.SingleLiveEvent;

public class WelcomeFragmentViewModel extends ViewModel {

    private SingleLiveEvent<Long> goToRegistrationNicknamePage = new SingleLiveEvent<>();

    public WelcomeFragmentViewModel(long userId) {
        new Handler().postDelayed(() -> goToRegistrationNicknamePage.postValue(userId), 1000);
    }

    public SingleLiveEvent<Long> getGoToRegistrationNicknamePage() {
        return goToRegistrationNicknamePage;
    }
}
