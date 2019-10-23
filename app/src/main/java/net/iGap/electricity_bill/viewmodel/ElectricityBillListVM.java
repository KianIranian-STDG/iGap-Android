package net.iGap.electricity_bill.viewmodel;

import android.view.View;

import androidx.databinding.ObservableField;
import androidx.lifecycle.MutableLiveData;

import net.iGap.G;
import net.iGap.R;
import net.iGap.api.apiService.BaseAPIViewModel;
import net.iGap.api.apiService.ResponseCallback;
import net.iGap.api.errorhandler.ErrorModel;
import net.iGap.electricity_bill.repository.api.ElectricityBillAPIRepository;
import net.iGap.electricity_bill.repository.model.Bill;
import net.iGap.electricity_bill.repository.model.BillData;
import net.iGap.electricity_bill.repository.model.BillRegister;
import net.iGap.electricity_bill.repository.model.BranchDebit;
import net.iGap.electricity_bill.repository.model.ElectricityResponseModel;
import net.iGap.helper.HelperNumerical;
import net.iGap.request.RequestMplGetBillToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ElectricityBillListVM extends BaseAPIViewModel {

    private MutableLiveData<Map<BillData.BillDataModel, BranchDebit>> mMapData;
    private MutableLiveData<Boolean> goBack;
    private ObservableField<Integer> progressVisibility;
    private int nationalID = -1;

    public ElectricityBillListVM() {

        mMapData = new MutableLiveData<>(new HashMap<>());
        progressVisibility = new ObservableField<>(View.GONE);
        goBack = new MutableLiveData<>(false);

    }

    public void getBranchData() {
        new ElectricityBillAPIRepository().getBillList(this, new ResponseCallback<ElectricityResponseModel<BillData>>() {
            @Override
            public void onSuccess(ElectricityResponseModel<BillData> data) {
                if (data.getStatus() == 200) {
                    nationalID = data.getData().getNID();
                    Map<BillData.BillDataModel, BranchDebit> tmp = new HashMap<>();
                    for (BillData.BillDataModel dataModel:data.getData().getBillData()) {
                        tmp.put(dataModel, new BranchDebit());
                        getDebitData(dataModel);
                    }
                    mMapData.setValue(tmp);
                }
            }

            @Override
            public void onError(ErrorModel error) {

            }
        });
    }

    public void getDebitData(BillData.BillDataModel bill) {
        new ElectricityBillAPIRepository().getBranchDebit(bill.getBillID(), this,
                new ResponseCallback<ElectricityResponseModel<BranchDebit>>() {
                    @Override
                    public void onSuccess(ElectricityResponseModel<BranchDebit> data) {
                        if (data.getStatus() == 200) {
                            Map<BillData.BillDataModel, BranchDebit> tmp = mMapData.getValue();
                            data.getData().setLoading(false);
                            tmp.put(bill, data.getData());
                            mMapData.setValue(tmp);
                        }
                    }

                    @Override
                    public void onError(ErrorModel error) {

                    }
                });
    }

    public void payBill (int position){
        BranchDebit tmp = mMapData.getValue().get(new ArrayList<>(mMapData.getValue().keySet()).get(position));
        if (Long.parseLong(tmp.getTotalBillDebt()) < 1000) {
            return;
        }

//        progressVisibilityPay.set(View.VISIBLE);
//        payBtnEnable.set(false);

        G.onMplResult = error -> {
//            progressVisibilityPay.set(View.GONE);
            if (error) {
//                progressVisibilityPay.set(View.GONE);
//                payBtnEnable.set(true);
            } else {
                // error
            }
        };

        RequestMplGetBillToken requestMplGetBillToken = new RequestMplGetBillToken();
        requestMplGetBillToken.mplGetBillToken(Long.parseLong(tmp.getBillID()),
                Long.parseLong(tmp.getTotalBillDebt().replace("0","")) + Long.parseLong(tmp.getPaymentID()));
    }

    public void deleteItem(int position) {
        BillData.BillDataModel dataModel = new ArrayList<>(mMapData.getValue().keySet()).get(position);
        BillRegister info = new BillRegister();
        info.setNID("" + nationalID);
        info.setID(dataModel.getBillID());

        new ElectricityBillAPIRepository().deleteBill(info, this, new ResponseCallback<ElectricityResponseModel<String>>() {
            @Override
            public void onSuccess(ElectricityResponseModel<String> data) {
                if (data.getStatus() == 200) {
                    Map<BillData.BillDataModel, BranchDebit> tmp = mMapData.getValue();
                    tmp.remove(dataModel);
                    mMapData.setValue(tmp);
                }
            }

            @Override
            public void onError(ErrorModel error) {

            }
        });
    }

    public void deleteAccount() {
        new ElectricityBillAPIRepository().deleteAccount(String.valueOf(nationalID), this, new ResponseCallback<ElectricityResponseModel<String>>() {
            @Override
            public void onSuccess(ElectricityResponseModel<String> data) {
                if (data.getStatus() == 200) {
                    mMapData.setValue(null);
                    goBack.setValue(true);
                }
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

    public MutableLiveData<Map<BillData.BillDataModel, BranchDebit>> getmMapData() {
        return mMapData;
    }

    public void setmMapData(MutableLiveData<Map<BillData.BillDataModel, BranchDebit>> mMapData) {
        this.mMapData = mMapData;
    }

    public int getNationalID() {
        return nationalID;
    }

    public void setNationalID(int nationalID) {
        this.nationalID = nationalID;
    }

    public MutableLiveData<Boolean> getGoBack() {
        return goBack;
    }

    public void setGoBack(MutableLiveData<Boolean> goBack) {
        this.goBack = goBack;
    }
}
