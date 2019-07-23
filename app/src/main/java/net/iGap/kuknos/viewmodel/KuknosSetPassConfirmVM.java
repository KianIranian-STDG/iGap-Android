package net.iGap.kuknos.viewmodel;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.os.Handler;

import net.iGap.R;
import net.iGap.kuknos.service.model.ErrorM;
import net.iGap.kuknos.service.model.KuknosPassM;

public class KuknosSetPassConfirmVM extends ViewModel {

    private MutableLiveData<KuknosPassM> kuknosPassM;
    private MutableLiveData<ErrorM> error;
    private MutableLiveData<Boolean> nextPage;
    private MutableLiveData<Boolean> progressState;
    private String selectedPin;
    private String PIN;
    private String PIN1;
    private String PIN2;
    private String PIN3;
    private String PIN4;
    private boolean completePin = false;

    public KuknosSetPassConfirmVM() {
        if (kuknosPassM == null) {
            kuknosPassM = new MutableLiveData<KuknosPassM>();
        }
        if (error == null) {
            error = new MutableLiveData<ErrorM>();
        }
        if (nextPage == null) {
            nextPage = new MutableLiveData<Boolean>();
            nextPage.setValue(false);
        }
        if (progressState == null) {
            progressState = new MutableLiveData<>();
        }
    }

    public void onSubmitBtn() {
        if (completePin){
            PIN = PIN1 + PIN2 + PIN3 + PIN4;
            checkPIN();
        }
        else {
            error.setValue(new ErrorM(true, "Set Pin", "0", R.string.kuknos_SetPass_error));
        }
    }

    public void checkPIN() {
        if (PIN.equals(selectedPin)) {
            sendDataToServer();
        }
        else {
            error.setValue(new ErrorM(true, "PIN NOT Match", "0", R.string.kuknos_SetPassConf_error));
        }
    }

    public void sendDataToServer() {
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

            }
        }, 1000);
    }

    // setter and getter

    public String getPIN1() {
        return PIN1;
    }

    public void setPIN1(String PIN1) {
        this.PIN1 = PIN1;
    }

    public String getPIN2() {
        return PIN2;
    }

    public void setPIN2(String PIN2) {
        this.PIN2 = PIN2;
    }

    public String getPIN3() {
        return PIN3;
    }

    public void setPIN3(String PIN3) {
        this.PIN3 = PIN3;
    }

    public String getPIN4() {
        return PIN4;
    }

    public void setPIN4(String PIN4) {
        this.PIN4 = PIN4;
    }

    public MutableLiveData<KuknosPassM> getKuknosPassM() {
        return kuknosPassM;
    }

    public void setKuknosPassM(MutableLiveData<KuknosPassM> kuknosPassM) {
        this.kuknosPassM = kuknosPassM;
    }

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

    public boolean isCompletePin() {
        return completePin;
    }

    public void setCompletePin(boolean completePin) {
        this.completePin = completePin;
    }

    public String getPIN() {
        return PIN;
    }

    public void setPIN(String PIN) {
        this.PIN = PIN;
    }

    public String getSelectedPin() {
        return selectedPin;
    }

    public void setSelectedPin(String selectedPin) {
        this.selectedPin = selectedPin;
    }

    public MutableLiveData<Boolean> getProgressState() {
        return progressState;
    }

    public void setProgressState(MutableLiveData<Boolean> progressState) {
        this.progressState = progressState;
    }
}
