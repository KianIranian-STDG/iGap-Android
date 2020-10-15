package net.iGap.kuknos.viewmodel;

import android.text.TextUtils;

import androidx.databinding.ObservableField;
import androidx.lifecycle.MutableLiveData;

import net.iGap.R;
import net.iGap.api.apiService.BaseAPIViewModel;
import net.iGap.helper.HelperLog;
import net.iGap.kuknos.Model.KuknosError;
import net.iGap.kuknos.Model.KuknosSignupM;
import net.iGap.kuknos.Model.Parsian.KuknosResponseModel;
import net.iGap.kuknos.Model.Parsian.KuknosUserInfo;
import net.iGap.module.SingleLiveEvent;
import net.iGap.module.kuknos.mnemonic.WalletException;
import net.iGap.observers.interfaces.ResponseCallback;
import net.iGap.kuknos.Repository.UserRepo;

public class KuknosRestoreVM extends BaseAPIViewModel {

    private MutableLiveData<KuknosError> error;
    private SingleLiveEvent<Integer> nextPage;
    private MutableLiveData<Boolean> progressState;
    private ObservableField<String> keys = new ObservableField<>();
    private MutableLiveData<Boolean> pinCheck;
    private UserRepo userRepo = new UserRepo();
    private KuknosSignupM kuknosSignupM;
    private boolean isSeedMode = false;

    public KuknosRestoreVM() {
        nextPage = new SingleLiveEvent<>();
        error = new MutableLiveData<>();
        progressState = new MutableLiveData<>();
        progressState.setValue(false);
        pinCheck = new MutableLiveData<>(false);
    }

    public void onNext() {
        if (isSeedMode)
            checkForSeed();
        else
            checkForMnemonic();
    }

    private void checkForMnemonic() {
        if (TextUtils.isEmpty(keys.get())) {
            error.setValue(new KuknosError(true, "Empty Entry", "0", R.string.kuknos_Restore_Error_empty_str));
        } else if (keys.get().split(" ").length < 12) {
            error.setValue(new KuknosError(true, "Invalid Entry", "0", R.string.kuknos_Restore_Error_invalid_str));
        } else {
            if (/*pinCheck.getValue()*/false)
                nextPage.setValue(1);
            else
                generateKeypairWithMnemonic();
        }
    }

    private void checkForSeed() {
        if (TextUtils.isEmpty(keys.get())) {
            error.setValue(new KuknosError(true, "Empty Entry", "0", R.string.kuknos_RestoreSeed_Error_empty_str));
        } else if (keys.get().length() < 20) {
            error.setValue(new KuknosError(true, "Invalid Entry", "0", R.string.kuknos_RestoreSeed_Error_invalid_str));
        } else if (!keys.get().startsWith("S")) {
            error.setValue(new KuknosError(true, "Invalid Entry", "0", R.string.kuknos_RestoreSeed_Error_invalidStart_str));
        } else {
            if (/*pinCheck.getValue()*/false)
                nextPage.setValue(1);
            else
                generateKeypairWithSeed();
        }
    }

    private void generateKeypairWithMnemonic() {
        progressState.setValue(true);
        try {
            checkUserInfo(userRepo.generateKeyPairWithMnemonic(keys.get().trim(), null));
        } catch (WalletException e) {
            error.setValue(new KuknosError(true, "Internal Error", "1", R.string.kuknos_RecoverySK_ErrorGenerateKey));
            e.printStackTrace();
            HelperLog.getInstance().setErrorLog(e);
        }
    }

    private void generateKeypairWithSeed() {
        progressState.setValue(true);
        try {
            checkUserInfo(userRepo.generateKeyPairWithSeed(keys.get(), null, null));
        } catch (Exception e) {
            error.setValue(new KuknosError(true, "Internal Error", "1", R.string.kuknos_RecoverySK_ErrorGenerateKey));
            e.printStackTrace();
        }
    }

    private void checkUserInfo(String publicKey) {
        progressState.setValue(true);
        userRepo.getUserStatus(publicKey, this, new ResponseCallback<KuknosResponseModel<KuknosUserInfo>>() {
            @Override
            public void onSuccess(KuknosResponseModel<KuknosUserInfo> data) {
                switch (data.getData().getStatus()) {
                    case "CREATED":
                    case "ACTIVATED":
                        kuknosSignupM = new KuknosSignupM();
                        kuknosSignupM.setRegistered(true);
                        nextPage.setValue(2);
                        break;
                    case "ACTIVATED_ON_NETWORK":
                        nextPage.setValue(3);
                        break;
                    default:
                        error.setValue(new KuknosError(true, "", "1", R.string.kuknos_Restore_ErrorNoAccount_Snack));
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

    public UserRepo getUserRepo() {
        return userRepo;
    }

    public void setUserRepo(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    public KuknosSignupM getKuknosSignupM() {
        return kuknosSignupM;
    }

    public void setSeedMode(boolean seedMode) {
        isSeedMode = seedMode;
    }

    public SingleLiveEvent<Integer> getNextPage() {
        return nextPage;
    }

    public void setNextPage(SingleLiveEvent<Integer> nextPage) {
        this.nextPage = nextPage;
    }
}
