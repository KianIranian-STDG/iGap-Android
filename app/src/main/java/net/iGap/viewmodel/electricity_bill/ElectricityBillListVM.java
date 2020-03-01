package net.iGap.viewmodel.electricity_bill;

import android.view.View;

import androidx.databinding.ObservableInt;
import androidx.lifecycle.MutableLiveData;

import net.iGap.G;
import net.iGap.R;
import net.iGap.api.apiService.BaseAPIViewModel;
import net.iGap.observers.interfaces.ResponseCallback;
import net.iGap.api.errorhandler.ErrorModel;
import net.iGap.repository.ElectricityBillAPIRepository;
import net.iGap.model.electricity_bill.BillData;
import net.iGap.model.electricity_bill.BillRegister;
import net.iGap.model.electricity_bill.BranchDebit;
import net.iGap.model.electricity_bill.ElectricityResponseModel;
import net.iGap.module.SingleLiveEvent;
import net.iGap.request.RequestMplGetBillToken;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class ElectricityBillListVM extends BaseAPIViewModel {

    private MutableLiveData<Map<BillData.BillDataModel, BranchDebit>> mMapData;
    private SingleLiveEvent<Boolean> goBack;
    private MutableLiveData<ErrorModel> errorM;
    private MutableLiveData<Integer> showRequestFailedError;

    private ObservableInt progressVisibility;
    private ObservableInt errorVisibility;
    private ObservableInt showRetryView = new ObservableInt(View.GONE);
    private int nationalID = -1;

    public ElectricityBillListVM() {

        mMapData = new MutableLiveData<>(new HashMap<>());
        progressVisibility = new ObservableInt(View.GONE);
        errorVisibility = new ObservableInt(View.GONE);
        goBack = new SingleLiveEvent<>();
        errorM = new MutableLiveData<>();
        showRequestFailedError = new MutableLiveData<>();
    }

    public void getBranchData() {
        progressVisibility.set(View.VISIBLE);
        errorVisibility.set(View.GONE);
        showRetryView.set(View.GONE);
        new ElectricityBillAPIRepository().getBillList(this, new ResponseCallback<ElectricityResponseModel<BillData>>() {
            @Override
            public void onSuccess(ElectricityResponseModel<BillData> data) {
                if (data.getStatus() == 200) {
                    if (data.getData().getBillData().size() == 0) {
                        errorVisibility.set(View.VISIBLE);
                    }
                    nationalID = Integer.valueOf(data.getData().getNID());
                    Map<BillData.BillDataModel, BranchDebit> tmp = new HashMap<>();
                    for (BillData.BillDataModel dataModel : data.getData().getBillData()) {
                        tmp.put(dataModel, new BranchDebit());
                        getDebitData(dataModel);
                    }
                    mMapData.setValue(tmp);
                }
                progressVisibility.set(View.GONE);
            }

            @Override
            public void onError(String error) {
                showRetryView.set(View.VISIBLE);
                progressVisibility.set(View.GONE);
                errorM.setValue(new ErrorModel("", error));
                errorVisibility.set(View.VISIBLE);
            }

            @Override
            public void onFailed() {
                showRetryView.set(View.VISIBLE);
                progressVisibility.set(View.GONE);
                showRequestFailedError.setValue(R.string.connection_error);
            }
        });
    }

    private void getDebitData(@NotNull BillData.BillDataModel bill) {
        new ElectricityBillAPIRepository().getBranchDebit(bill.getBillID(), this, new ResponseCallback<ElectricityResponseModel<BranchDebit>>() {
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
            public void onError(String error) {
                Map<BillData.BillDataModel, BranchDebit> tmp = mMapData.getValue();
                BranchDebit debitTmp = tmp.get(bill);
                debitTmp.setLoading(false);
                debitTmp.setTotalBillDebt("0");
                debitTmp.setPaymentID("0");
                tmp.put(bill, debitTmp);
                mMapData.setValue(tmp);
            }

            @Override
            public void onFailed() {
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

    public void payBill(BillData.BillDataModel item) {

        BranchDebit tmp = mMapData.getValue().get(item);
        if (tmp == null || tmp.getPaymentID() == null || tmp.getPaymentID().equals("") || tmp.getPaymentID().equals("null")) {
            errorM.setValue(new ErrorModel("", "003"));
            return;
        }

        if (Long.parseLong(tmp.getTotalBillDebt().replace(",", "").replace(" ریال", "")) < 10000) {
            errorM.setValue(new ErrorModel("", "004"));
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
        if (tmp.getPaymentID().startsWith(tmp.getTotalBillDebt().replace("000", "").replace(",", "").replace(" ریال", ""))) {
            requestMplGetBillToken.mplGetBillToken(Long.parseLong(tmp.getBillID()), Long.parseLong(tmp.getPaymentID()));
        } else {
            requestMplGetBillToken.mplGetBillToken(Long.parseLong(tmp.getBillID()),
                    Long.parseLong(tmp.getTotalBillDebt().replace("000", "").replace(",", "").replace(" ریال", "") + tmp.getPaymentID()));
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
            public void onError(String error) {
                errorM.setValue(new ErrorModel("", error));
                progressVisibility.set(View.GONE);
            }

            @Override
            public void onFailed() {
                progressVisibility.set(View.GONE);
                showRequestFailedError.setValue(R.string.connection_error);
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
            public void onError(String error) {
                errorM.setValue(new ErrorModel("", error));
                progressVisibility.set(View.GONE);
            }

            @Override
            public void onFailed() {
                progressVisibility.set(View.GONE);
                showRequestFailedError.setValue(R.string.connection_error);
            }
        });
    }

    public ObservableInt getProgressVisibility() {
        return progressVisibility;
    }

    public void setProgressVisibility(ObservableInt progressVisibility) {
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

    public ObservableInt getErrorVisibility() {
        return errorVisibility;
    }

    public void setErrorVisibility(ObservableInt errorVisibility) {
        this.errorVisibility = errorVisibility;
    }

    public MutableLiveData<Integer> getShowRequestFailedError() {
        return showRequestFailedError;
    }

    public ObservableInt getShowRetryView() {
        return showRetryView;
    }
}
