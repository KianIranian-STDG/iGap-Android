package net.iGap.viewmodel.electricity_bill;

import android.view.View;

import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;
import androidx.databinding.ObservableInt;
import androidx.lifecycle.MutableLiveData;

import net.iGap.R;
import net.iGap.api.apiService.BaseAPIViewModel;
import net.iGap.model.electricity_bill.BillInfo;
import net.iGap.model.electricity_bill.ElectricityResponseModel;
import net.iGap.observers.interfaces.ResponseCallback;
import net.iGap.realm.RealmElectricityBill;
import net.iGap.repository.ElectricityBillAPIRepository;

public class ElectricityBillAddVM extends BaseAPIViewModel {

    private ObservableField<String> billID;
    private ObservableInt billIDError;
    private ObservableBoolean billIDErrorEnable;

    private ObservableField<String> billName;
    private ObservableInt billNameError;
    private ObservableBoolean billNameErrorEnable;

    private ObservableField<String> billPhone;
    private ObservableInt billPhoneError;
    private ObservableBoolean billPhoneErrorEnable;

    private ObservableField<String> billUserID;
    private ObservableInt billUserIDError;
    private ObservableBoolean billUserIDErrorEnable;

    private ObservableField<String> billEmail;
    private ObservableInt billEmailError;
    private ObservableBoolean billEmailErrorEnable;

    private ObservableField<Integer> progressVisibility;

    private MutableLiveData<Boolean> goBack;
    private MutableLiveData<String> errorM;
    private MutableLiveData<String> successM;
    private MutableLiveData<Integer> errorRequestFailed;

    private BillInfo info;
    private boolean editMode = false;

    public ElectricityBillAddVM() {

        billID = new ObservableField<>();
        billIDError = new ObservableInt();
        billIDErrorEnable = new ObservableBoolean(false);

        RealmElectricityBill repo = new RealmElectricityBill();
        billPhone = new ObservableField<>(repo.getUserNum());
        billPhoneError = new ObservableInt();
        billPhoneErrorEnable = new ObservableBoolean(false);

        billName = new ObservableField<>();
        billNameError = new ObservableInt();
        billNameErrorEnable = new ObservableBoolean(false);

        billUserID = new ObservableField<>("0");
        billUserIDError = new ObservableInt();
        billUserIDErrorEnable = new ObservableBoolean(false);

        billEmail = new ObservableField<>(repo.getUserEmail());
        billEmailError = new ObservableInt();
        billEmailErrorEnable = new ObservableBoolean(false);

        progressVisibility = new ObservableField<>(View.GONE);
        goBack = new MutableLiveData<>(false);
        errorM = new MutableLiveData<>();
        successM = new MutableLiveData<>();
        errorRequestFailed = new MutableLiveData<>();

        info = new BillInfo();
    }

    public void addBill() {
        progressVisibility.set(View.VISIBLE);
        if (!checkData()) {
            progressVisibility.set(View.GONE);
            return;
        }
        info.setBillID(billID.get());
        info.setMobileNum(billPhone.get());
//        info.setNID(billUserID.get());
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
                    successM.setValue(data.getMessage());
                goBack.setValue(true);
                progressVisibility.set(View.GONE);
            }

            @Override
            public void onError(String error) {
                progressVisibility.set(View.GONE);
                errorM.setValue(error);
            }

