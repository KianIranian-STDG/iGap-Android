package net.iGap.kuknos.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import net.iGap.R;
import net.iGap.kuknos.Model.KuknosError;

public class KuknosSetPassVM extends ViewModel {

    private MutableLiveData<KuknosError> error;
    private MutableLiveData<Boolean> nextPage;
    private String PIN;
    private String PIN1;
    private String PIN2;
    private String PIN3;
    private String PIN4;
    private boolean completePin = false;

    public KuknosSetPassVM() {
        error = new MutableLiveData<>();
        nextPage = new MutableLiveData<>();
        nextPage.setValue(false);
    }

    public void onSubmitBtn() {
        if (completePin) {
            PIN = PIN1 + PIN2 + PIN3 + PIN4;
            nextPage.setValue(true);
        } else {
            error.setValue(new KuknosError(true, "Set Pin", "0", R.string.kuknos_SetPass_error));
        }
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

    public MutableLiveData<KuknosError> getError() {
        return error;
    }

    public void setError(MutableLiveData<KuknosError> error) {
        this.error = error;
    }

    public MutableLiveData<Boolean> getNextPage() {
        return nextPage;
    }

    public void setNextPage(MutableLiveData<Boolean> nextPage) {
        this.nextPage = nextPage;
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
}
