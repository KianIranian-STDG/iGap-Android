package net.iGap.electricity_bill.viewmodel;

import android.util.Log;
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
import net.iGap.electricity_bill.repository.model.BranchDebit;
import net.iGap.electricity_bill.repository.model.ElectricityResponseModel;
import net.iGap.module.SingleLiveEvent;
import net.iGap.request.RequestMplGetBillToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ElectricityBillListVM extends BaseAPIViewModel {

    private MutableLiveData<Map<BillData.BillDataModel, BranchDebit>> mMapData;
    private SingleLiveEvent<Boolean> goBack;
    private MutableLiveData<ErrorModel> errorM;

    private ObservableField<Integer> progressVisibility;
    private ObservableField<Integer> errorVisibility;
    private int nationalID = -1;

    public ElectricityBillListVM() {

        mMapData = new MutableLiveData<>(new HashMap<>());
        progressVisibility = new ObservableField<>(View.GONE);
        errorVisibility = new ObservableField<>(View.GONE);
        goBack = new SingleLiveEvent<>();
        errorM = new MutableLiveData<>();

    }

    public void getBranchData() {
        progressVisibility.set(View.VISIBLE);
        errorVisibility.set(View.GONE);
        new ElectricityBillAPIRepository().getBillList(this, new ResponseCallback<ElectricityResponseModel<BillData>>() {
            @Override
            public void onSuccess(ElectricityResponseModel<BillData> data) {
                if (data.getStatus() == 200) {
                    if (data.getData().getBillData().size() == 0) {
                        errorVisibility.set(View.VISIBLE);
                    }
                    nationalID = Integer.valueOf(data.getData().getNID());
                    Map<BillData.BillDataModel, BranchDebit> tmp = new HashMap<>();
                    for (BillData.BillDataModel dataModel:data.getData().getBillData()) {
                        tmp.put(dataModel, new BranchDebit());
                        getDebitData(dataModel);
                    }
                    mMapData.setValue(tmp);
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

    private void getDebitData(BillData.BillDataModel bill) {
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
                        Map<BillData.BillDataModel, BranchDebit> tmp = mMapData.getValue();
                        BranchDebit debitTmp = tmp.get(bill);
                        debitTmp.setLoading(false);
                        debitTmp.setTotalBillDebt("0");
                        debitTmp.setPaymentID("0");
                        tmp.put(bill, debitTmp);
                        mMapData.setValue(tmp);
                    }
                });
    }

    public void payBill (BillData.BillDataModel item){

        BranchDebit tmp = mMapData.getValue().get(item);
        if (tmp == null || tmp.getPaymentID() == null || tmp.getPaymentID().equals("") || tmp.getPaymentID().equals("null")) {
            errorM.setValue(new ErrorModel("" , "003"));
            return;
        }

        if (Long.parseLong(tmp.getTotalBillDebt().replace(",","").replace(" ریال", "")) < 10000) {
            errorM.setValue(new ErrorModel("" , "004"));
            return;
        }

        progressVisibility.set(View.VISIBLE);

        G.onMplResult = error -> {
            progressVisibility.set(View.GONE);
            if (error) {
                errorM.setValue(new ErrorModel("", "001"));
            }
        };
        RequestMplGetBillToken requestMplGetBillToken = new RequestMplGetBillToken();
        if (tmp.getPaymentID().startsWith(tmp.getTotalBillDebt().replace("000", "").replace(",","").replace(" ریال", ""))) {
            requestMplGetBillToken.mplGetBillToken(Long.parseLong(tmp.getBillID()), Long.parseLong(tmp.getPaymentID()));
        }
        else {
            requestMplGetBillToken.mplGetBillToken(Long.parseLong(tmp.getBillID()),
                    Long.parseLong(tmp.getTotalBillDebt().replace("000","").replace(",","").replace(" ریال", "") + tmp.getPaymentID()));
        }
    }

    public void deleteItem(BillData.BillDataModel item) {
        progressVisibility.set(View.VISIBLE);
        BillData.BillDataModel dataModel = item;
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
                progressVisibility.set(View.GONE);
            }

            @Override
            public void onError(ErrorModel error) {
                errorM.setValue(error);
                progressVisibility.set(View.GONE);
            }
        });
    }

    public void deleteAccount() {
        progressVisibility.set(View.VISIBLE);
        new ElectricityBillAPIRepository().deleteAccount(String.valueOf(nationalID), this, new ResponseCallback<ElectricityResponseModel<String>>() {
            @Override
            public void onSuccess(ElectricityResponseModel<String> data) {
                if (data.getStatus() == 200) {
                    mMapData.setValue(null);
                    goBack.setValue(true);
                }
                progressVisibility.set(View.GONE);
            }

            @Override
            public void onError(ErrorModel error) {
                errorM.setValue(error);
                progressVisibility.set(View.GONE);
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

    public SingleLiveEvent<Boolean> getGoBack() {
        return goBack;
    }

    public void setGoBack(SingleLiveEvent<Boolean> goBack) {
        this.goBack = goBack;
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
