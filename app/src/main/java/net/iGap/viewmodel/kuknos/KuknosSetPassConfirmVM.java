package net.iGap.viewmodel.kuknos;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import net.iGap.R;
import net.iGap.helper.HelperLog;
import net.iGap.model.kuknos.KuknosError;
import net.iGap.module.kuknos.mnemonic.WalletException;
import net.iGap.repository.kuknos.UserRepo;

public class KuknosSetPassConfirmVM extends ViewModel {

    private MutableLiveData<KuknosError> error;
    private MutableLiveData<Boolean> nextPageSignup;
    private MutableLiveData<Boolean> nextPagePanel;
    private MutableLiveData<Boolean> progressState;
    private String selectedPin;
    private String PIN;
    private String PIN1;
    private String PIN2;
    private String PIN3;
    private String PIN4;
    private boolean completePin = false;
    private UserRepo userRepo = new UserRepo();
    private int mode;

    public KuknosSetPassConfirmVM() {
        error = new MutableLiveData<>();
        nextPageSignup = new MutableLiveData<>();
        nextPageSignup.setValue(false);
        nextPagePanel = new MutableLiveData<>();
        nextPagePanel.setValue(false);
        progressState = new MutableLiveData<>();
    }

    public void onSubmitBtn() {
        if (completePin) {
            PIN = PIN1 + PIN2 + PIN3 + PIN4;
            checkPIN();
        } else {
            error.setValue(new KuknosError(true, "Set Pin", "0", R.string.kuknos_SetPass_error));
        }
    }

    private void checkPIN() {
        if (PIN.equals(selectedPin)) {
            sendDataToServer();
        } else {
            error.setValue(new KuknosError(true, "PIN NOT Match", "0", R.string.kuknos_SetPassConf_error));
        }
    }

    private void sendDataToServer() {
        registerUser();
    }

    private void registerUser() {
        progressState.setValue(true);
        userRepo.setPIN(PIN);
        if (mode == 0) {
            try {
                userRepo.generateKeyPairWithMnemonic(userRepo.getMnemonic(), PIN);
            } catch (WalletException e) {
                error.setValue(new KuknosError(true, "Internal Error", "1", R.string.kuknos_RecoverySK_ErrorGenerateKey));
                e.printStackTrace();
                HelperLog.setErrorLog(e);
                return;
            }
            nextPageSignup.setValue(true);
        } else if (mode == 2) {
            nextPagePanel.setValue(true);
        } else {
            nextPageSignup.setValue(true);
        }
    }

    // setter and getter

    public String getPIN1() {
        return PIN1;
    }

    public void setPIN1(String PIN1) {
        this.PIN1 = PIN1;
    }

    public String getPIN2() {
        return PIN2;
    }

    public void setPIN2(String PIN2) {
        this.PIN2 = PIN2;
    }

    public String getPIN3() {
        return PIN3;
    }

    public void setPIN3(String PIN3) {
        this.PIN3 = PIN3;
    }

    public String getPIN4() {
        return PIN4;
    }

    public void setPIN4(String PIN4) {
        this.PIN4 = PIN4;
    }

    public MutableLiveData<KuknosError> getError() {
        return error;
    }

    public void setError(MutableLiveData<KuknosError> error) {
        this.error = error;
    }

    public MutableLiveData<Boolean> getNextPageSignup() {
        return nextPageSignup;
    }

    public void setNextPageSignup(MutableLiveData<Boolean> nextPageSignup) {
        this.nextPageSignup = nextPageSignup;
    }

    public void setCompletePin(boolean completePin) {
        this.completePin = completePin;
    }

    public String getPIN() {
        return PIN;
    }

    public void setPIN(String PIN) {
        this.PIN = PIN;
    }

    public void setSelectedPin(String selectedPin) {
        this.selectedPin = selectedPin;
    }

    public MutableLiveData<Boolean> getProgressState() {
        return progressState;
    }

    public void setProgressState(MutableLiveData<Boolean> progressState) {
        this.progressState = progressState;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public MutableLiveData<Boolean> getNextPagePanel() {
        return nextPagePanel;
    }
}
