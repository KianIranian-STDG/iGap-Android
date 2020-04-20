package net.iGap.viewmodel.kuknos;

import androidx.core.text.HtmlCompat;
import androidx.databinding.ObservableField;
import androidx.lifecycle.MutableLiveData;

import net.iGap.G;
import net.iGap.R;
import net.iGap.api.apiService.BaseAPIViewModel;
import net.iGap.model.kuknos.KuknosError;
import net.iGap.model.kuknos.KuknosSignupM;
import net.iGap.model.kuknos.Parsian.KuknosResponseModel;
import net.iGap.model.kuknos.Parsian.KuknosUsernameStatus;
import net.iGap.observers.interfaces.ResponseCallback;
import net.iGap.repository.kuknos.UserRepo;
import net.iGap.request.RequestInfoPage;

import java.util.Objects;

public class KuknosSignupInfoVM extends BaseAPIViewModel {

    private KuknosSignupM kuknosSignupM;
    private MutableLiveData<KuknosError> error;
    private MutableLiveData<Boolean> nextPage;
    private MutableLiveData<Integer> checkUsernameState;
    private MutableLiveData<Boolean> progressSendDServerState;
    private ObservableField<String> username = new ObservableField<>();
    private ObservableField<String> name = new ObservableField<>();
    private ObservableField<String> phoneNum = new ObservableField<>();
    private ObservableField<String> email = new ObservableField<>();
    private ObservableField<String> NID = new ObservableField<>();
    private boolean usernameIsValid = false;
    private UserRepo userRepo = new UserRepo();
    private MutableLiveData<String> TandCAgree;
    private boolean termsAndConditionIsChecked = false;

    public KuknosSignupInfoVM() {

        UserRepo userRepo = new UserRepo();
        name.set(userRepo.getUserFirstName());
        phoneNum.set(userRepo.getUserNum());
        email.set(userRepo.getUserEmail());

        error = new MutableLiveData<>();
        nextPage = new MutableLiveData<>();
        checkUsernameState = new MutableLiveData<>();
        checkUsernameState.setValue(-1);
        progressSendDServerState = new MutableLiveData<>(false);
        TandCAgree = new MutableLiveData<>(null);
    }

    public void onSubmitBtn() {

        if (!usernameIsValid) {
            isUsernameValid(true);
            return;
        }

        if (!checkEntryData()) {
            return;
        }

        kuknosSignupM = new KuknosSignupM(name.get(), phoneNum.get().replaceFirst("98", "0"), email.get(), NID.get(), userRepo.getAccountID(), username.get(), false);
        sendDataToServer();

    }

    private void sendDataToServer() {
        progressSendDServerState.setValue(true);
        userRepo.registerUser(kuknosSignupM, this, new ResponseCallback<KuknosResponseModel>() {
            @Override
            public void onSuccess(KuknosResponseModel data) {
                kuknosSignupM.setRegistered(true);
                nextPage.setValue(true);
                progressSendDServerState.setValue(false);
            }

            @Override
            public void onError(String errorM) {
                error.setValue(new KuknosError(true, "", errorM, 0));
                progressSendDServerState.setValue(false);
            }

            @Override
            public void onFailed() {
                error.setValue(new KuknosError(true, "", "No connection to server", 0));
                progressSendDServerState.setValue(false);
            }
        });
    }

    public void isUsernameValid(boolean isCallFromBTN) {

//     -1: begin or typing 0 : in progress 1: done & success 2: done and fail

        if (username.get() == null) {
            error.setValue(new KuknosError(true, "empty username", "0", R.string.kuknos_SignupInfo_errorUsernameEmpty));
        } else if (username.get().isEmpty()) {
            error.setValue(new KuknosError(true, "empty username", "0", R.string.kuknos_SignupInfo_errorUsernameEmpty));
        } else if (!username.get().matches("^[A-Za-z][A-Za-z0-9]*") || username.get().length() < 5) {
            error.setValue(new KuknosError(true, "empty username", "0", R.string.kuknos_SignupInfo_errorUsernameRegularExp));
        } else {
            // TODO: fetch data from server for valid username
            checkUsernameServer(isCallFromBTN);
        }
    }

