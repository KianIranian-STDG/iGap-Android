package net.iGap.electricity_bill.viewmodel;

import android.view.View;

import androidx.databinding.ObservableField;
import androidx.lifecycle.MutableLiveData;

import net.iGap.R;
import net.iGap.api.apiService.BaseAPIViewModel;
import net.iGap.api.apiService.ResponseCallback;
import net.iGap.api.errorhandler.ErrorModel;
import net.iGap.electricity_bill.repository.api.ElectricityBillAPIRepository;
import net.iGap.electricity_bill.repository.api.ElectricityBillRealmRepo;
import net.iGap.electricity_bill.repository.model.BillRegister;
import net.iGap.electricity_bill.repository.model.ElectricityResponseModel;

public class ElectricityBillAddVM extends BaseAPIViewModel {

    private ObservableField<String> billID;
    private ObservableField<Integer> billIDError;
    private ObservableField<Boolean> billIDErrorEnable;

    private ObservableField<String> billName;
    private ObservableField<Integer> billNameError;
    private ObservableField<Boolean> billNameErrorEnable;

    private ObservableField<String> billPhone;
    private ObservableField<Integer> billPhoneError;
    private ObservableField<Boolean> billPhoneErrorEnable;

    private ObservableField<String> billUserID;
    private ObservableField<Integer> billUserIDError;
    private ObservableField<Boolean> billUserIDErrorEnable;

    private ObservableField<String> billEmail;
    private ObservableField<Integer> billEmailError;
    private ObservableField<Boolean> billEmailErrorEnable;

    private ObservableField<Integer> progressVisibility;

    private MutableLiveData<Boolean> goBack;
    private MutableLiveData<ErrorModel> errorM;

    private BillRegister info;
    private boolean editMode = false;

    public ElectricityBillAddVM() {

        billID = new ObservableField<>();
        billIDError = new ObservableField<>();
        billIDErrorEnable = new ObservableField<>(false);

        ElectricityBillRealmRepo repo = new ElectricityBillRealmRepo();
        billPhone = new ObservableField<>(repo.getUserNum());
        billPhoneError = new ObservableField<>();
        billPhoneErrorEnable = new ObservableField<>(false);

        billName = new ObservableField<>();
        billNameError = new ObservableField<>();
        billNameErrorEnable = new ObservableField<>(false);

        billUserID = new ObservableField<>();
        billUserIDError = new ObservableField<>();
        billUserIDErrorEnable = new ObservableField<>(false);

        billEmail = new ObservableField<>(repo.getUserEmail());
        billEmailError = new ObservableField<>();
        billEmailErrorEnable = new ObservableField<>(false);

        progressVisibility = new ObservableField<>(View.GONE);
        goBack = new MutableLiveData<>(false);
        errorM = new MutableLiveData<>();

        info = new BillRegister();
        info.setEmailEnable(false);
        info.setAppEnable(false);
        info.setPrintEnable(false);
        info.setSMSEnable(false);
    }

    public void addBill() {
        progressVisibility.set(View.VISIBLE);
        if (!checkData()) {
            progressVisibility.set(View.GONE);
            return;
        }
        info.setID(billID.get());
        info.setMobileNum(billPhone.get());
        info.setNID(billUserID.get());
        info.setTitle(billName.get());

        if (editMode)
            editAndSaveData();
        else
            sendAndSaveData();
    }

    private void sendAndSaveData() {
        progressVisibility.set(View.VISIBLE);
        new ElectricityBillAPIRepository().addBill(info, this, new ResponseCallback<ElectricityResponseModel<String>>() {
            @Override
            public void onSuccess(ElectricityResponseModel<String> data) {
                if (data.getStatus() == 200)
                    errorM.setValue(new ErrorModel("200", data.getMessage()));
                goBack.setValue(true);
                progressVisibility.set(View.GONE);
            }

            @Override
            public void onError(ErrorModel error) {
                progressVisibility.set(View.GONE);
                errorM.setValue(error);
            }
        });
    }

    private void editAndSaveData() {
        progressVisibility.set(View.VISIBLE);
        new ElectricityBillAPIRepository().editBill(info, this, new ResponseCallback<ElectricityResponseModel<String>>() {
            @Override
            public void onSuccess(ElectricityResponseModel<String> data) {
                if (data.getStatus() == 200)
                    errorM.setValue(new ErrorModel("200", data.getMessage()));
                goBack.setValue(true);
                progressVisibility.set(View.GONE);
            }

            @Override
            public void onError(ErrorModel error) {
                progressVisibility.set(View.GONE);
                errorM.setValue(error);
            }
        });
    }

