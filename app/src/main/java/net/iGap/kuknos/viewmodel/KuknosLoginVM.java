package net.iGap.kuknos.viewmodel;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.text.TextUtils;

import net.iGap.G;
import net.iGap.R;
import net.iGap.kuknos.service.model.ErrorM;
import net.iGap.kuknos.service.model.KuknosLoginM;

import java.util.Objects;

public class KuknosLoginVM extends ViewModel {

    private MutableLiveData<KuknosLoginM> loginData;
    private MutableLiveData<ErrorM> error;
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
    }

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

    public void onSubmit() {
        loginData.setValue(new KuknosLoginM(userNum, ID));
        if (TextUtils.isEmpty(Objects.requireNonNull(loginData).getValue().getUserID())) {
            error.setValue(new ErrorM(true, "Empty Entry", "", R.string.kuknos_login_error_empty_str));

        }
        else if (!loginData.getValue().isUserIDValid()) {
            error.setValue(new ErrorM(true, "Invalid Entry", "", R.string.kuknos_login_error_invalid_str));
        }
        else {
            // Data is Correct & proceed

        }
    }

    public MutableLiveData<KuknosLoginM> getLoginData() {
        return loginData;
    }

    public MutableLiveData<ErrorM> getError() {
        return error;
    }
}