    private boolean checkEntryData() {
        // name
        if (name.get() == null) {
            error.setValue(new KuknosError(true, "Invalid Name Format", "2", R.string.kuknos_SignupInfo_errorNameEmptyInvalid));
            return false;
        }
        if (Objects.requireNonNull(name.get()).isEmpty()) {
            error.setValue(new KuknosError(true, "Invalid Name Format", "2", R.string.kuknos_SignupInfo_errorNameEmptyInvalid));
            return false;
        }
        if (name.get().length() < 3) {
            error.setValue(new KuknosError(true, "Invalid Name Format", "2", R.string.kuknos_SignupInfo_errorNameLengthInvalid));
            return false;
        }
        // NID
        if (NID.get() == null) {
            error.setValue(new KuknosError(true, "Invalid NID Format", "3", R.string.kuknos_SignupInfo_errorNIDFormatInvalid));
            return false;
        }
        if (Objects.requireNonNull(NID.get()).isEmpty()) {
            error.setValue(new KuknosError(true, "Invalid NID Format", "3", R.string.kuknos_SignupInfo_errorNIDFormatInvalid));
            return false;
        }
        if (NID.get().length() != 10) {
            error.setValue(new KuknosError(true, "Invalid NID Length", "3", R.string.kuknos_SignupInfo_errorNIDFormatInvalid));
            return false;
        }
        // email
        if (email.get() == null) {
            error.setValue(new KuknosError(true, "Invalid NID Format", "1", R.string.kuknos_SignupInfo_errorEmailInvalid));
            return false;
        }
        if (Objects.requireNonNull(email.get()).isEmpty()) {
            error.setValue(new KuknosError(true, "Invalid NID Format", "1", R.string.kuknos_SignupInfo_errorEmailInvalid));
            return false;
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email.get()).matches()) {
            error.setValue(new KuknosError(true, "Invalid NID Length", "1", R.string.kuknos_SignupInfo_errorEmailInvalid));
            return false;
        }
        //Terms and Condition
        if (!termsAndConditionIsChecked) {
            error.setValue(new KuknosError(true, "Invalid NID Length", "4", R.string.kuknos_SignupInfo_errorTermAndCondition));
            return false;
        }
        return true;
    }

    public void checkUsernameServer(boolean isCallFromBTN) {
        checkUsernameState.setValue(0);
        userRepo.checkUsername(username.get(), this, new ResponseCallback<KuknosResponseModel<KuknosUsernameStatus>>() {
            @Override
            public void onSuccess(KuknosResponseModel<KuknosUsernameStatus> data) {
                if (!data.getData().isExist()) {
                    checkUsernameState.setValue(1);
                    usernameIsValid = true;
                } else {
                    checkUsernameState.setValue(2);
                }
            }

            @Override
            public void onError(String errorM) {
                checkUsernameState.setValue(-1);
                error.setValue(new KuknosError(true, "Server Error", errorM, 0));
            }

            @Override
            public void onFailed() {
                checkUsernameState.setValue(-1);
                error.setValue(new KuknosError(true, "Server Error", "4", R.string.time_out));
            }
        });
    }

    public void cancelUsernameServer() {
        checkUsernameState.setValue(-1);
    }

    public void getTermsAndCond() {

        if (TandCAgree.getValue() != null && !TandCAgree.getValue().equals("error")) {
            TandCAgree.postValue(TandCAgree.getValue());
            return;
        }
        if (!G.isSecure) {
            TandCAgree.postValue("error");
            return;
        }
        new RequestInfoPage().infoPageAgreementDiscovery("KUKNUS_SIGNUP_AGREEMENT", new RequestInfoPage.OnInfoPage() {
            @Override
            public void onInfo(String body) {
                if (body != null)
                    TandCAgree.postValue(HtmlCompat.fromHtml(body, HtmlCompat.FROM_HTML_MODE_LEGACY).toString());
                else
                    TandCAgree.postValue("error");
            }

            @Override
            public void onError(int major, int minor) {
                TandCAgree.postValue("error");
            }
        });
    }

    //Setter and Getter

    public void termsOnCheckChange(boolean isChecked) {
        termsAndConditionIsChecked = isChecked;
    }

    public KuknosSignupM getKuknosSignupM() {
        return kuknosSignupM;
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

    public MutableLiveData<Integer> getCheckUsernameState() {
        return checkUsernameState;
    }

    public void setCheckUsernameState(MutableLiveData<Integer> checkUsernameState) {
        this.checkUsernameState = checkUsernameState;
    }

    public MutableLiveData<Boolean> getProgressSendDServerState() {
        return progressSendDServerState;
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

    public ObservableField<String> getEmail() {
        return email;
    }

    public void setEmail(ObservableField<String> email) {
        this.email = email;
    }

    public ObservableField<String> getPhoneNum() {
        return phoneNum;
    }

    public ObservableField<String> getNID() {
        return NID;
    }

    public MutableLiveData<String> getTandCAgree() {
        return TandCAgree;
    }
}
