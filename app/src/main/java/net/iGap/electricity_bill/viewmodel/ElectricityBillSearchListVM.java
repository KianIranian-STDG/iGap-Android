package net.iGap.electricity_bill.viewmodel;

import android.view.View;

import androidx.databinding.ObservableField;
import androidx.lifecycle.MutableLiveData;

import net.iGap.R;
import net.iGap.api.apiService.BaseAPIViewModel;
import net.iGap.api.apiService.ResponseCallback;
import net.iGap.api.errorhandler.ErrorModel;
import net.iGap.electricity_bill.repository.api.ElectricityBillAPIRepository;
import net.iGap.electricity_bill.repository.model.BranchData;
import net.iGap.electricity_bill.repository.model.CompanyList;
import net.iGap.electricity_bill.repository.model.ElectricityResponseList;

import java.util.List;

public class ElectricityBillSearchListVM extends BaseAPIViewModel {

    private MutableLiveData<CompanyList> mCompanyData;
    private MutableLiveData<List<BranchData>> mBranchData;
    private MutableLiveData<ErrorModel> errorM;

    private ObservableField<String> billSerial;
    private ObservableField<Integer> billSerialError;
    private ObservableField<Boolean> billSerialErrorEnable;
    private ObservableField<Integer> progressVisibility;
    private ObservableField<Integer> errorVisibility;

    private int companyPosition = -1;
    private int tryOut = 3;

    public ElectricityBillSearchListVM() {
        mCompanyData = new MutableLiveData<>();
        mBranchData = new MutableLiveData<>();
        errorM = new MutableLiveData<>();

        billSerial = new ObservableField<>();
        billSerialError = new ObservableField<>();
        billSerialErrorEnable = new ObservableField<>();
        progressVisibility = new ObservableField<>(View.GONE);
        errorVisibility = new ObservableField<>(View.GONE);
    }

    public void getCompanyData() {
        new ElectricityBillAPIRepository().getCompanies(this, new ResponseCallback<CompanyList>() {
            @Override
            public void onSuccess(CompanyList data) {
                mCompanyData.setValue(data);
            }

            @Override
            public void onError(ErrorModel error) {
                if (tryOut > 0)
                    getCompanyData();
                else {
                    errorM.setValue(error);
                }
                tryOut--;
            }
        });
    }

    private boolean checkData() {
        if (billSerial.get() == null || billSerial.get().isEmpty()) {
            billSerialError.set(R.string.elecBill_error_billSerial);
            billSerialErrorEnable.set(true);
            return false;
        }

        if (billSerial.get().length() < 7) {
            billSerialError.set(R.string.elecBill_error_billSerialLength);
            billSerialErrorEnable.set(true);
            return false;
        }

        if (companyPosition == -1 || companyPosition == 0) {
            errorM.setValue(new ErrorModel("error", "001"));
            return false;
        }
        return true;
    }

    public void getBranchData() {
        if (!checkData())
            return;

        progressVisibility.set(View.VISIBLE);
        new ElectricityBillAPIRepository().searchBills(billSerial.get(), String.valueOf(mCompanyData.getValue().getCompaniesList().get(companyPosition).getCode()), this, new ResponseCallback<ElectricityResponseList<BranchData>>() {
            @Override
            public void onSuccess(ElectricityResponseList<BranchData> data) {
                if (data.getStatus() == 200) {
                    if (data.getDataList().size() == 0) {
                        errorVisibility.set(View.VISIBLE);
                    }
                    mBranchData.setValue(data.getDataList());
                }
                progressVisibility.set(View.GONE);
            }

            @Override
            public void onError(ErrorModel error) {
                progressVisibility.set(View.GONE);
                errorM.setValue(error);
                errorVisibility.set(View.VISIBLE);
            }
        });
    }

    public MutableLiveData<CompanyList> getmCompanyData() {
        return mCompanyData;
    }

    public void setmCompanyData(MutableLiveData<CompanyList> mCompanyData) {
        this.mCompanyData = mCompanyData;
    }

    public MutableLiveData<List<BranchData>> getmBranchData() {
        return mBranchData;
    }

    public void setmBranchData(MutableLiveData<List<BranchData>> mBranchData) {
        this.mBranchData = mBranchData;
    }

    public ObservableField<String> getBillSerial() {
        return billSerial;
    }

    public void setBillSerial(ObservableField<String> billSerial) {
        this.billSerial = billSerial;
    }

    public ObservableField<Integer> getBillSerialError() {
        return billSerialError;
    }

    public void setBillSerialError(ObservableField<Integer> billSerialError) {
        this.billSerialError = billSerialError;
    }

    public ObservableField<Boolean> getBillSerialErrorEnable() {
        return billSerialErrorEnable;
    }

    public void setBillSerialErrorEnable(ObservableField<Boolean> billSerialErrorEnable) {
        this.billSerialErrorEnable = billSerialErrorEnable;
    }

    public ObservableField<Integer> getProgressVisibility() {
        return progressVisibility;
    }

    public void setProgressVisibility(ObservableField<Integer> progressVisibility) {
        this.progressVisibility = progressVisibility;
    }

    public int getCompanyPosition() {
        return companyPosition;
    }

    public void setCompanyPosition(int companyPosition) {
        this.companyPosition = companyPosition;
    }

    public MutableLiveData<ErrorModel> getErrorM() {
        return errorM;
    }

    public void setErrorM(MutableLiveData<ErrorModel> errorM) {
        this.errorM = errorM;
    }

    public ObservableField<Integer> getErrorVisibility() {
        return errorVisibility;
    }

    public void setErrorVisibility(ObservableField<Integer> errorVisibility) {
        this.errorVisibility = errorVisibility;
    }
}
