package net.iGap.kuknos.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.databinding.ObservableField;
import android.os.Handler;

import net.iGap.kuknos.service.Repository.UserRepo;
import net.iGap.kuknos.service.model.ErrorM;
import net.iGap.kuknos.service.model.KuknosRestoreM;

public class KuknosShowRecoveryKeySVM extends ViewModel {

    private MutableLiveData<KuknosRestoreM> kuknosRestoreM;
    private MutableLiveData<ErrorM> error;
    private MutableLiveData<Boolean> nextPage;
    private ObservableField<String> keys = new ObservableField<>();
    private UserRepo userRepo = new UserRepo();

    public KuknosShowRecoveryKeySVM() {
        if (nextPage == null) {
            nextPage = new MutableLiveData<Boolean>();
            nextPage.setValue(false);
        }
        if (error == null) {
            error = new MutableLiveData<ErrorM>();
        }
        if (kuknosRestoreM == null) {
            kuknosRestoreM = new MutableLiveData<KuknosRestoreM>();
        }
        keys.set(userRepo.getMnemonic());
    }

    public void onNext() {
        // TODO call API
        // Data is Correct & proceed
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //success
                nextPage.setValue(true);
                //error
                /*error.setValue(new ErrorM(true, "Server Error", "1", R.string.kuknos_login_error_server_str));
                progressState.setValue(false);*/
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

    public MutableLiveData<KuknosRestoreM> getKuknosRestoreM() {
        return kuknosRestoreM;
    }

    public void setKuknosRestoreM(MutableLiveData<KuknosRestoreM> kuknosRestoreM) {
        this.kuknosRestoreM = kuknosRestoreM;
    }

    public ObservableField<String> getKeys() {
        return keys;
    }

    public void setKeys(ObservableField<String> keys) {
        this.keys = keys;
    }
}
