package net.iGap.kuknos.viewmodel;

import android.os.Handler;

import androidx.databinding.ObservableField;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import net.iGap.R;
import net.iGap.kuknos.service.Repository.UserRepo;
import net.iGap.kuknos.service.model.ErrorM;
import net.iGap.kuknos.service.model.KuknosSignupM;

import java.util.Objects;

public class KuknosSignupInfoVM extends ViewModel {

    private MutableLiveData<KuknosSignupM> kuknosSignupM;
    private MutableLiveData<ErrorM> error;
    private MutableLiveData<Boolean> nextPage;
//    private MutableLiveData<Integer> checkUsernameState;
    private MutableLiveData<Boolean> progressSendDServerState;
//    private ObservableField<String> username = new ObservableField<>();
    private ObservableField<String> name = new ObservableField<>();
    private ObservableField<String> phoneNum = new ObservableField<>();
    private ObservableField<String> email = new ObservableField<>();
    private ObservableField<String> NID = new ObservableField<>();
    private boolean usernameIsValid = false;
    private UserRepo userRepo = new UserRepo();

    public KuknosSignupInfoVM() {

        UserRepo userRepo = new UserRepo();
        name.set(userRepo.getUserFirstName());
        phoneNum.set(userRepo.getUserNum());
        email.set(userRepo.getUserEmail());

        kuknosSignupM = new MutableLiveData<>();
        error = new MutableLiveData<>();
        nextPage = new MutableLiveData<>();
//        checkUsernameState = new MutableLiveData<>();
//        checkUsernameState.setValue(-1);
        progressSendDServerState = new MutableLiveData<>();
        progressSendDServerState.setValue(false);
    }

    public boolean loginStatus() {
        if (userRepo.getSeedKey() != null) {
            return !userRepo.getSeedKey().equals("-1");
        }
        return false;
    }

    public void onSubmitBtn() {

        if (!checkEntryData()) {
            return;
        }

        kuknosSignupM.setValue(new KuknosSignupM(name.get(), phoneNum.get(), email.get(), NID.get()));
        nextPage.setValue(true);

        // this part is for the older version with userID option
        /*if (usernameIsValid) {
            nextPage.setValue(true);
            return;
        }
        isUsernameValid(true);*/

    }

    /*public void isUsernameValid(boolean isCallFromBTN) {

        *//*-1: begin or typing 0 : in progress 1: done & success 2: done and fail*//*

        if (username.get() == null) {
            error.setValue(new ErrorM(true, "empty username", "0", R.string.kuknos_SignupInfo_errorUsernameEmpty));
        } else if (username.get().isEmpty()) {
            error.setValue(new ErrorM(true, "empty username", "0", R.string.kuknos_SignupInfo_errorUsernameEmpty));
        } else {
            // TODO: fetch data from server for valid username
            checkUsernameServer(isCallFromBTN);
        }
    }*/

    private boolean checkEntryData() {
        // name
        if (name.get() == null) {
            error.setValue(new ErrorM(true, "Invalid Name Format", "2", R.string.kuknos_SignupInfo_errorNameEmptyInvalid));
            return false;
        }
        if (Objects.requireNonNull(name.get()).isEmpty()) {
            error.setValue(new ErrorM(true, "Invalid Name Format", "2", R.string.kuknos_SignupInfo_errorNameEmptyInvalid));
            return false;
        }
        if (name.get().length() < 3) {
            error.setValue(new ErrorM(true, "Invalid Name Format", "2", R.string.kuknos_SignupInfo_errorNameLengthInvalid));
            return false;
        }
        // NID
        if (NID.get() == null) {
            error.setValue(new ErrorM(true, "Invalid NID Format", "3", R.string.kuknos_SignupInfo_errorNIDFormatInvalid));
            return false;
        }
        if (Objects.requireNonNull(NID.get()).isEmpty()) {
            error.setValue(new ErrorM(true, "Invalid NID Format", "3", R.string.kuknos_SignupInfo_errorNIDFormatInvalid));
            return false;
        }
        if (NID.get().length() != 10) {
            error.setValue(new ErrorM(true, "Invalid NID Length", "3", R.string.kuknos_SignupInfo_errorNIDFormatInvalid));
            return false;
        }
        // email
        if (email.get() == null) {
            error.setValue(new ErrorM(true, "Invalid NID Format", "1", R.string.kuknos_SignupInfo_errorEmailInvalid));
            return false;
        }
        if (Objects.requireNonNull(email.get()).isEmpty()) {
            error.setValue(new ErrorM(true, "Invalid NID Format", "1", R.string.kuknos_SignupInfo_errorEmailInvalid));
            return false;
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email.get()).matches()) {
            error.setValue(new ErrorM(true, "Invalid NID Length", "1", R.string.kuknos_SignupInfo_errorEmailInvalid));
            return false;
        }
        return true;
    }

    /*public void checkUsernameServer(boolean isCallFromBTN) {
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
                    nextPage.setValue(true);
                //error
//                checkUsernameState.setValue(2);
//                error.setValue(new ErrorM(true, "Server Error", "1", R.string.kuknos_login_error_server_str));
            }
        }, 1000);
    }*/

    /*public void cancelUsernameServer() {
        checkUsernameState.setValue(-1);
    }*/

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

    /*public MutableLiveData<Integer> getCheckUsernameState() {
        return checkUsernameState;
    }

    public void setCheckUsernameState(MutableLiveData<Integer> checkUsernameState) {
        this.checkUsernameState = checkUsernameState;
    }*/

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

    /*public ObservableField<String> getUsername() {
        return username;
    }

    public void setUsername(ObservableField<String> username) {
        this.username = username;
    }*/

    public ObservableField<String> getName() {
        return name;
    }

    public void setName(ObservableField<String> name) {
        this.name = name;
    }

    public ObservableField<String> getEmail() {
        return email;
    }

    public void setEmail(ObservableField<String> email) {
        this.email = email;
    }

    public ObservableField<String> getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(ObservableField<String> phoneNum) {
        this.phoneNum = phoneNum;
    }

    public ObservableField<String> getNID() {
        return NID;
    }

    public void setNID(ObservableField<String> NID) {
        this.NID = NID;
    }
}
