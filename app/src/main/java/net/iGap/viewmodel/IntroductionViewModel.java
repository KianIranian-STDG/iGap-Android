package net.iGap.viewmodel;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import net.iGap.G;
import net.iGap.R;
import net.iGap.module.SingleLiveEvent;

public class IntroductionViewModel extends ViewModel {

    private MutableLiveData<Integer> showErrorMessage = new MutableLiveData<>();
    private SingleLiveEvent<Boolean> goToRegistrationPage = new SingleLiveEvent<>();
    private SingleLiveEvent<Boolean> goToChangeLanguagePage = new SingleLiveEvent<>();

    public MutableLiveData<Integer> getShowErrorMessage() {
        return showErrorMessage;
    }

    public SingleLiveEvent<Boolean> getGoToRegistrationPage() {
        return goToRegistrationPage;
    }

    public SingleLiveEvent<Boolean> getGoToChangeLanguagePage() {
        return goToChangeLanguagePage;
    }

    public void onLanguageChangeClick() {
        if (G.socketConnection) {
            goToChangeLanguagePage.setValue(false);
        } else {
            showErrorMessage.setValue(R.string.waiting_for_connection);
        }
    }

    public void onStartClick() {
        if (G.socketConnection) {
            goToRegistrationPage.setValue(true);
        } else {
            showErrorMessage.setValue(R.string.waiting_for_connection);
        }
    }
}
