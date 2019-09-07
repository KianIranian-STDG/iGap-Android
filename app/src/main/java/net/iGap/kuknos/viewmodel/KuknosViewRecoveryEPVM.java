package net.iGap.kuknos.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.databinding.ObservableField;
import android.os.Handler;

import net.iGap.R;
import net.iGap.kuknos.service.model.ErrorM;

public class KuknosViewRecoveryEPVM extends ViewModel {

    private ObservableField<String> PIN = new ObservableField<>();
    private MutableLiveData<ErrorM> error;
    private MutableLiveData<Boolean> progressState;
    private MutableLiveData<Boolean> nextPage;

    public KuknosViewRecoveryEPVM() {
        if (error == null)
            error = new MutableLiveData<>();
        if (progressState == null) {
            progressState = new MutableLiveData<>();
            progressState.setValue(false);
        }if (nextPage == null) {
            nextPage = new MutableLiveData<>();
            nextPage.setValue(false);
        }
    }

    public void onSubmitBtn() {
        if (!checkEntry())
            return;
        sendDataServer();
    }

    private void sendDataServer() {
        progressState.setValue(true);
        // TODO: send data to server
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                progressState.setValue(false);

                //success
                nextPage.setValue(true);
                //error
                //error.setValue(new ErrorM(true, "wrong pin", "1", R.string.kuknos_viewRecoveryEP_wrongPINE));
            }
        }, 1000);
    }

    private boolean checkEntry() {
        if (PIN.get() == null) {
            // empty
            error.setValue(new ErrorM(true, "empty pin", "0", R.string.kuknos_viewRecoveryEP_emptyPIN));
            return false;
        }
        if (PIN.get().isEmpty()) {
            // empty
            error.setValue(new ErrorM(true, "empty pin", "0", R.string.kuknos_viewRecoveryEP_emptyPIN));
            return false;
        }
        if (PIN.get().length() != 4) {
            error.setValue(new ErrorM(true, "wrong length pin", "0", R.string.kuknos_viewRecoveryEP_wrongPIN));
            return false;
        }
        return true;
    }

    public ObservableField<String> getPIN() {
        return PIN;
    }

    public void setPIN(ObservableField<String> PIN) {
        this.PIN = PIN;
    }

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

    public MutableLiveData<Boolean> getNextPage() {
        return nextPage;
    }

    public void setNextPage(MutableLiveData<Boolean> nextPage) {
        this.nextPage = nextPage;
    }
}
