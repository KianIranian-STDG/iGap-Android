package net.iGap.kuknos.viewmodel;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.os.Handler;

import net.iGap.kuknos.service.model.ErrorM;
import net.iGap.kuknos.service.model.KuknosRestoreM;

public class KuknosShowRecoveryKeySVM extends ViewModel {

    private MutableLiveData<KuknosRestoreM> kuknosRestoreM;
    private MutableLiveData<ErrorM> error;
    private MutableLiveData<Boolean> nextPage;
    private String keys;

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

    public String getKeys() {
        return keys;
    }

    public void setKeys(String keys) {
        this.keys = keys;
    }

    public MutableLiveData<KuknosRestoreM> getKuknosRestoreM() {
        return kuknosRestoreM;
    }

    public void setKuknosRestoreM(MutableLiveData<KuknosRestoreM> kuknosRestoreM) {
        this.kuknosRestoreM = kuknosRestoreM;
    }

}
