package net.iGap.kuknos.viewmodel;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.databinding.ObservableField;
import android.os.Handler;

import net.iGap.R;
import net.iGap.api.apiService.ApiResponse;
import net.iGap.kuknos.service.Repository.UserRepo;
import net.iGap.kuknos.service.mnemonic.WalletException;
import net.iGap.kuknos.service.model.ErrorM;
import net.iGap.kuknos.service.model.KuknosRestoreM;
import net.iGap.kuknos.service.model.KuknosSubmitM;

public class KuknosShowRecoveryKeyVM extends ViewModel {

    private UserRepo userRepo = new UserRepo();
    private MutableLiveData<ErrorM> error;
    private MutableLiveData<Boolean> nextPage;
    private MutableLiveData<Boolean> progressState;
    private ObservableField<String> mnemonic = new ObservableField<>();
    private ObservableField<Boolean> pinCheck = new ObservableField<>();
    private String token, username;

    public KuknosShowRecoveryKeyVM() {
        if (nextPage == null) {
            nextPage = new MutableLiveData<Boolean>();
            nextPage.setValue(false);
        }
        if (error == null) {
            error = new MutableLiveData<ErrorM>();
        }
        if (progressState == null) {
            progressState = new MutableLiveData<Boolean>();
            progressState.setValue(false);
        }
    }

    public void initMnemonic() {
        userRepo.generateMnemonic();
        if (userRepo.getMnemonic().equals("-1")) {
            error.setValue(new ErrorM(true, "generate fatal error", "1", R.string.kuknos_RecoverySK_ErrorGenerateMn));
            return;
        }
        mnemonic.set(userRepo.getMnemonic());
    }

    public void onNext() {

        progressState.setValue(true);
        if (pinCheck.get()) {
            nextPage.setValue(true);
            progressState.setValue(false);
        }
        else {
            userRepo.setPIN("-1");
            try {
                userRepo.generateKeyPairWithMnemonic();
            } catch (WalletException e) {
                error.setValue(new ErrorM(true, "Internal Error", "2", R.string.kuknos_RecoverySK_ErrorGenerateKey));
                e.printStackTrace();
            }
            userRepo.registerUser(token, userRepo.getAccountID(), username, new ApiResponse<KuknosSubmitM>() {
                @Override
                public void onResponse(KuknosSubmitM kuknosSubmitM) {
                    if (kuknosSubmitM.getOk() == 1) {
                        nextPage.setValue(true);
                    }
                }

                @Override
                public void onFailed(String error) {

                }

                @Override
                public void setProgressIndicator(boolean visibility) {
                    progressState.setValue(visibility);
                }
            });
        }
    }

    //Setter and Getter

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

    public MutableLiveData<Boolean> getProgressState() {
        return progressState;
    }

    public void setProgressState(MutableLiveData<Boolean> progressState) {
        this.progressState = progressState;
    }

    public ObservableField<String> getMnemonic() {
        return mnemonic;
    }

    public void setMnemonic(ObservableField<String> mnemonic) {
        this.mnemonic = mnemonic;
    }

    public ObservableField<Boolean> getPinCheck() {
        return pinCheck;
    }

    public void setPinCheck(ObservableField<Boolean> pinCheck) {
        this.pinCheck = pinCheck;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