            @Override
            public void onFailed() {
                progressVisibility.set(View.GONE);
                errorRequestFailed.setValue(R.string.connection_error);
            }
        });
    }

    private void editAndSaveData() {
        progressVisibility.set(View.VISIBLE);
        new ElectricityBillAPIRepository().editBill(info, this, new ResponseCallback<ElectricityResponseModel<String>>() {
            @Override
            public void onSuccess(ElectricityResponseModel<String> data) {
                if (data.getStatus() == 200)
                    successM.setValue(data.getMessage());
                goBack.setValue(true);
                progressVisibility.set(View.GONE);
            }

            @Override
            public void onError(String error) {
                progressVisibility.set(View.GONE);
                errorM.setValue(error);
            }

            @Override
            public void onFailed() {
                progressVisibility.set(View.GONE);
                errorRequestFailed.setValue(R.string.connection_error);
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
        /*if (billUserID.get() == null || billUserID.get().isEmpty()) {
            billUserIDError.set(R.string.elecBill_Entry_userIDError);
            billUserIDErrorEnable.set(true);
            return false;
        }
        if (billUserID.get().length() < 10) {
            billUserIDError.set(R.string.elecBill_Entry_userIDLengthError);
            billUserIDErrorEnable.set(true);
            return false;
        }*/
        if (billEmail.get() != null && !billEmail.get().isEmpty()) {
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(billEmail.get()).matches()) {
                billEmailError.set(R.string.elecBill_Entry_emailFormatError);
                billEmailErrorEnable.set(true);
                return false;
            }
//            info.setEmail(billEmail.get());
//            info.setEmailEnable(true);
        }
        return true;
    }

    public ObservableField<String> getBillID() {
        return billID;
    }

    public void setBillID(ObservableField<String> billID) {
        this.billID = billID;
    }

    public ObservableInt getBillIDError() {
        return billIDError;
    }

    public void setBillIDError(ObservableInt billIDError) {
        this.billIDError = billIDError;
    }

    public ObservableBoolean getBillIDErrorEnable() {
        return billIDErrorEnable;
    }

    public void setBillIDErrorEnable(ObservableBoolean billIDErrorEnable) {
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

    public ObservableInt getBillNameError() {
        return billNameError;
    }

    public void setBillNameError(ObservableInt billNameError) {
        this.billNameError = billNameError;
    }

    public ObservableBoolean getBillNameErrorEnable() {
        return billNameErrorEnable;
    }

    public void setBillNameErrorEnable(ObservableBoolean billNameErrorEnable) {
        this.billNameErrorEnable = billNameErrorEnable;
    }

    public ObservableField<String> getBillPhone() {
        return billPhone;
    }

    public void setBillPhone(ObservableField<String> billPhone) {
        this.billPhone = billPhone;
    }

    public ObservableInt getBillPhoneError() {
        return billPhoneError;
    }

    public void setBillPhoneError(ObservableInt billPhoneError) {
        this.billPhoneError = billPhoneError;
    }

    public ObservableBoolean getBillPhoneErrorEnable() {
        return billPhoneErrorEnable;
    }

    public void setBillPhoneErrorEnable(ObservableBoolean billPhoneErrorEnable) {
        this.billPhoneErrorEnable = billPhoneErrorEnable;
    }

    public ObservableField<String> getBillUserID() {
        return billUserID;
    }

    public void setBillUserID(ObservableField<String> billUserID) {
        this.billUserID = billUserID;
    }

    public ObservableInt getBillUserIDError() {
        return billUserIDError;
    }

    public void setBillUserIDError(ObservableInt billUserIDError) {
        this.billUserIDError = billUserIDError;
    }

    public ObservableBoolean getBillUserIDErrorEnable() {
        return billUserIDErrorEnable;
    }

    public void setBillUserIDErrorEnable(ObservableBoolean billUserIDErrorEnable) {
        this.billUserIDErrorEnable = billUserIDErrorEnable;
    }

    public ObservableField<String> getBillEmail() {
        return billEmail;
    }

    public void setBillEmail(ObservableField<String> billEmail) {
        this.billEmail = billEmail;
    }

    public ObservableInt getBillEmailError() {
        return billEmailError;
    }

    public void setBillEmailError(ObservableInt billEmailError) {
        this.billEmailError = billEmailError;
    }

    public ObservableBoolean getBillEmailErrorEnable() {
        return billEmailErrorEnable;
    }

    public void setBillEmailErrorEnable(ObservableBoolean billEmailErrorEnable) {
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

    public MutableLiveData<String> getErrorM() {
        return errorM;
    }

    public void setErrorM(MutableLiveData<String> errorM) {
        this.errorM = errorM;
    }

    public MutableLiveData<String> getSuccessM() {
        return successM;
    }

    public MutableLiveData<Integer> getErrorRequestFailed() {
        return errorRequestFailed;
    }

}
