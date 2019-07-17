package net.iGap.kuknos.viewmodel;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.os.Handler;

import net.iGap.R;
import net.iGap.kuknos.service.model.ErrorM;
import net.iGap.kuknos.service.model.KuknosRestoreM;

public class KuknosShowRecoveryKeyVM extends ViewModel {

    private MutableLiveData<KuknosRestoreM> kuknosRestoreM;
    private MutableLiveData<ErrorM> error;
    private MutableLiveData<Boolean> nextPage;
    private MutableLiveData<Boolean> advancedPage;
    private MutableLiveData<Boolean> progressState;
    private String keys;

    public KuknosShowRecoveryKeyVM() {
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
        if (advancedPage == null) {
            advancedPage = new MutableLiveData<Boolean>();
        }
        if (progressState == null) {
            progressState = new MutableLiveData<Boolean>();
            progressState.setValue(false);
        }
    }

    public void onAdvancedSecurity() {
        advancedPage.setValue(true);
    }

    public void onNext() {
        progressState.setValue(true);
        // TODO call API
        // Data is Correct & proceed
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //success
                /*progressState.setValue(false);
                nextPage.setValue(true);*/
                //error
                error.setValue(new ErrorM(true, "Server Error", "1", R.string.kuknos_login_error_server_str));
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

    public MutableLiveData<Boolean> getProgressState() {
        return progressState;
    }

    public void setProgressState(MutableLiveData<Boolean> progressState) {
        this.progressState = progressState;
    }

    public MutableLiveData<Boolean> getAdvancedPage() {
        return advancedPage;
    }

    public void setAdvancedPage(MutableLiveData<Boolean> advancedPage) {
        this.advancedPage = advancedPage;
    }
}
