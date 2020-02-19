package net.iGap.viewmodel;

import androidx.lifecycle.MutableLiveData;

import net.iGap.module.accountManager.AccountManager;
import net.iGap.R;
import net.iGap.repository.CPayRepository;
import net.iGap.model.cPay.RegisterPlaqueBodyModel;
import net.iGap.model.cPay.RegisterPlaqueModel;

public class FragmentCPayEditViewModel extends BaseCPayViewModel<RegisterPlaqueModel> {

    private MutableLiveData<Boolean> addCarListener = new MutableLiveData<>();

    private String nameTxt = "", familyTxt = "", nationalIDTxt = "";

    public FragmentCPayEditViewModel() {

    }

    public void onSubmitClicked(boolean isPlaqueCorrect, String plaque) {
        if (isPlaqueCorrect) {

            if (nameTxt.length() != 0 && familyTxt.length() != 0 && nationalIDTxt.length() == 10) {
                registerPlaque(plaque);
            } else {
                getMessageToUser().setValue(R.string.complete_correct);
            }

        } else {
            getMessageToUser().setValue(R.string.plaque_is_not_valid);
        }
    }

    private void registerPlaque(String plaque) {

        String phone = AccountManager.getInstance().getCurrentUser().getPhoneNumber();
        if (phone.startsWith("98")) {
            phone = "0" + phone.substring(2);
        } else if (phone.startsWith("+98")) {
            phone = "0" + phone.substring(3);
        }

        RegisterPlaqueBodyModel body = new RegisterPlaqueBodyModel();
        body.setFirstName(nameTxt);
        body.setLastName(familyTxt);
        body.setNationalId(nationalIDTxt);
        body.setPlaque(plaque + "4");
        body.setMobile(phone);

        getLoaderListener().setValue(true);
        CPayRepository.getInstance().registerNewPlaque(body, this, this);
    }

    public void onNameEditTextChanged(String text) {
        nameTxt = text.trim();
    }

    public void onFamilyEditTextChanged(String text) {
        familyTxt = text.trim();
    }

    public void onNationalIdEditTextChanged(String text) {
        nationalIDTxt = text.trim();
    }

    public MutableLiveData<Boolean> getAddCarListener() {
        return addCarListener;
    }

    @Override
    public void onSuccess(RegisterPlaqueModel data) {
        getMessageToUser().setValue(R.string.plaque_added);
        getLoaderListener().setValue(false);
        addCarListener.postValue(true);
    }

}
