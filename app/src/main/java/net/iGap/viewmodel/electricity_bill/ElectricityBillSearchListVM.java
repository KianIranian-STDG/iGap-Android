package net.iGap.viewmodel.electricity_bill;

import android.view.View;

import androidx.databinding.ObservableField;
import androidx.databinding.ObservableInt;
import androidx.lifecycle.MutableLiveData;

import net.iGap.R;
import net.iGap.api.apiService.BaseAPIViewModel;
import net.iGap.api.errorhandler.ErrorModel;
import net.iGap.model.electricity_bill.CompanyList;
import net.iGap.model.electricity_bill.ElectricityBranchData;
import net.iGap.model.electricity_bill.ElectricityResponseList;
import net.iGap.observers.interfaces.ResponseCallback;
import net.iGap.repository.ElectricityBillAPIRepository;

import java.util.List;

public class ElectricityBillSearchListVM extends BaseAPIViewModel {

    private MutableLiveData<CompanyList> mCompanyData;
    private MutableLiveData<List<ElectricityBranchData>> mBranchData;
    private MutableLiveData<ErrorModel> errorM;
    private MutableLiveData<Integer> showRequestFailedError;

    private ObservableField<String> billSerial;
    private ObservableInt billSerialError;
    private ObservableField<Boolean> billSerialErrorEnable;
    private ObservableInt progressVisibility;
    private ObservableInt errorVisibility;

    private int companyPosition = -1;
    private int tryOut = 3;

    public ElectricityBillSearchListVM() {
        mCompanyData = new MutableLiveData<>();
        mBranchData = new MutableLiveData<>();
        errorM = new MutableLiveData<>();
        showRequestFailedError = new MutableLiveData<>();

        billSerial = new ObservableField<>();
        billSerialError = new ObservableInt();
        billSerialErrorEnable = new ObservableField<>();
        progressVisibility = new ObservableInt(View.GONE);
        errorVisibility = new ObservableInt(View.GONE);
    }

    public void getCompanyData() {
        new ElectricityBillAPIRepository().getCompanies(this, new ResponseCallback<CompanyList>() {
            @Override
            public void onSuccess(CompanyList data) {
                mCompanyData.setValue(data);
            }

            @Override
            public void onError(String error) {
                if (tryOut > 0)
                    getCompanyData();
                else {
                    errorM.setValue(new ErrorModel("", error));
                }
                tryOut--;
            }

            @Override
            public void onFailed() {
                if (tryOut > 0)
                    getCompanyData();
                else {
                    showRequestFailedError.setValue(R.string.connection_error);
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

        if (companyPosition == -1) {
            errorM.setValue(new ErrorModel("error", "001"));
            return false;
        }
        return true;
    }

    public void getBranchData() {
        progressVisibility.set(View.VISIBLE);

        if (!checkData()) {
            progressVisibility.set(View.GONE);
            return;
        }
        errorVisibility.set(View.GONE);
        new ElectricityBillAPIRepository().searchBills(billSerial.get(), String.valueOf(mCompanyData.getValue().getCompaniesList().get(companyPosition).getCode()), this, new ResponseCallback<ElectricityResponseList<ElectricityBranchData>>() {
            @Override
            public void onSuccess(ElectricityResponseList<ElectricityBranchData> data) {
                if (data.getStatus() == 200) {
                    if (data.getDataList().size() == 0) {
                        errorVisibility.set(View.VISIBLE);
                    }
                    mBranchData.setValue(data.getDataList());
                }
                progressVisibility.set(View.GONE);
            }

            @Override
            public void onError(String error) {
                progressVisibility.set(View.GONE);
                errorM.setValue(new ErrorModel("", error));
                errorVisibility.set(View.VISIBLE);
            }

            @Override
            public void onFailed() {
                progressVisibility.set(View.GONE);
                showRequestFailedError.setValue(R.string.connection_error);
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

    public MutableLiveData<List<ElectricityBranchData>> getmBranchData() {
        return mBranchData;
    }

    public void setmBranchData(MutableLiveData<List<ElectricityBranchData>> mBranchData) {
        this.mBranchData = mBranchData;
    }

    public ObservableField<String> getBillSerial() {
        return billSerial;
    }

    public void setBillSerial(ObservableField<String> billSerial) {
        this.billSerial = billSerial;
    }

    public ObservableInt getBillSerialError() {
        return billSerialError;
    }

    public void setBillSerialError(ObservableInt billSerialError) {
        this.billSerialError = billSerialError;
    }

    public ObservableField<Boolean> getBillSerialErrorEnable() {
        return billSerialErrorEnable;
    }

    public void setBillSerialErrorEnable(ObservableField<Boolean> billSerialErrorEnable) {
        this.billSerialErrorEnable = billSerialErrorEnable;
    }

    public ObservableInt getProgressVisibility() {
        return progressVisibility;
    }

    public void setProgressVisibility(ObservableInt progressVisibility) {
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

    public ObservableInt getErrorVisibility() {
        return errorVisibility;
    }

    public void setErrorVisibility(ObservableInt errorVisibility) {
        this.errorVisibility = errorVisibility;
    }

    public MutableLiveData<Integer> getShowRequestFailedError() {
        return showRequestFailedError;
    }
}
