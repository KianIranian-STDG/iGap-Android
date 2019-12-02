package net.iGap.viewmodel;

import android.util.Log;

import androidx.lifecycle.ViewModel;

import net.iGap.model.GoToMainFromRegister;
import net.iGap.model.repository.RegisterRepository;
import net.iGap.module.SingleLiveEvent;
import net.iGap.module.StartupActions;

public class RegistrationViewModel extends ViewModel {

    private boolean showPro;
    private boolean isAddAccount;
    private RegisterRepository repository;

    private SingleLiveEvent<Boolean> grantPermission = new SingleLiveEvent<>();
    private SingleLiveEvent<Boolean> goToNicknamePage = new SingleLiveEvent<>();
    private SingleLiveEvent<Boolean> goToIntroduction = new SingleLiveEvent<>();
    private SingleLiveEvent<Boolean> goToRegisterPage = new SingleLiveEvent<>();
    private SingleLiveEvent<Boolean> loadFromBackStack = new SingleLiveEvent<>();

    public RegistrationViewModel(boolean showPro,boolean isAddAccount) {
        repository = RegisterRepository.getInstance();
        grantPermission.setValue(true);
        this.showPro = showPro;
        this.isAddAccount = isAddAccount;
    }

    public SingleLiveEvent<Boolean> getGrantPermission() {
        return grantPermission;
    }

    public SingleLiveEvent<GoToMainFromRegister> goToMainPage() {
        return repository.getGoToMainPage();
    }

    public SingleLiveEvent<Long> goToContactPage(){
        return repository.getGoToSyncContactPageForNewUser();
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

    public SingleLiveEvent<Boolean> getGoToRegisterPage() {
        return goToRegisterPage;
    }

    public void startApp(int backStackCount) {
        Log.wtf(this.getClass().getName(), "startApp");
        StartupActions.makeFolder();
        if (backStackCount == 0) {
            if (showPro) {
                goToNicknamePage.setValue(true);
            } else if(isAddAccount){
                goToRegisterPage.setValue(true);
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
