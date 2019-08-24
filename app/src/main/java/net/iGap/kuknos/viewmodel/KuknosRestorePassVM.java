package net.iGap.kuknos.viewmodel;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.os.Handler;

import net.iGap.R;
import net.iGap.kuknos.service.Repository.UserRepo;
import net.iGap.kuknos.service.mnemonic.WalletException;
import net.iGap.kuknos.service.model.ErrorM;
import net.iGap.kuknos.service.model.KuknosPassM;

public class KuknosRestorePassVM extends ViewModel {

    private MutableLiveData<KuknosPassM> kuknosPassM;
    private MutableLiveData<ErrorM> error;
    private MutableLiveData<Boolean> nextPage;
    private MutableLiveData<Boolean> progressState;
    private UserRepo userRepo = new UserRepo();
    private String PIN;
    private String PIN1;
    private String PIN2;
    private String PIN3;
    private String PIN4;
    private boolean completePin = false;
    private String keyPhrase;

    public KuknosRestorePassVM() {
        if (kuknosPassM == null) {
            kuknosPassM = new MutableLiveData<KuknosPassM>();
        }
        if (error == null) {
            error = new MutableLiveData<ErrorM>();
        }
        if (nextPage == null) {
            nextPage = new MutableLiveData<Boolean>();
            nextPage.setValue(false);
        }
    }

    public void onSubmitBtn() {
        if (completePin){
            PIN = PIN1 + PIN2 + PIN3 + PIN4;
            nextPage.setValue(true);
        }
        else {
            error.setValue(new ErrorM(true, "Set Pin", "0", R.string.kuknos_SetPass_error));
        }
    }

    private void generateKeypair() {
        progressState.setValue(true);
        // TODO call API
        // Data is Correct & proceed
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                userRepo.setMnemonic(keyPhrase);
                userRepo.setPIN(PIN);
                try {
                    userRepo.generateKeyPairWithMnemonicAndPIN();
                    //success
                    nextPage.setValue(true);
                } catch (WalletException e) {
                    //error
                    error.setValue(new ErrorM(true, "Internal Error", "2", R.string.kuknos_RecoverySK_ErrorGenerateKey));
                    e.printStackTrace();
                }
                progressState.setValue(false);
            }
        }, 1000);
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

    public void setKeyPhrase(String keyPhrase) {
        this.keyPhrase = keyPhrase;
    }
}
