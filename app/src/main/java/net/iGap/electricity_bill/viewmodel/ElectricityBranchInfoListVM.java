package net.iGap.electricity_bill.viewmodel;

import android.view.View;

import androidx.databinding.ObservableField;
import androidx.lifecycle.MutableLiveData;

import net.iGap.G;
import net.iGap.api.apiService.BaseAPIViewModel;
import net.iGap.api.apiService.ResponseCallback;
import net.iGap.api.errorhandler.ErrorModel;
import net.iGap.electricity_bill.repository.api.ElectricityBillAPIRepository;
import net.iGap.electricity_bill.repository.model.BillData;
import net.iGap.electricity_bill.repository.model.BillRegister;
import net.iGap.electricity_bill.repository.model.BranchData;
import net.iGap.electricity_bill.repository.model.BranchDebit;
import net.iGap.electricity_bill.repository.model.ElectricityResponseModel;
import net.iGap.request.RequestMplGetBillToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ElectricityBranchInfoListVM extends BaseAPIViewModel {

    private MutableLiveData<ElectricityResponseModel<BranchData>> mData;
    private ObservableField<Integer> progressVisibility;
    private String billID = null;

    public ElectricityBranchInfoListVM() {

        mData = new MutableLiveData<>();
        progressVisibility = new ObservableField<>(View.GONE);

    }

    public void getData() {
        progressVisibility.set(View.VISIBLE);
        new ElectricityBillAPIRepository().getBranchInfo(billID, this, new ResponseCallback<ElectricityResponseModel<BranchData>>() {
            @Override
            public void onSuccess(ElectricityResponseModel<BranchData> data) {
                if (data.getStatus() == 200)
                    mData.setValue(data);
                progressVisibility.set(View.GONE);
            }

            @Override
            public void onError(ErrorModel error) {

            }
        });
    }

    public ObservableField<Integer> getProgressVisibility() {
        return progressVisibility;
    }

    public void setProgressVisibility(ObservableField<Integer> progressVisibility) {
        this.progressVisibility = progressVisibility;
    }

    public MutableLiveData<ElectricityResponseModel<BranchData>> getmData() {
        return mData;
    }

    public void setmData(MutableLiveData<ElectricityResponseModel<BranchData>> mData) {
        this.mData = mData;
    }

    public String getBillID() {
        return billID;
    }

    public void setBillID(String billID) {
        this.billID = billID;
    }
}
