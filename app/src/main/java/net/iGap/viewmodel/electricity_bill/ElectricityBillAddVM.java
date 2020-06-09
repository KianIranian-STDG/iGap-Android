package net.iGap.viewmodel.electricity_bill;

import android.view.View;

import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;
import androidx.databinding.ObservableInt;
import androidx.lifecycle.MutableLiveData;

import net.iGap.R;
import net.iGap.api.apiService.BaseAPIViewModel;
import net.iGap.model.bill.BillInfo;
import net.iGap.model.electricity_bill.ElectricityResponseModel;
import net.iGap.observers.interfaces.ResponseCallback;
import net.iGap.repository.BillsAPIRepository;

public class ElectricityBillAddVM extends BaseAPIViewModel {

    private ObservableField<String> billID;
    private ObservableInt billIDError;
    private ObservableBoolean billIDErrorEnable;

    private ObservableField<String> billName;
    private ObservableInt billNameError;
    private ObservableBoolean billNameErrorEnable;

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

        billName = new ObservableField<>();
        billNameError = new ObservableInt();
        billNameErrorEnable = new ObservableBoolean(false);

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
        info.setTitle(billName.get());
        switch (info.getBillType()) {
            case ELECTRICITY:
                info.setBillID(billID.get());
                break;
            case GAS:
                info.setGasID(billID.get());
                break;
            case MOBILE:
                info.setPhoneNum(billID.get());
                break;
            case PHONE:
                info.setPhoneNum(billID.get().substring(3));
                info.setAreaCode(billID.get().substring(0, 3));
                break;
        }
        if (editMode)
            editAndSaveData();
        else
            sendAndSaveData();
    }

    private void sendAndSaveData() {
        progressVisibility.set(View.VISIBLE);
        new BillsAPIRepository().addBill(info, this, new ResponseCallback<ElectricityResponseModel<String>>() {
            @Override
            public void onSuccess(ElectricityResponseModel<String> data) {
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
        new BillsAPIRepository().editBill(info, this, new ResponseCallback<ElectricityResponseModel<String>>() {
            @Override
            public void onSuccess(ElectricityResponseModel<String> data) {
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
        switch (info.getBillType()) {
            case GAS:
            case ELECTRICITY:
                if (billID.get().length() < 12) {
                    billIDError.set(R.string.elecBill_EntryService_lengthError);
                    billIDErrorEnable.set(true);
                    return false;
                }
                break;
            case PHONE:
            case MOBILE:
                if (billID.get().length() < 11) {
                    billIDError.set(R.string.elecBill_EntryPhone_lengthError);
                    billIDErrorEnable.set(true);
                    return false;
                }
                break;
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

    public void setInfo(BillInfo info) {
        this.info = info;
    }
}
