package net.iGap.kuknos.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.databinding.ObservableField;
import android.text.TextUtils;

import net.iGap.R;
import net.iGap.api.apiService.ApiResponse;
import net.iGap.kuknos.service.Repository.UserRepo;
import net.iGap.kuknos.service.model.ErrorM;
import net.iGap.kuknos.service.model.KuknoscheckUserM;

public class KuknosLoginVM extends ViewModel {

    private KuknoscheckUserM kuknoscheckUserM;
    private MutableLiveData<ErrorM> error;
    private MutableLiveData<Boolean> nextPage;
    private MutableLiveData<Integer> progressState;
    private ObservableField<String> ID = new ObservableField<>();
    private ObservableField<String> userNum = new ObservableField<>();
    private UserRepo userRepo = new UserRepo();
    private boolean isRegisteredBefore = false;

    public KuknosLoginVM() {
        error = new MutableLiveData<>();
        nextPage = new MutableLiveData<>();
        nextPage.setValue(false);
        progressState = new MutableLiveData<>();
        userNum.set(userRepo.getUserNum());
    }

    public void onSubmit() {
        if (TextUtils.isEmpty(ID.get())) {
            error.setValue(new ErrorM(true, "Empty Entry", "0", R.string.kuknos_login_error_empty_str));
        }
        else if (ID.get().length() != 10) {
            error.setValue(new ErrorM(true, "Invalid Entry", "0", R.string.kuknos_login_error_invalid_str));
        }
        else {
            if (isRegisteredBefore == true)
                nextPage.setValue(true);
            else
                checkUser();
        }
    }

    private void checkUser() {
        userRepo.checkUser(userNum.get(), ID.get(), new ApiResponse<KuknoscheckUserM>() {
            @Override
            public void onResponse(KuknoscheckUserM kuknoscheckUserM) {
                if (kuknoscheckUserM.getToken()!=null) {
                    KuknosLoginVM.this.kuknoscheckUserM = kuknoscheckUserM;
                    nextPage.setValue(true);
                }
            }

            @Override
            public void onFailed(String error) {
                if (error.equals("registeredBefore"))
                    KuknosLoginVM.this.error.setValue(new ErrorM(true, "Server Error", "1", R.string.kuknos_login_error_server_str));
                else
                    KuknosLoginVM.this.error.setValue(new ErrorM(true, "Server Error", "1", R.string.kuknos_login_error_server_ErrorStr));
            }

            @Override
            public void setProgressIndicator(boolean visibility) {
                if (visibility)
                    progressState.setValue(1);
                else
                    progressState.setValue(0);
            }
        });
    }

    public boolean loginStatus() {
        if (userRepo.getSeedKey() != null) {
            if (!userRepo.getSeedKey().equals("-1"))
                return true;
        }
        return false;
    }

    // Setter and Getter

    public MutableLiveData<ErrorM> getError() {
        return error;
    }

    public MutableLiveData<Boolean> getNextPage() {
        return nextPage;
    }

    public void setNextPage(MutableLiveData<Boolean> nextPage) {
        this.nextPage = nextPage;
    }

    public void setError(MutableLiveData<ErrorM> error) {
        this.error = error;
    }

    public ObservableField<String> getID() {
        return ID;
    }

    public void setID(ObservableField<String> ID) {
        this.ID = ID;
    }

    public ObservableField<String> getUserNum() {
        return userNum;
    }

    public void setUserNum(ObservableField<String> userNum) {
        this.userNum = userNum;
    }

    public MutableLiveData<Integer> getProgressState() {
        return progressState;
    }

    public void setProgressState(MutableLiveData<Integer> progressState) {
        this.progressState = progressState;
    }

    public KuknoscheckUserM getKuknoscheckUserM() {
        return kuknoscheckUserM;
    }

    public void setKuknoscheckUserM(KuknoscheckUserM kuknoscheckUserM) {
        this.kuknoscheckUserM = kuknoscheckUserM;
    }
}
