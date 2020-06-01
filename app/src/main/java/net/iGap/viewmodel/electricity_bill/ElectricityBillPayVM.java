package net.iGap.viewmodel.electricity_bill;

import android.view.View;

import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;
import androidx.databinding.ObservableInt;
import androidx.lifecycle.MutableLiveData;

import net.iGap.G;
import net.iGap.R;
import net.iGap.api.apiService.BaseAPIViewModel;
import net.iGap.api.errorhandler.ErrorModel;
import net.iGap.model.electricity_bill.Bill;
import net.iGap.model.electricity_bill.ElectricityResponseModel;
import net.iGap.model.electricity_bill.LastBillData;
import net.iGap.model.electricity_bill.ServiceDebit;
import net.iGap.observers.interfaces.ResponseCallback;
import net.iGap.repository.ElectricityBillAPIRepository;
import net.iGap.request.RequestMplGetBillToken;

public class ElectricityBillPayVM extends BaseAPIViewModel {

    private ObservableField<String> billID;
    private ObservableField<String> billPayID;
    private ObservableField<String> billPrice;
    private ObservableField<String> billTime;
    private ObservableInt progressVisibilityData;
    private ObservableInt progressVisibilityPay;
    private ObservableInt progressVisibilityDownload;
    private ObservableBoolean payBtnEnable;

    private MutableLiveData<LastBillData> billImage;
    private MutableLiveData<Integer> showRequestFailedError;
    private Bill debit;
    private MutableLiveData<ErrorModel> errorM;

    public ElectricityBillPayVM() {

        billID = new ObservableField<>("-");
        billPayID = new ObservableField<>("-");
        billPrice = new ObservableField<>("-");
        billTime = new ObservableField<>("-");
        billImage = new MutableLiveData<>();
        errorM = new MutableLiveData<>();
        debit = new Bill();

        progressVisibilityData = new ObservableInt(View.VISIBLE);
        progressVisibilityPay = new ObservableInt(View.GONE);
        progressVisibilityDownload = new ObservableInt(View.GONE);
        payBtnEnable = new ObservableBoolean(true);
        showRequestFailedError = new MutableLiveData<>();

    }

    public void getData() {
        progressVisibilityData.set(View.VISIBLE);
        new ElectricityBillAPIRepository().getBranchDebit(debit.getID(), this, new ResponseCallback<ElectricityResponseModel<ServiceDebit>>() {
            @Override
            public void onSuccess(ElectricityResponseModel<ServiceDebit> data) {
                progressVisibilityData.set(View.GONE);
                if (data.getStatus() == 200) {
                    debit = new Bill(debit.getID(), data.getData().getPaymentID(), data.getData().getTotalElectricityBillDebt(), data.getData().getPaymentDeadLineDate());
                    billPayID.set(data.getData().getPaymentIDConverted());
                    billPrice.set(data.getData().getTotalBillDebtConverted() + " ریال");
                    billTime.set(data.getData().getPaymentDeadLineDate());
                }
            }

            @Override
            public void onError(String error) {
                errorM.setValue(new ErrorModel("", error));
                progressVisibilityData.set(View.GONE);
            }

            @Override
            public void onFailed() {
                progressVisibilityData.set(View.GONE);
                showRequestFailedError.setValue(R.string.connection_error);
            }
        });
    }

    public void payBill() {

        if (debit.getPayID() == null || debit.getPayID().equals("") || debit.getPayID().equals("null")) {
            errorM.setValue(new ErrorModel("", "001"));
            getData();
            return;
        }

        if (Long.parseLong(debit.getPrice().replace(",", "").replace(" ریال", "")) < 10000) {
            errorM.setValue(new ErrorModel("", "002"));
            return;
        }

        progressVisibilityPay.set(View.VISIBLE);
        payBtnEnable.set(false);

        G.onMplResult = error -> {
            progressVisibilityPay.set(View.GONE);
            payBtnEnable.set(true);
            if (error) {
                errorM.setValue(new ErrorModel("", "003"));
            }
        };

        RequestMplGetBillToken requestMplGetBillToken = new RequestMplGetBillToken();
        if (debit.getPayID().startsWith(debit.getPrice().replace("000", "").replace(",", "").replace(" ریال", ""))) {
            requestMplGetBillToken.mplGetBillToken(Long.parseLong(debit.getID()), Long.parseLong(debit.getPayID()));
        } else {
            requestMplGetBillToken.mplGetBillToken(Long.parseLong(debit.getID()), Long.parseLong(debit.getPrice().replace("000", "").replace(",", "").replace(" ریال", "") + debit.getPayID()));
        }
    }

    public void showBillImage() {
        progressVisibilityDownload.set(View.VISIBLE);
        new ElectricityBillAPIRepository().getLastBill(debit.getID(), this, new ResponseCallback<ElectricityResponseModel<LastBillData>>() {
            @Override
            public void onSuccess(ElectricityResponseModel<LastBillData> data) {
                if (data.getStatus() == 200)
                    billImage.setValue(data.getData());
                progressVisibilityDownload.set(View.GONE);
            }

            @Override
            public void onError(String error) {
                progressVisibilityDownload.set(View.GONE);
                errorM.setValue(new ErrorModel("", error));
            }

            @Override
            public void onFailed() {
                progressVisibilityData.set(View.GONE);
                showRequestFailedError.setValue(R.string.connection_error);
            }
        });
    }

    public ObservableField<String> getBillID() {
        return billID;
    }

    public void setBillID(ObservableField<String> billID) {
        this.billID = billID;
    }

    public ObservableField<String> getBillPayID() {
        return billPayID;
    }

    public void setBillPayID(ObservableField<String> billPayID) {
        this.billPayID = billPayID;
    }

    public ObservableField<String> getBillPrice() {
        return billPrice;
    }

    public void setBillPrice(ObservableField<String> billPrice) {
        this.billPrice = billPrice;
    }

    public ObservableField<String> getBillTime() {
        return billTime;
    }

    public void setBillTime(ObservableField<String> billTime) {
        this.billTime = billTime;
    }

    public ObservableInt getProgressVisibilityData() {
        return progressVisibilityData;
    }

    public void setProgressVisibilityData(ObservableInt progressVisibilityData) {
        this.progressVisibilityData = progressVisibilityData;
    }

    public ObservableInt getProgressVisibilityPay() {
        return progressVisibilityPay;
    }

    public void setProgressVisibilityPay(ObservableInt progressVisibilityPay) {
        this.progressVisibilityPay = progressVisibilityPay;
    }

    public ObservableBoolean getPayBtnEnable() {
        return payBtnEnable;
    }

    public void setPayBtnEnable(ObservableBoolean payBtnEnable) {
        this.payBtnEnable = payBtnEnable;
    }

    public MutableLiveData<LastBillData> getBillImage() {
        return billImage;
    }

    public void setBillImage(MutableLiveData<LastBillData> billImage) {
        this.billImage = billImage;
    }

    public ObservableInt getProgressVisibilityDownload() {
        return progressVisibilityDownload;
    }

    public void setProgressVisibilityDownload(ObservableInt progressVisibilityDownload) {
        this.progressVisibilityDownload = progressVisibilityDownload;
    }

    public MutableLiveData<ErrorModel> getErrorM() {
        return errorM;
    }

    public void setErrorM(MutableLiveData<ErrorModel> errorM) {
        this.errorM = errorM;
    }

    public Bill getDebit() {
        return debit;
    }

    public void setDebit(Bill debit) {
        this.debit = debit;
    }

    public MutableLiveData<Integer> getShowRequestFailedError() {
        return showRequestFailedError;
    }
}
