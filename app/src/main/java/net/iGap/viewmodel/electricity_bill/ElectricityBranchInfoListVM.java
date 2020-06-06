package net.iGap.viewmodel.electricity_bill;

import android.view.View;

import androidx.databinding.ObservableField;
import androidx.lifecycle.MutableLiveData;

import net.iGap.R;
import net.iGap.api.apiService.BaseAPIViewModel;
import net.iGap.model.bill.BillInfo;
import net.iGap.model.bill.GasBranchData;
import net.iGap.model.electricity_bill.ElectricityBranchData;
import net.iGap.model.electricity_bill.ElectricityResponseModel;
import net.iGap.observers.interfaces.ResponseCallback;
import net.iGap.repository.BillsAPIRepository;

public class ElectricityBranchInfoListVM extends BaseAPIViewModel {

    private MutableLiveData<ElectricityResponseModel<ElectricityBranchData>> mDataElec;
    private MutableLiveData<ElectricityResponseModel<GasBranchData>> mDataGas;
    private MutableLiveData<Integer> showRequestFailedError;
    private ObservableField<Integer> progressVisibility;
    private ObservableField<Integer> errorVisibility;
    private String billID = null;

    public ElectricityBranchInfoListVM() {

        mDataElec = new MutableLiveData<>();
        mDataGas = new MutableLiveData<>();
        showRequestFailedError = new MutableLiveData<>();
        progressVisibility = new ObservableField<>(View.GONE);
        errorVisibility = new ObservableField<>(View.GONE);

    }

    public void getDataGas() {
        progressVisibility.set(View.VISIBLE);
        BillInfo temp = new BillInfo();
        temp.setBillType(BillInfo.BillType.GAS);
        temp.setGasID(billID);
        new BillsAPIRepository().getGasBranchInfo(temp, this, new ResponseCallback<ElectricityResponseModel<GasBranchData>>() {
            @Override
            public void onSuccess(ElectricityResponseModel<GasBranchData> data) {
                mDataGas.setValue(data);
                progressVisibility.set(View.GONE);
            }

            @Override
            public void onError(String error) {
                progressVisibility.set(View.GONE);
                errorVisibility.set(View.VISIBLE);
            }

            @Override
            public void onFailed() {
                progressVisibility.set(View.GONE);
                showRequestFailedError.setValue(R.string.connection_error);
            }
        });
    }

    public void getDataElec() {
        progressVisibility.set(View.VISIBLE);
        BillInfo temp = new BillInfo();
        temp.setBillType(BillInfo.BillType.ELECTRICITY);
        temp.setBillID(billID);
        new BillsAPIRepository().getElectricityBranchInfo(temp, this, new ResponseCallback<ElectricityResponseModel<ElectricityBranchData>>() {
            @Override
            public void onSuccess(ElectricityResponseModel<ElectricityBranchData> data) {
                mDataElec.setValue(data);
                progressVisibility.set(View.GONE);
            }

            @Override
            public void onError(String error) {
                progressVisibility.set(View.GONE);
                errorVisibility.set(View.VISIBLE);
            }

            @Override
            public void onFailed() {
                progressVisibility.set(View.GONE);
                showRequestFailedError.setValue(R.string.connection_error);
            }
        });
    }

    public ObservableField<Integer> getProgressVisibility() {
        return progressVisibility;
    }

    public void setProgressVisibility(ObservableField<Integer> progressVisibility) {
        this.progressVisibility = progressVisibility;
    }

    public MutableLiveData<ElectricityResponseModel<ElectricityBranchData>> getmDataElec() {
        return mDataElec;
    }

    public void setmDataElec(MutableLiveData<ElectricityResponseModel<ElectricityBranchData>> mDataElec) {
        this.mDataElec = mDataElec;
    }

    public String getBillID() {
        return billID;
    }

    public void setBillID(String billID) {
        this.billID = billID;
    }

    public ObservableField<Integer> getErrorVisibility() {
        return errorVisibility;
    }

    public void setErrorVisibility(ObservableField<Integer> errorVisibility) {
        this.errorVisibility = errorVisibility;
    }

    public MutableLiveData<Integer> getShowRequestFailedError() {
        return showRequestFailedError;
    }

    public MutableLiveData<ElectricityResponseModel<GasBranchData>> getmDataGas() {
        return mDataGas;
    }

    public void setmDataGas(MutableLiveData<ElectricityResponseModel<GasBranchData>> mDataGas) {
        this.mDataGas = mDataGas;
    }
}
