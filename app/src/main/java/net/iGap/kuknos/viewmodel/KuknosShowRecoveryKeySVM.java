package net.iGap.kuknos.viewmodel;

import android.os.Handler;

import androidx.databinding.ObservableField;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import net.iGap.kuknos.service.Repository.UserRepo;
import net.iGap.kuknos.service.model.ErrorM;
import net.iGap.kuknos.service.model.KuknosRestoreM;

public class KuknosShowRecoveryKeySVM extends ViewModel {

    private MutableLiveData<ErrorM> error;
    private MutableLiveData<Boolean> nextPage;
    private ObservableField<String> keys = new ObservableField<>();

    public KuknosShowRecoveryKeySVM() {
        nextPage = new MutableLiveData<>();
        nextPage.setValue(false);
        error = new MutableLiveData<>();
        UserRepo userRepo = new UserRepo();
        keys.set(userRepo.getMnemonic());
    }

    public void onNext() {
        // TODO call API
        // Data is Correct & proceed
        Handler handler = new Handler();
        handler.postDelayed(() -> {
            //success
            nextPage.setValue(true);
            //error
            /*error.setValue(new ErrorM(true, "Server Error", "1", R.string.kuknos_login_error_server_str));
            progressState.setValue(false);*/
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

    public ObservableField<String> getKeys() {
        return keys;
    }

    public void setKeys(ObservableField<String> keys) {
        this.keys = keys;
    }
}
