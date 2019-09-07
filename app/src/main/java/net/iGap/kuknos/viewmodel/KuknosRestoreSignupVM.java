package net.iGap.kuknos.viewmodel;

import android.os.Handler;

import androidx.databinding.ObservableField;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import net.iGap.R;
import net.iGap.api.apiService.ApiResponse;
import net.iGap.kuknos.service.Repository.UserRepo;
import net.iGap.kuknos.service.model.ErrorM;
import net.iGap.kuknos.service.model.KuknosSignupM;
import net.iGap.kuknos.service.model.KuknosSubmitM;

public class KuknosRestoreSignupVM extends ViewModel {

    private MutableLiveData<KuknosSignupM> kuknosSignupM;
    private MutableLiveData<ErrorM> error;
    private MutableLiveData<Boolean> nextPage;
    private MutableLiveData<Integer> checkUsernameState;
    private MutableLiveData<Boolean> progressSendDServerState;
    private ObservableField<String> username = new ObservableField<>();
    private ObservableField<String> name = new ObservableField<>();
    private ObservableField<String> family = new ObservableField<>();
    private ObservableField<String> email = new ObservableField<>();
    private boolean usernameIsValid = false;
    private String token;
    private UserRepo userRepo = new UserRepo();

    public KuknosRestoreSignupVM() {

        name.set(userRepo.getUserFirstName());
        family.set(userRepo.getUserLastName());
        email.set(userRepo.getUserEmail());

        kuknosSignupM = new MutableLiveData<>();
        error = new MutableLiveData<>();
        nextPage = new MutableLiveData<>();
        checkUsernameState = new MutableLiveData<>();
        checkUsernameState.setValue(-1);
        progressSendDServerState = new MutableLiveData<>();
        progressSendDServerState.setValue(false);
    }

    public void onSubmitBtn() {

        if (!checkEmail()) {
            return;
        }
        if (usernameIsValid) {
            submitUser();
            return;
        }
        isUsernameValid(true);

    }

    public void isUsernameValid(boolean isCallFromBTN) {

        /*-1: begin or typing 0 : in progress 1: done & success 2: done and fail*/

        if (username.get() == null) {
            error.setValue(new ErrorM(true, "empty username", "0", R.string.kuknos_SignupInfo_errorUsernameEmpty));
        } else if (username.get().isEmpty()) {
            error.setValue(new ErrorM(true, "empty username", "0", R.string.kuknos_SignupInfo_errorUsernameEmpty));
        } else {
            // TODO: fetch data from server for valid username
            checkUsernameServer(isCallFromBTN);
        }
    }

    private boolean checkEmail() {
        if (email.get() != null) {
            if (!email.get().isEmpty()) {
                if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email.get()).matches()) {
                    error.setValue(new ErrorM(true, "Invalid Email Format", "1", R.string.kuknos_SignupInfo_errorEmailInvalid));
                    return false;
                } else
                    return true;
            } else
                return true;
        } else
            return true;
    }

    public void checkUsernameServer(boolean isCallFromBTN) {
        checkUsernameState.setValue(0);
        // TODO check from server for avalibility
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //success
                checkUsernameState.setValue(1);
                usernameIsValid = true;
                if (isCallFromBTN)
                    submitUser();
                //error
//                checkUsernameState.setValue(2);
//                error.setValue(new ErrorM(true, "Server Error", "1", R.string.kuknos_login_error_server_str));
            }
        }, 1000);
    }

    private void submitUser() {
        userRepo.registerUser(token, userRepo.getAccountID(), username.get(), new ApiResponse<KuknosSubmitM>() {
            @Override
            public void onResponse(KuknosSubmitM kuknosSubmitM) {
                if (kuknosSubmitM.getOk() == 1) {
                    nextPage.setValue(true);
                }
            }

            @Override
            public void onFailed(String error) {
                // TODO delete this fucking shit
                nextPage.setValue(true);
            }

            @Override
            public void setProgressIndicator(boolean visibility) {
                progressSendDServerState.setValue(visibility);
            }
        });
    }

    public void cancelUsernameServer() {
        checkUsernameState.setValue(-1);
        // TODO cancel current API checking
    }

    //Setter and Getter

    public MutableLiveData<KuknosSignupM> getKuknosSignupM() {
        return kuknosSignupM;
    }

    public void setKuknosSignupM(MutableLiveData<KuknosSignupM> kuknosSignupM) {
        this.kuknosSignupM = kuknosSignupM;
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

    public MutableLiveData<Integer> getCheckUsernameState() {
        return checkUsernameState;
    }

    public void setCheckUsernameState(MutableLiveData<Integer> checkUsernameState) {
        this.checkUsernameState = checkUsernameState;
    }

    public MutableLiveData<Boolean> getProgressSendDServerState() {
        return progressSendDServerState;
    }

    public void setProgressSendDServerState(MutableLiveData<Boolean> progressSendDServerState) {
        this.progressSendDServerState = progressSendDServerState;
    }

    public boolean isUsernameIsValid() {
        return usernameIsValid;
    }

    public void setUsernameIsValid(boolean usernameIsValid) {
        this.usernameIsValid = usernameIsValid;
    }

    public ObservableField<String> getUsername() {
        return username;
    }

    public void setUsername(ObservableField<String> username) {
        this.username = username;
    }

    public ObservableField<String> getName() {
        return name;
    }

    public void setName(ObservableField<String> name) {
        this.name = name;
    }

    public ObservableField<String> getFamily() {
        return family;
    }

    public void setFamily(ObservableField<String> family) {
        this.family = family;
    }

    public ObservableField<String> getEmail() {
        return email;
    }

    public void setEmail(ObservableField<String> email) {
        this.email = email;
    }


}
