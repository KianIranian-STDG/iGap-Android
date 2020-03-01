package net.iGap.viewmodel.kuknos;

import android.text.TextUtils;

import androidx.databinding.ObservableField;
import androidx.lifecycle.MutableLiveData;

import net.iGap.R;
import net.iGap.api.apiService.BaseAPIViewModel;
import net.iGap.model.kuknos.KuknosError;
import net.iGap.model.kuknos.KuknosSignupM;
import net.iGap.model.kuknos.Parsian.KuknosResponseModel;
import net.iGap.model.kuknos.Parsian.KuknosUserInfo;
import net.iGap.module.kuknos.mnemonic.WalletException;
import net.iGap.observers.interfaces.ResponseCallback;
import net.iGap.repository.kuknos.UserRepo;

public class KuknosRestoreVM extends BaseAPIViewModel {

    private MutableLiveData<KuknosError> error;
    private MutableLiveData<Integer> nextPage;
    private MutableLiveData<Boolean> progressState;
    private ObservableField<String> keys = new ObservableField<>();
    private MutableLiveData<Boolean> pinCheck;
    private UserRepo userRepo = new UserRepo();
    private KuknosSignupM kuknosSignupM;

    public KuknosRestoreVM() {
        nextPage = new MutableLiveData<>();
        nextPage.setValue(0);
        error = new MutableLiveData<>();
        progressState = new MutableLiveData<>();
        progressState.setValue(false);
        pinCheck = new MutableLiveData<>(false);
    }

    public void onNext() {
        if (TextUtils.isEmpty(keys.get())) {
            error.setValue(new KuknosError(true, "Empty Entry", "0", R.string.kuknos_Restore_Error_empty_str));
        } else if (keys.get().split(" ").length < 12) {
            error.setValue(new KuknosError(true, "Invalid Entry", "0", R.string.kuknos_Restore_Error_invalid_str));
        } else {
            if (/*pinCheck.getValue()*/true)
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
            error.setValue(new KuknosError(true, "Internal Error", "2", R.string.kuknos_RecoverySK_ErrorGenerateKey));
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
                        nextPage.setValue(2);
                        break;
                    case "NOT_CREATED":
                        nextPage.setValue(3);
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

    //Setter and Getter

    public MutableLiveData<KuknosError> getError() {
        return error;
    }

    public void setError(MutableLiveData<KuknosError> error) {
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

    public void setToken(String token) {
    }

    public MutableLiveData<Integer> getNextPage() {
        return nextPage;
    }

    public void setNextPage(MutableLiveData<Integer> nextPage) {
        this.nextPage = nextPage;
    }

    public UserRepo getUserRepo() {
        return userRepo;
    }

    public void setUserRepo(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    public KuknosSignupM getKuknosSignupM() {
        return kuknosSignupM;
    }

}
