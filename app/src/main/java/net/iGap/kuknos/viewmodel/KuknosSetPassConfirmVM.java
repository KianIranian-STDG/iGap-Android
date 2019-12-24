package net.iGap.kuknos.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import net.iGap.R;
import net.iGap.api.apiService.ApiResponse;
import net.iGap.kuknos.service.Repository.UserRepo;
import net.iGap.kuknos.service.mnemonic.WalletException;
import net.iGap.kuknos.service.model.ErrorM;
import net.iGap.kuknos.service.model.KuknosPassM;
import net.iGap.kuknos.service.model.KuknosSubmitM;

public class KuknosSetPassConfirmVM extends ViewModel {

    private MutableLiveData<KuknosPassM> kuknosPassM;
    private MutableLiveData<ErrorM> error;
    private MutableLiveData<Boolean> nextPage;
    private MutableLiveData<Boolean> progressState;
    private String selectedPin;
    private String PIN;
    private String PIN1;
    private String PIN2;
    private String PIN3;
    private String PIN4;
    private boolean completePin = false;
    private UserRepo userRepo = new UserRepo();
    private String token, username;

    public KuknosSetPassConfirmVM() {
        kuknosPassM = new MutableLiveData<>();
        error = new MutableLiveData<>();
        nextPage = new MutableLiveData<>();
        nextPage.setValue(false);
        progressState = new MutableLiveData<>();
    }

    public void onSubmitBtn() {
        if (completePin) {
            PIN = PIN1 + PIN2 + PIN3 + PIN4;
            checkPIN();
        } else {
            error.setValue(new ErrorM(true, "Set Pin", "0", R.string.kuknos_SetPass_error));
        }
    }

    public void checkPIN() {
        if (PIN.equals(selectedPin)) {
            sendDataToServer();
        } else {
            error.setValue(new ErrorM(true, "PIN NOT Match", "0", R.string.kuknos_SetPassConf_error));
        }
    }

    public void sendDataToServer() {
        registerUser();
    }

    public void registerUser() {
        progressState.setValue(true);
        userRepo.setPIN(PIN);
        try {
            userRepo.generateKeyPairWithMnemonicAndPIN();
        } catch (WalletException e) {
            error.setValue(new ErrorM(true, "Internal Error", "1", R.string.kuknos_RecoverySK_ErrorGenerateKey));
            e.printStackTrace();
        }
        nextPage.setValue(true);
        /*userRepo.registerUser(token, userRepo.getAccountID(), username, new ApiResponse<KuknosSubmitM>() {
            @Override
            public void onResponse(KuknosSubmitM kuknosSubmitM) {
                if (kuknosSubmitM.getOk() == 1) {
                    nextPage.setValue(true);
                }
            }

            @Override
            public void onFailed(String error) {
                // TODO delete this
                nextPage.setValue(true);
            }

            @Override
            public void setProgressIndicator(boolean visibility) {
                progressState.setValue(visibility);
            }
        });*/
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

    public MutableLiveData<KuknosPassM> getKuknosPassM() {
        return kuknosPassM;
    }

    public void setKuknosPassM(MutableLiveData<KuknosPassM> kuknosPassM) {
        this.kuknosPassM = kuknosPassM;
    }

    public MutableLiveData<ErrorM> getError() {
        return error;
    }

    public void setError(MutableLiveData<ErrorM> error) {
        this.error = error;
    }

    public MutableLiveData<Boolean> getNextPage() {
        return nextPage;
    }

    public void setNextPage(MutableLiveData<Boolean> nextPage) {
        this.nextPage = nextPage;
    }

    public boolean isCompletePin() {
        return completePin;
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

    public String getSelectedPin() {
        return selectedPin;
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

    public void setToken(String token) {
        this.token = token;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