    private boolean checkData() {
        if (billName.get() == null || billName.get().isEmpty()) {
            billNameError.set(R.string.elecBill_Entry_nameError);
            billNameErrorEnable.set(true);
            return false;
        }
        if (billID.get() == null || billID.get().isEmpty()) {
            billIDError.set(R.string.elecBill_Entry_billIDError);
            billIDErrorEnable.set(true);
            return false;
        }
        if (billID.get().length() < 13) {
            billIDError.set(R.string.elecBill_Entry_lengthError);
            billIDErrorEnable.set(true);
            return false;
        }
        if (billPhone.get() == null || billPhone.get().isEmpty()) {
            billPhoneError.set(R.string.elecBill_Entry_phoneError);
            billPhoneErrorEnable.set(true);
            return false;
        }
        if (billPhone.get().length() < 11) {
            billPhoneError.set(R.string.elecBill_Entry_phoneLengthError);
            billPhoneErrorEnable.set(true);
            return false;
        }
        if (!(billPhone.get().startsWith("09") || billPhone.get().startsWith("98"))) {
            billPhoneError.set(R.string.elecBill_Entry_phoneFormatError);
            billPhoneErrorEnable.set(true);
            return false;
        }
        if (billUserID.get() == null || billUserID.get().isEmpty()) {
            billUserIDError.set(R.string.elecBill_Entry_userIDError);
            billUserIDErrorEnable.set(true);
            return false;
        }
        if (billUserID.get().length() < 10) {
            billUserIDError.set(R.string.elecBill_Entry_userIDLengthError);
            billUserIDErrorEnable.set(true);
            return false;
        }
        if (billEmail.get() != null && !billEmail.get().isEmpty()) {
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(billEmail.get()).matches()) {
                billEmailError.set(R.string.elecBill_Entry_emailFormatError);
                billEmailErrorEnable.set(true);
                return false;
            }
            info.setEmail(billEmail.get());
            info.setEmailEnable(true);
        }
        return true;
    }

    public ObservableField<String> getBillID() {
        return billID;
    }

    public void setBillID(ObservableField<String> billID) {
        this.billID = billID;
    }

    public ObservableField<Integer> getBillIDError() {
        return billIDError;
    }

    public void setBillIDError(ObservableField<Integer> billIDError) {
        this.billIDError = billIDError;
    }

    public ObservableField<Boolean> getBillIDErrorEnable() {
        return billIDErrorEnable;
    }

    public void setBillIDErrorEnable(ObservableField<Boolean> billIDErrorEnable) {
        this.billIDErrorEnable = billIDErrorEnable;
    }

    public ObservableField<Integer> getProgressVisibility() {
        return progressVisibility;
    }

    public void setProgressVisibility(ObservableField<Integer> progressVisibility) {
        this.progressVisibility = progressVisibility;
    }

    public ObservableField<String> getBillName() {
        return billName;
    }

    public void setBillName(ObservableField<String> billName) {
        this.billName = billName;
    }

    public ObservableField<Integer> getBillNameError() {
        return billNameError;
    }

    public void setBillNameError(ObservableField<Integer> billNameError) {
        this.billNameError = billNameError;
    }

    public ObservableField<Boolean> getBillNameErrorEnable() {
        return billNameErrorEnable;
    }

    public void setBillNameErrorEnable(ObservableField<Boolean> billNameErrorEnable) {
        this.billNameErrorEnable = billNameErrorEnable;
    }

    public ObservableField<String> getBillPhone() {
        return billPhone;
    }

    public void setBillPhone(ObservableField<String> billPhone) {
        this.billPhone = billPhone;
    }

    public ObservableField<Integer> getBillPhoneError() {
        return billPhoneError;
    }

    public void setBillPhoneError(ObservableField<Integer> billPhoneError) {
        this.billPhoneError = billPhoneError;
    }

    public ObservableField<Boolean> getBillPhoneErrorEnable() {
        return billPhoneErrorEnable;
    }

    public void setBillPhoneErrorEnable(ObservableField<Boolean> billPhoneErrorEnable) {
        this.billPhoneErrorEnable = billPhoneErrorEnable;
    }

    public ObservableField<String> getBillUserID() {
        return billUserID;
    }

    public void setBillUserID(ObservableField<String> billUserID) {
        this.billUserID = billUserID;
    }

    public ObservableField<Integer> getBillUserIDError() {
        return billUserIDError;
    }

    public void setBillUserIDError(ObservableField<Integer> billUserIDError) {
        this.billUserIDError = billUserIDError;
    }

    public ObservableField<Boolean> getBillUserIDErrorEnable() {
        return billUserIDErrorEnable;
    }

    public void setBillUserIDErrorEnable(ObservableField<Boolean> billUserIDErrorEnable) {
        this.billUserIDErrorEnable = billUserIDErrorEnable;
    }

    public ObservableField<String> getBillEmail() {
        return billEmail;
    }

    public void setBillEmail(ObservableField<String> billEmail) {
        this.billEmail = billEmail;
    }

    public ObservableField<Integer> getBillEmailError() {
        return billEmailError;
    }

    public void setBillEmailError(ObservableField<Integer> billEmailError) {
        this.billEmailError = billEmailError;
    }

    public ObservableField<Boolean> getBillEmailErrorEnable() {
        return billEmailErrorEnable;
    }

    public void setBillEmailErrorEnable(ObservableField<Boolean> billEmailErrorEnable) {
        this.billEmailErrorEnable = billEmailErrorEnable;
    }

    public MutableLiveData<Boolean> getGoBack() {
        return goBack;
    }

    public void setGoBack(MutableLiveData<Boolean> goBack) {
        this.goBack = goBack;
    }

    public boolean isEditMode() {
        return editMode;
    }

    public void setEditMode(boolean editMode) {
        this.editMode = editMode;
    }

    public MutableLiveData<ErrorModel> getErrorM() {
        return errorM;
    }

    public void setErrorM(MutableLiveData<ErrorModel> errorM) {
        this.errorM = errorM;
    }
}
