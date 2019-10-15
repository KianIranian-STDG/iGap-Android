package net.iGap.viewmodel;

import android.util.Log;

import androidx.lifecycle.ViewModel;

import net.iGap.model.GoToMainFromRegister;
import net.iGap.model.repository.RegisterRepository;
import net.iGap.module.SingleLiveEvent;
import net.iGap.module.StartupActions;

public class RegistrationViewModel extends ViewModel {

    private boolean showPro;
    private RegisterRepository repository;

    private SingleLiveEvent<Boolean> grantPermission = new SingleLiveEvent<>();
    private SingleLiveEvent<Boolean> goToNicknamePage = new SingleLiveEvent<>();
    private SingleLiveEvent<Boolean> goToIntroduction = new SingleLiveEvent<>();
    private SingleLiveEvent<Boolean> loadFromBackStack = new SingleLiveEvent<>();

    public RegistrationViewModel(boolean showPro) {
        repository = RegisterRepository.getInstance();
        grantPermission.setValue(true);
        this.showPro = showPro;
    }

    public SingleLiveEvent<Boolean> getGrantPermission() {
        return grantPermission;
    }

    public SingleLiveEvent<GoToMainFromRegister> goToMainPage() {
        return repository.getGoToMainPage();
    }

    public SingleLiveEvent<Boolean> getExistUser(){
        return repository.getLoginExistUser();
    }

    public SingleLiveEvent<Long> goToWelcomePage() {
        return repository.getGoToWelcomePage();
    }

    public SingleLiveEvent<Boolean> getGoToNicknamePage() {
        return goToNicknamePage;
    }

    public SingleLiveEvent<Boolean> getGoToIntroduction() {
        return goToIntroduction;
    }

    public SingleLiveEvent<Boolean> getLoadFromBackStack() {
        return loadFromBackStack;
    }

    public void startApp(int backStackCount) {
        Log.wtf(this.getClass().getName(), "startApp");
        StartupActions.makeFolder();
        if (backStackCount == 0) {
            if (showPro) {
                goToNicknamePage.setValue(true);
            } else {
                goToIntroduction.setValue(true);
            }
        } else {
            loadFromBackStack.setValue(true);
        }
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        repository.clearInstance();
    }
}
