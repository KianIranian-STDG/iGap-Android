package net.iGap.kuknos.viewmodel;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.databinding.ObservableField;
import android.os.Handler;
import android.text.TextUtils;

import net.iGap.R;
import net.iGap.kuknos.service.Repository.UserRepo;
import net.iGap.kuknos.service.mnemonic.WalletException;
import net.iGap.kuknos.service.model.ErrorM;
import net.iGap.kuknos.service.model.KuknosRestoreM;

import java.util.Objects;

public class KuknosRestoreVM extends ViewModel {

    private MutableLiveData<ErrorM> error;
    private MutableLiveData<Boolean> nextPage;
    private MutableLiveData<Boolean> progressState;
    private ObservableField<String> keys = new ObservableField<>();
    private UserRepo userRepo = new UserRepo();

    public KuknosRestoreVM() {
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

    public void onNext() {
        if (TextUtils.isEmpty(keys.get())) {
            error.setValue(new ErrorM(true, "Empty Entry", "0", R.string.kuknos_Restore_Error_empty_str));
        }
        else if (keys.get().split(" ").length < 12) {
            error.setValue(new ErrorM(true, "Invalid Entry", "0", R.string.kuknos_Restore_Error_invalid_str));
        }
        else {
            generateKeypair();
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
                userRepo.setMnemonic(keys.get());
                try {
                    userRepo.generateKeyPairWithMnemonic();
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

    public ObservableField<String> getKeys() {
        return keys;
    }

    public void setKeys(ObservableField<String> keys) {
        this.keys = keys;
    }
}
