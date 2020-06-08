package net.iGap.viewmodel.electricity_bill;

import android.view.View;

import androidx.databinding.ObservableInt;
import androidx.lifecycle.MutableLiveData;

import net.iGap.G;
import net.iGap.R;
import net.iGap.api.apiService.BaseAPIViewModel;
import net.iGap.api.errorhandler.ErrorModel;
import net.iGap.model.bill.BillInfo;
import net.iGap.model.bill.BillList;
import net.iGap.model.bill.Debit;
import net.iGap.model.bill.MobileDebit;
import net.iGap.model.bill.ServiceDebit;
import net.iGap.model.electricity_bill.ElectricityResponseModel;
import net.iGap.module.SingleLiveEvent;
import net.iGap.observers.interfaces.ResponseCallback;
import net.iGap.repository.BillsAPIRepository;
import net.iGap.repository.ElectricityBillAPIRepository;
import net.iGap.request.RequestMplGetBillToken;

import java.util.HashMap;
import java.util.Map;

public class ElectricityBillListVM extends BaseAPIViewModel {

    private MutableLiveData<Map<BillList.Bill, Debit>> mMapData;
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

    public BillInfo getBillInfo(BillList.Bill dataModel) {
        BillInfo temp = new BillInfo();
        temp.setServerID(dataModel.getId());
        switch (dataModel.getBillType()) {
            case "ELECTRICITY":
                temp.setTitle(dataModel.getBillTitle());
                temp.setBillID(dataModel.getBillID());
                temp.setBillType(BillInfo.BillType.ELECTRICITY);
                break;
            case "GAS":
                temp.setTitle(dataModel.getBillTitle());
                temp.setGasID(dataModel.getSubscriptionCode());
                temp.setBillID(dataModel.getSubscriptionCode());
                temp.setBillType(BillInfo.BillType.GAS);
                break;
            case "MOBILE_MCI":
                temp.setTitle(dataModel.getBillTitle());
                temp.setPhoneNum(dataModel.getPhoneNumber());
                temp.setBillID(dataModel.getPhoneNumber());
                temp.setBillType(BillInfo.BillType.MOBILE);
                break;
            case "PHONE":
                temp.setTitle(dataModel.getBillTitle());
                temp.setPhoneNum(dataModel.getPhoneNumber());
                temp.setAreaCode(dataModel.getAreaCode());
                temp.setBillID(dataModel.getAreaCode() + dataModel.getPhoneNumber());
                temp.setBillType(BillInfo.BillType.PHONE);
                break;
        }
        return temp;
    }

    public void reloadData(BillList.Bill data) {
        Map<BillList.Bill, Debit> tmp = mMapData.getValue();
        switch (data.getBillType()) {
            case "ELECTRICITY":
            case "GAS":
                ServiceDebit temp = (ServiceDebit) tmp.get(data).getData();
                temp.setLoading(true);
                temp.setFail(false);
                tmp.put(data, new Debit(temp));
                getServiceDebit(data);
                break;
            case "MOBILE_MCI":
            case "PHONE":
                MobileDebit temp1 = (MobileDebit) tmp.get(data).getData();
                temp1.setLoading(true);
                temp1.setFail(false);
                tmp.put(data, new Debit(temp1));
                getPhoneDebit(data);
                break;
        }
    }

