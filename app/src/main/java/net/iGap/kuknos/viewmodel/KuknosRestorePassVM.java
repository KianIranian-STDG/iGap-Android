package net.iGap.kuknos.viewmodel;

import androidx.lifecycle.MutableLiveData;

import net.iGap.R;
import net.iGap.api.apiService.BaseAPIViewModel;
import net.iGap.observers.interfaces.ResponseCallback;
import net.iGap.kuknos.service.Repository.UserRepo;
import net.iGap.kuknos.service.mnemonic.WalletException;
import net.iGap.kuknos.service.model.ErrorM;
import net.iGap.kuknos.service.model.KuknosSignupM;
import net.iGap.kuknos.service.model.Parsian.KuknosResponseModel;
import net.iGap.kuknos.service.model.Parsian.KuknosUserInfo;

public class KuknosRestorePassVM extends BaseAPIViewModel {

    private MutableLiveData<ErrorM> error;
    private MutableLiveData<Integer> nextPage;
    private MutableLiveData<Boolean> progressState;
    private UserRepo userRepo = new UserRepo();
    private String PIN;
    private String PIN1;
    private String PIN2;
    private String PIN3;
    private String PIN4;
    private boolean completePin = false;
    private String keyPhrase;
    private KuknosSignupM kuknosSignupM;

    public KuknosRestorePassVM() {
        error = new MutableLiveData<>();
        nextPage = new MutableLiveData<>();
        nextPage.setValue(0);
        progressState = new MutableLiveData<>();
    }

    public void onSubmitBtn() {
        if (completePin) {
            PIN = PIN1 + PIN2 + PIN3 + PIN4;
            generateKeypair();
        } else {
            error.setValue(new ErrorM(true, "Set Pin", "0", R.string.kuknos_SetPass_error));
        }
    }

    private void generateKeypair() {
        progressState.setValue(true);
        userRepo.setMnemonic(keyPhrase);
        userRepo.setPIN(PIN);
        try {
            userRepo.generateKeyPairWithMnemonicAndPIN();
        } catch (WalletException e) {
            error.setValue(new ErrorM(true, "Internal Error", "2", R.string.kuknos_RecoverySK_ErrorGenerateKey));
            e.printStackTrace();
        }
        checkUserInfo();
    }

    private void checkUserInfo() {
        progressState.setValue(true);
        userRepo.getUserStatus(this, new ResponseCallback<KuknosResponseModel<KuknosUserInfo>>() {
            @Override
            public void onSuccess(KuknosResponseModel<KuknosUserInfo> data) {
                switch (data.getData().getStatus()) {
                    case "CREATED":
                    case "ACTIVATED":
                        kuknosSignupM = new KuknosSignupM();
                        kuknosSignupM.setRegistered(true);
                        nextPage.setValue(1);
                        break;
                    case "NOT_CREATED":
                        nextPage.setValue(2);
                        break;
                }
                progressState.setValue(false);
            }

            @Override
            public void onError(String error) {
                progressState.setValue(false);
            }

            @Override
            public void onFailed() {
                progressState.setValue(false);
            }

        });
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

    public MutableLiveData<ErrorM> getError() {
        return error;
    }

    public void setError(MutableLiveData<ErrorM> error) {
        this.error = error;
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

    public void setKeyPhrase(String keyPhrase) {
        this.keyPhrase = keyPhrase;
    }

    public void setToken(String token) {
    }

    public MutableLiveData<Boolean> getProgressState() {
        return progressState;
    }

    public void setProgressState(MutableLiveData<Boolean> progressState) {
        this.progressState = progressState;
    }

    public MutableLiveData<Integer> getNextPage() {
        return nextPage;
    }

    public void setNextPage(MutableLiveData<Integer> nextPage) {
        this.nextPage = nextPage;
    }

    public KuknosSignupM getKuknosSignupM() {
        return kuknosSignupM;
    }

}
