package net.iGap.kuknos.viewmodel;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.os.Handler;
import android.text.TextUtils;

import net.iGap.G;
import net.iGap.R;
import net.iGap.kuknos.service.model.ErrorM;
import net.iGap.kuknos.service.model.KuknosLoginM;

import java.util.Objects;

public class KuknosLoginVM extends ViewModel {

    private MutableLiveData<KuknosLoginM> loginData;
    private MutableLiveData<ErrorM> error;
    private MutableLiveData<Boolean> nextPage;
    private MutableLiveData<Boolean> progressState;
    private String ID;
    private String userNum;

    public KuknosLoginVM() {
        // TODO clear hard code
        userNum = "09376290072";
        if (loginData == null) {
            loginData = new MutableLiveData<KuknosLoginM>();
        }
        if (error == null) {
            error = new MutableLiveData<ErrorM>();
        }
        if (nextPage == null) {
            nextPage = new MutableLiveData<Boolean>();
            nextPage.setValue(false);
        }
        if (progressState == null) {
            progressState = new MutableLiveData<Boolean>();
            progressState.setValue(false);
        }
    }

    public void onSubmit() {
        loginData.setValue(new KuknosLoginM(userNum, ID));
        if (TextUtils.isEmpty(Objects.requireNonNull(loginData).getValue().getUserID())) {
            error.setValue(new ErrorM(true, "Empty Entry", "0", R.string.kuknos_login_error_empty_str));
        }
        else if (!loginData.getValue().isUserIDValid()) {
            error.setValue(new ErrorM(true, "Invalid Entry", "0", R.string.kuknos_login_error_invalid_str));
        }
        else {
            progressState.setValue(true);
            // TODO call API
            // Data is Correct & proceed
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    //success
                    progressState.setValue(false);
                    nextPage.setValue(true);
                    //error
                    /*error.setValue(new ErrorM(true, "Server Error", "1", R.string.kuknos_login_error_server_str));
                    progressState.setValue(false);*/
                }
            }, 1000);
        }
    }

    // Setter and Getter

    public String getUserNum() {
        return userNum;
    }

    public void setUserNum(String userNum) {
        this.userNum = userNum;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public MutableLiveData<KuknosLoginM> getLoginData() {
        return loginData;
    }

    public MutableLiveData<ErrorM> getError() {
        return error;
    }

    public MutableLiveData<Boolean> getNextPage() {
        return nextPage;
    }

    public void setNextPage(MutableLiveData<Boolean> nextPage) {
        this.nextPage = nextPage;
    }

    public void setLoginData(MutableLiveData<KuknosLoginM> loginData) {
        this.loginData = loginData;
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
