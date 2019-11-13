package net.iGap.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import net.iGap.R;
import net.iGap.WebSocketClient;
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
        if (WebSocketClient.getInstance().isConnect()) {
            goToChangeLanguagePage.setValue(false);
        } else {
            showErrorMessage.setValue(R.string.waiting_for_connection);
        }
    }

    public void onStartClick() {
        if (WebSocketClient.getInstance().isConnect()) {
            goToRegistrationPage.setValue(true);
        } else {
            showErrorMessage.setValue(R.string.waiting_for_connection);
        }
    }
}
