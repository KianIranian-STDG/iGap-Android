package net.iGap.kuknos.viewmodel;

import androidx.databinding.ObservableField;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import net.iGap.kuknos.Model.KuknosError;
import net.iGap.kuknos.Repository.UserRepo;

public class KuknosShowRecoveryKeySVM extends ViewModel {

    private MutableLiveData<KuknosError> error;
    //    private MutableLiveData<Boolean> nextPage;
    private ObservableField<String> recoveryKeys = new ObservableField<>();
    private ObservableField<String> publicKey = new ObservableField<>();
    private ObservableField<String> privateKey = new ObservableField<>();

    public KuknosShowRecoveryKeySVM() {
//        nextPage = new MutableLiveData<>();
//        nextPage.setValue(false);
        error = new MutableLiveData<>();
        UserRepo userRepo = new UserRepo();
        if (userRepo.isMnemonicAvailable())
            recoveryKeys.set(userRepo.getMnemonic());
        else
            recoveryKeys.set("---");
        publicKey.set(userRepo.getAccountID());
        privateKey.set(userRepo.getSeedKey());
    }

    /*public void onNext() {
        // TODO call API
        // Data is Correct & proceed
        Handler handler = new Handler();
        handler.postDelayed(() -> {
            //success
            nextPage.setValue(true);
            //error
            *//*error.setValue(new ErrorM(true, "Server Error", "1", R.string.kuknos_login_error_server_str));
            progressState.setValue(false);*//*
        }, 1000);
    }*/

    //Setter and Getter

    public MutableLiveData<KuknosError> getError() {
        return error;
    }

    public void setError(MutableLiveData<KuknosError> error) {
        this.error = error;
    }

    public ObservableField<String> getRecoveryKeys() {
        return recoveryKeys;
    }

    public void setRecoveryKeys(ObservableField<String> recoveryKeys) {
        this.recoveryKeys = recoveryKeys;
    }

    public ObservableField<String> getPublicKey() {
        return publicKey;
    }

    public ObservableField<String> getPrivateKey() {
        return privateKey;
    }

    public void setPublicKey(ObservableField<String> publicKey) {
        this.publicKey = publicKey;
    }

    public void setPrivateKey(ObservableField<String> privateKey) {
        this.privateKey = privateKey;
    }
}
