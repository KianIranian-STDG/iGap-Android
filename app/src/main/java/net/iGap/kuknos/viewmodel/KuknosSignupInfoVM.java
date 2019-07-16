package net.iGap.kuknos.viewmodel;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import net.iGap.R;
import net.iGap.kuknos.service.model.ErrorM;
import net.iGap.kuknos.service.model.KuknosSignupM;

public class KuknosSignupInfoVM extends ViewModel {

    private MutableLiveData<KuknosSignupM> kuknosSignupM;
    private MutableLiveData<ErrorM> error;
    private MutableLiveData<Boolean> nextPage;
    private MutableLiveData<Boolean> AdvancedPage;
    private MutableLiveData<Integer> checkUsernameState;
    private String username;
    private String name;
    private String family;
    private String email;

    public KuknosSignupInfoVM() {
        //TODO clear hard code
        name = "Hossein";
        family = "Amini";

        if (kuknosSignupM == null) {
            kuknosSignupM = new MutableLiveData<KuknosSignupM>();
        }
        if (error == null) {
            error = new MutableLiveData<ErrorM>();
        }
        if (nextPage == null) {
            nextPage = new MutableLiveData<Boolean>();
        }
        if (AdvancedPage == null) {
            AdvancedPage = new MutableLiveData<Boolean>();
        }
        if (checkUsernameState == null) {
            checkUsernameState = new MutableLiveData<Integer>();
            checkUsernameState.setValue(-1);
        }
    }

    public void onSubmitBtn() {

        checkUsername();
        checkEmail();

    }

    public void onAdvancedSecurity() {

    }

    public void checkUsername() {

        /*-1: begin or typing 0 : in progress 1: done & success 2: done and fail*/
        if (username == null) {
            error.setValue(new ErrorM(true, "empty username", "0", R.string.kuknos_SignupInfo_errorUsernameEmpty));
        }
        else if (username.isEmpty()) {
            error.setValue(new ErrorM(true, "empty username", "0", R.string.kuknos_SignupInfo_errorUsernameEmpty));
        }
        else {
            // TODO: fetch data from server for valid username
            checkUsernameState.setValue(0);
        }

    }

    private void checkEmail() {
        if (email!= null) {
            if (!email.isEmpty()) {
                if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    error.setValue(new ErrorM(true, "Invalid Email Format", "1", R.string.kuknos_SignupInfo_errorEmailInvalid));
                }
            }
        }
    }

    public MutableLiveData<KuknosSignupM> getKuknosSignupM() {
        return kuknosSignupM;
    }

    public void setKuknosSignupM(MutableLiveData<KuknosSignupM> kuknosSignupM) {
        this.kuknosSignupM = kuknosSignupM;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFamily() {
        return family;
    }

    public void setFamily(String family) {
        this.family = family;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public MutableLiveData<Boolean> getAdvancedPage() {
        return AdvancedPage;
    }

    public void setAdvancedPage(MutableLiveData<Boolean> advancedPage) {
        AdvancedPage = advancedPage;
    }

    public MutableLiveData<Integer> getCheckUsernameState() {
        return checkUsernameState;
    }

    public void setCheckUsernameState(MutableLiveData<Integer> checkUsernameState) {
        this.checkUsernameState = checkUsernameState;
    }
}
