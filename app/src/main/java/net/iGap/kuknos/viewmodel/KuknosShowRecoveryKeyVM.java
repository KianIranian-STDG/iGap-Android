package net.iGap.kuknos.viewmodel;

import android.util.Log;

import androidx.databinding.ObservableField;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import net.iGap.R;
import net.iGap.api.apiService.ApiResponse;
import net.iGap.api.apiService.BaseAPIViewModel;
import net.iGap.api.apiService.ResponseCallback;
import net.iGap.api.errorhandler.ErrorModel;
import net.iGap.kuknos.service.Repository.UserRepo;
import net.iGap.kuknos.service.mnemonic.WalletException;
import net.iGap.kuknos.service.model.ErrorM;
import net.iGap.kuknos.service.model.KuknosSignupM;
import net.iGap.kuknos.service.model.KuknosSubmitM;
import net.iGap.kuknos.service.model.Parsian.KuknosResponseModel;

public class KuknosShowRecoveryKeyVM extends BaseAPIViewModel {

    private UserRepo userRepo = new UserRepo();
    private MutableLiveData<ErrorM> error;
    private MutableLiveData<Boolean> nextPage;
    private MutableLiveData<Boolean> progressState;
    private ObservableField<String> mnemonic = new ObservableField<>();
    private ObservableField<Boolean> pinCheck = new ObservableField<>(false);
    private KuknosSignupM info;

    public KuknosShowRecoveryKeyVM() {
        nextPage = new MutableLiveData<>();
        nextPage.setValue(false);
        error = new MutableLiveData<>();
        progressState = new MutableLiveData<>();
        progressState.setValue(false);
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
        } else {
            userRepo.setPIN("-1");
            try {
                userRepo.generateKeyPairWithMnemonic();
            } catch (WalletException e) {
                error.setValue(new ErrorM(true, "Internal Error", "2", R.string.kuknos_RecoverySK_ErrorGenerateKey));
                e.printStackTrace();
            }

            progressState.setValue(true);
            info.setKeyString(userRepo.getAccountID());
            userRepo.registerUser(info, this, new ResponseCallback<KuknosResponseModel>() {
                @Override
                public void onSuccess(KuknosResponseModel data) {
                    nextPage.setValue(true);
                    progressState.setValue(false);
                }

                @Override
                public void onError(ErrorModel error) {
                    progressState.setValue(false);
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

    public void setInfo(KuknosSignupM info) {
        this.info = info;
    }
}
