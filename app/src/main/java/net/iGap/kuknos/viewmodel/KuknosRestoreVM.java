package net.iGap.kuknos.viewmodel;

import android.text.TextUtils;

import androidx.databinding.ObservableField;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import net.iGap.R;
import net.iGap.api.apiService.ApiResponse;
import net.iGap.kuknos.service.Repository.UserRepo;
import net.iGap.kuknos.service.mnemonic.WalletException;
import net.iGap.kuknos.service.model.ErrorM;
import net.iGap.kuknos.service.model.KuknosInfoM;

public class KuknosRestoreVM extends ViewModel {

    private MutableLiveData<ErrorM> error;
    private MutableLiveData<Integer> nextPage;
    private MutableLiveData<Boolean> progressState;
    private ObservableField<String> keys = new ObservableField<>();
    private MutableLiveData<Boolean> pinCheck;
    private UserRepo userRepo = new UserRepo();
    private String token;

    public KuknosRestoreVM() {
        nextPage = new MutableLiveData<>();
        nextPage.setValue(0);
        error = new MutableLiveData<>();
        progressState = new MutableLiveData<>();
        progressState.setValue(false);
        pinCheck = new MutableLiveData<>();
    }

    public void onNext() {
        if (TextUtils.isEmpty(keys.get())) {
            error.setValue(new ErrorM(true, "Empty Entry", "0", R.string.kuknos_Restore_Error_empty_str));
        } else if (keys.get().split(" ").length < 12) {
            error.setValue(new ErrorM(true, "Invalid Entry", "0", R.string.kuknos_Restore_Error_invalid_str));
        } else {
            if (pinCheck.getValue())
                nextPage.setValue(1);
            else
                generateKeypair();
        }
    }

    private void generateKeypair() {
        progressState.setValue(true);
        userRepo.setMnemonic(keys.get());
        try {
            userRepo.generateKeyPairWithMnemonic();
        } catch (WalletException e) {
            error.setValue(new ErrorM(true, "Internal Error", "2", R.string.kuknos_RecoverySK_ErrorGenerateKey));
            e.printStackTrace();
        }
        checkUserInfo();
    }

    private void checkUserInfo() {
        /*userRepo.getUserInfo(userRepo.getAccountID(), new ApiResponse<KuknosInfoM>() {
            @Override
            public void onResponse(KuknosInfoM kuknosInfoM) {
                nextPage.setValue(2);
            }

            @Override
            public void onFailed(String error) {
                nextPage.setValue(3);
            }

            @Override
            public void setProgressIndicator(boolean visibility) {
                progressState.setValue(visibility);
            }
        });*/
    }

    //Setter and Getter

    public MutableLiveData<ErrorM> getError() {
        return error;
    }

    public void setError(MutableLiveData<ErrorM> error) {
        this.error = error;
    }

    public MutableLiveData<Boolean> getProgressState() {
        return progressState;
    }

    public void setProgressState(MutableLiveData<Boolean> progressState) {
        this.progressState = progressState;
    }

    public ObservableField<String> getKeys() {
        return keys;
    }

    public void setKeys(ObservableField<String> keys) {
        this.keys = keys;
    }

    public MutableLiveData<Boolean> getPinCheck() {
        return pinCheck;
    }

    public void setPinCheck(MutableLiveData<Boolean> pinCheck) {
        this.pinCheck = pinCheck;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public MutableLiveData<Integer> getNextPage() {
        return nextPage;
    }

    public void setNextPage(MutableLiveData<Integer> nextPage) {
        this.nextPage = nextPage;
    }
}
