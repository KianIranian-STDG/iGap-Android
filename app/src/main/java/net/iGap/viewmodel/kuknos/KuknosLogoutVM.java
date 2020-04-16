package net.iGap.viewmodel.kuknos;

import android.os.Handler;

import androidx.databinding.ObservableField;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import net.iGap.R;
import net.iGap.model.kuknos.KuknosError;
import net.iGap.repository.kuknos.UserRepo;

public class KuknosLogoutVM extends ViewModel {

    private ObservableField<String> PIN = new ObservableField<>();
    private MutableLiveData<KuknosError> error;
    private MutableLiveData<Boolean> progressState;
    private MutableLiveData<Boolean> nextPage;
    private UserRepo userRepo = new UserRepo();

    public KuknosLogoutVM() {
        error = new MutableLiveData<>();
        progressState = new MutableLiveData<>();
        progressState.setValue(false);
        nextPage = new MutableLiveData<>();
        nextPage.setValue(false);
    }

    public void onSubmitBtn() {
        if (!checkEntry())
            return;
        sendDataServer();
    }

    private void sendDataServer() {
        progressState.setValue(true);
        userRepo.deleteAccount();
        Handler handler = new Handler();
        handler.postDelayed(() -> {
            progressState.setValue(false);
            nextPage.setValue(true);
        }, 1000);
    }

    private boolean checkEntry() {
        if (PIN.get() == null) {
            // empty
            error.setValue(new KuknosError(true, "empty pin", "0", R.string.kuknos_viewRecoveryEP_emptyPIN));
            return false;
        }
        if (PIN.get().isEmpty()) {
            // empty
            error.setValue(new KuknosError(true, "empty pin", "0", R.string.kuknos_viewRecoveryEP_emptyPIN));
            return false;
        }
        if (PIN.get().length() != 4) {
            error.setValue(new KuknosError(true, "wrong length pin", "0", R.string.kuknos_viewRecoveryEP_wrongPIN));
            return false;
        }
        if (userRepo.getPIN() != null && !userRepo.getPIN().equals("-1") && !PIN.get().equals(userRepo.getPIN())) {
            error.setValue(new KuknosError(true, "wrong length pin", "1", R.string.kuknos_viewRecoveryEP_wrongPINE));
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

    public MutableLiveData<Boolean> getNextPage() {
        return nextPage;
    }

    public void setNextPage(MutableLiveData<Boolean> nextPage) {
        this.nextPage = nextPage;
    }
}
