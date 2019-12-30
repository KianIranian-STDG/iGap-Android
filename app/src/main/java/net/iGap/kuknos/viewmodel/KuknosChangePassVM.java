package net.iGap.kuknos.viewmodel;

import android.os.Handler;
import android.util.Log;

import androidx.databinding.ObservableField;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import net.iGap.R;
import net.iGap.kuknos.service.model.ErrorM;

public class KuknosChangePassVM extends ViewModel {

    private ObservableField<String> currentPIN = new ObservableField<>();
    private ObservableField<String> newPIN = new ObservableField<>();
    private ObservableField<String> rnewPIN = new ObservableField<>();
    private MutableLiveData<ErrorM> error;
    private MutableLiveData<Boolean> progressState;

    public KuknosChangePassVM() {
        error = new MutableLiveData<>();
        progressState = new MutableLiveData<>();
        progressState.setValue(false);
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
                error.setValue(new ErrorM(false, "change success", "3", R.string.kuknos_changePIN_successM));
                //error
                //error.setValue(new ErrorM(true, "failed success", "3", R.string.kuknos_changePIN_failM));
            }
        }, 1000);
    }

    private boolean checkEntry() {
        Log.d("amini", "checkEntry: " + currentPIN.get());
        if (currentPIN.get() == null) {
            // empty
            error.setValue(new ErrorM(true, "empty current pin", "0", R.string.kuknos_changePIN_emptyCP));
            return false;
        }
        if (currentPIN.get().isEmpty()) {
            // empty
            error.setValue(new ErrorM(true, "empty current pin", "0", R.string.kuknos_changePIN_emptyCP));
            return false;
        }
        if (currentPIN.get().length() != 4) {
            error.setValue(new ErrorM(true, "empty current pin", "0", R.string.kuknos_changePIN_wrongCP));
            return false;
        }
        if (newPIN.get() == null) {
            // empty
            error.setValue(new ErrorM(true, "empty new pin", "1", R.string.kuknos_changePIN_emptyNP));
            return false;
        }
        if (newPIN.get().isEmpty()) {
            // empty
            error.setValue(new ErrorM(true, "empty new pin", "1", R.string.kuknos_changePIN_emptyNP));
            return false;
        }
        if (newPIN.get().length() != 4) {
            error.setValue(new ErrorM(true, "empty current pin", "1", R.string.kuknos_changePIN_wrongNP));
            return false;
        }
        if (rnewPIN.get() == null) {
            // empty
            error.setValue(new ErrorM(true, "empty conf new pin", "2", R.string.kuknos_changePIN_emptyRNP));
            return false;
        }
        if (rnewPIN.get().isEmpty()) {
            // empty
            error.setValue(new ErrorM(true, "empty conf new pin", "2", R.string.kuknos_changePIN_emptyRNP));
            return false;
        }
        if (rnewPIN.get().length() != 4) {
            error.setValue(new ErrorM(true, "empty current pin", "2", R.string.kuknos_changePIN_wrongRNP));
            return false;
        }
        if (!newPIN.get().equals(rnewPIN.get())) {
            // np match
            error.setValue(new ErrorM(true, "not equal", "2", R.string.kuknos_changePIN_notEqual));
            return false;
        }
        return true;
    }

    public ObservableField<String> getCurrentPIN() {
        return currentPIN;
    }

    public ObservableField<String> getNewPIN() {
        return newPIN;
    }

    public ObservableField<String> getRnewPIN() {
        return rnewPIN;
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
}