    public void getBillsList() {
        progressVisibility.set(View.VISIBLE);
        errorVisibility.set(View.GONE);
        showRetryView.set(View.GONE);
        new BillsAPIRepository().getBillList(this, new ResponseCallback<BillList>() {
            @Override
            public void onSuccess(BillList data) {
                if (data.getBills() == null || data.getBills().size() == 0) {
                    errorVisibility.set(View.VISIBLE);
                }
                Map<BillList.Bill, Debit> tmp = new HashMap<>();
                for (BillList.Bill dataModel : data.getBills()) {
                    switch (dataModel.getBillType()) {
                        case "ELECTRICITY":
                        case "GAS":
                            Debit<ServiceDebit> temp = new Debit<>();
                            temp.setData(new ServiceDebit());
                            tmp.put(dataModel, temp);
                            getServiceDebit(dataModel);
                            break;
                        case "MOBILE_MCI":
                        case "PHONE":
                            Debit<MobileDebit> temp1 = new Debit<>();
                            temp1.setData(new MobileDebit());
                            tmp.put(dataModel, temp1);
                            getPhoneDebit(dataModel);
                            break;
                    }
                }
                mMapData.setValue(tmp);
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

    private void getServiceDebit(BillList.Bill dataModel) {
        BillInfo info = new BillInfo();
        info.setBillTypeString(dataModel.getBillType());
        if (dataModel.getBillType().equals("ELECTRICITY")) {
            info.setBillID(dataModel.getBillID());
            info.setMobileNum(dataModel.getMobileNumber());
        } else {
            info.setGasID(dataModel.getSubscriptionCode());
        }

        new BillsAPIRepository().serviceInquiry(info, this, new ResponseCallback<ElectricityResponseModel<ServiceDebit>>() {
            @Override
            public void onSuccess(ElectricityResponseModel<ServiceDebit> data) {
                Map<BillList.Bill, Debit> tmp = mMapData.getValue();
                data.getData().setLoading(false);
                tmp.put(dataModel, new Debit<>(data.getData()));
                mMapData.setValue(tmp);
            }

            @Override
            public void onError(String error) {
                Map<BillList.Bill, Debit> tmp = mMapData.getValue();
                Debit<ServiceDebit> debitTmp = tmp.get(dataModel);
                debitTmp.getData().setLoading(false);
                debitTmp.getData().setFail(true);
                debitTmp.getData().setTotalElectricityBillDebt("0");
                debitTmp.getData().setTotalGasBillDebt("0");
                debitTmp.getData().setPaymentID("0");
                tmp.put(dataModel, debitTmp);
                mMapData.setValue(tmp);
            }

            @Override
            public void onFailed() {
                Map<BillList.Bill, Debit> tmp = mMapData.getValue();
                Debit<ServiceDebit> debitTmp = tmp.get(dataModel);
                debitTmp.getData().setLoading(false);
                debitTmp.getData().setFail(true);
                debitTmp.getData().setTotalElectricityBillDebt("0");
                debitTmp.getData().setTotalGasBillDebt("0");
                debitTmp.getData().setPaymentID("0");
                tmp.put(dataModel, debitTmp);
                mMapData.setValue(tmp);
            }
        });
    }

    private void getPhoneDebit(BillList.Bill dataModel) {
        BillInfo info = new BillInfo();
        info.setBillTypeString(dataModel.getBillType());
        info.setPhoneNum(dataModel.getPhoneNumber());
        if (dataModel.getBillType().equals("PHONE")) {
            info.setAreaCode(dataModel.getAreaCode());
        }

        new BillsAPIRepository().phoneAndMobileInquiry(info, this, new ResponseCallback<ElectricityResponseModel<MobileDebit>>() {
            @Override
            public void onSuccess(ElectricityResponseModel<MobileDebit> data) {
                Map<BillList.Bill, Debit> tmp = mMapData.getValue();
                data.getData().setLoading(false);
                tmp.put(dataModel, new Debit<>(data.getData()));
                mMapData.setValue(tmp);
            }

            @Override
            public void onError(String error) {
                Map<BillList.Bill, Debit> tmp = mMapData.getValue();
                Debit<MobileDebit> debitTmp = tmp.get(dataModel);
                debitTmp.getData().setLoading(false);
                debitTmp.getData().setFail(true);
                debitTmp.getData().setLastTerm(new MobileDebit.MobileInquiryObject("0", "0", "0"));
                debitTmp.getData().setMidTerm(new MobileDebit.MobileInquiryObject("0", "0", "0"));
                tmp.put(dataModel, debitTmp);
                mMapData.setValue(tmp);
            }

            @Override
            public void onFailed() {
                Map<BillList.Bill, Debit> tmp = mMapData.getValue();
                Debit<MobileDebit> debitTmp = tmp.get(dataModel);
                debitTmp.getData().setLoading(false);
                debitTmp.getData().setFail(true);
                debitTmp.getData().setLastTerm(new MobileDebit.MobileInquiryObject("0", "0", "0"));
                debitTmp.getData().setMidTerm(new MobileDebit.MobileInquiryObject("0", "0", "0"));
                tmp.put(dataModel, debitTmp);
                mMapData.setValue(tmp);
            }
        });
    }

    /*private void getDebitData(@NotNull BillData.BillDataModel bill) {
        new ElectricityBillAPIRepository().getBranchDebit(bill.getBillID(), this, new ResponseCallback<ElectricityResponseModel<ServiceDebit>>() {
            @Override
            public void onSuccess(ElectricityResponseModel<ServiceDebit> data) {
                if (data.getStatus() == 200) {
                    Map<BillData.BillDataModel, ServiceDebit> tmp = mMapData.getValue();
                    data.getData().setLoading(false);
                    tmp.put(bill, data.getData());
                    mMapData.setValue(tmp);
                }
            }

            @Override
            public void onError(String error) {
                Map<BillData.BillDataModel, ServiceDebit> tmp = mMapData.getValue();
                ServiceDebit debitTmp = tmp.get(bill);
                debitTmp.setLoading(false);
                debitTmp.setTotalElectricityBillDebt("0");
                debitTmp.setPaymentID("0");
                tmp.put(bill, debitTmp);
                mMapData.setValue(tmp);
            }

            @Override
            public void onFailed() {
                Map<BillData.BillDataModel, ServiceDebit> tmp = mMapData.getValue();
                ServiceDebit debitTmp = tmp.get(bill);
                debitTmp.setLoading(false);
                debitTmp.setTotalElectricityBillDebt("0");
                debitTmp.setPaymentID("0");
                tmp.put(bill, debitTmp);
                mMapData.setValue(tmp);
            }
        });
    }*/

    public void payServiceBill(String billID, String payID, String price) {
        if (payID == null || payID.equals("") || payID.equals("null")) {
            errorM.setValue(new ErrorModel("", "003"));
            return;
        }

        if (Long.parseLong(price) < 10000) {
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
        requestMplGetBillToken.mplGetBillToken(Long.parseLong(billID), Long.parseLong(payID));
    }

    public void deleteItem(BillList.Bill item) {
        progressVisibility.set(View.VISIBLE);
        BillList.Bill dataModel = item;
        BillInfo info = new BillInfo();
        info.setBillTypeString(dataModel.getBillType());
        info.setServerID(dataModel.getId());

        new BillsAPIRepository().deleteBill(info, this, new ResponseCallback<ElectricityResponseModel<String>>() {
            @Override
            public void onSuccess(ElectricityResponseModel<String> data) {
                Map<BillList.Bill, Debit> tmp = mMapData.getValue();
                tmp.remove(dataModel);
                mMapData.setValue(tmp);
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

    @Deprecated
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

    public MutableLiveData<Map<BillList.Bill, Debit>> getmMapData() {
        return mMapData;
    }

    public void setmMapData(MutableLiveData<Map<BillList.Bill, Debit>> mMapData) {
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
