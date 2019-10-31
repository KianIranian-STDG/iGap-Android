package net.iGap.electricity_bill.viewmodel;

import android.view.View;

import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;
import androidx.lifecycle.MutableLiveData;

import net.iGap.G;
import net.iGap.api.apiService.BaseAPIViewModel;
import net.iGap.api.apiService.ResponseCallback;
import net.iGap.api.errorhandler.ErrorModel;
import net.iGap.electricity_bill.repository.api.ElectricityBillAPIRepository;
import net.iGap.electricity_bill.repository.model.Bill;
import net.iGap.electricity_bill.repository.model.BranchDebit;
import net.iGap.electricity_bill.repository.model.ElectricityResponseModel;
import net.iGap.electricity_bill.repository.model.LastBillData;
import net.iGap.helper.HelperNumerical;
import net.iGap.request.RequestMplGetBillToken;

public class ElectricityBillPayVM extends BaseAPIViewModel {

    private ObservableField<String> billID;
    private ObservableField<String> billPayID;
    private ObservableField<String> billPrice;
    private ObservableField<String> billTime;
    private ObservableField<Integer> progressVisibilityData;
    private ObservableField<Integer> progressVisibilityPay;
    private ObservableField<Integer> progressVisibilityDownload;
    private ObservableBoolean payBtnEnable;

    private MutableLiveData<LastBillData> billImage;
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

        progressVisibilityData = new ObservableField<>(View.VISIBLE);
        progressVisibilityPay = new ObservableField<>(View.GONE);
        progressVisibilityDownload = new ObservableField<>(View.GONE);
        payBtnEnable = new ObservableBoolean(true);

    }

    public void getData() {
        progressVisibilityData.set(View.VISIBLE);
        new ElectricityBillAPIRepository().getBranchDebit(debit.getID(), this,
                new ResponseCallback<ElectricityResponseModel<BranchDebit>>() {
            @Override
            public void onSuccess(ElectricityResponseModel<BranchDebit> data) {
                progressVisibilityData.set(View.GONE);
                if (data.getStatus() == 200) {
                    debit = new Bill(debit.getID(), data.getData().getPaymentIDConverted(), data.getData().getTotalBillDebtConverted(), data.getData().getPaymentDeadLineDate());
                    billPayID.set(data.getData().getPaymentIDConverted());
                    billPrice.set(new HelperNumerical().getCommaSeparatedPrice(Long.parseLong(data.getData().getTotalBillDebtConverted())) + " ریال");
                    billTime.set(data.getData().getPaymentDeadLineDate());
                }
            }

            @Override
            public void onError(ErrorModel error) {
                errorM.setValue(error);
                progressVisibilityData.set(View.GONE);
            }
        });
    }

    public void payBill (){

        if (debit.getPayID() == null || debit.getPayID().equals("") || debit.getPayID().equals("null")) {
            errorM.setValue(new ErrorModel("" , "001"));
            getData();
            return;
        }

        if (Long.parseLong(debit.getPrice().replace(",","").replace(" ریال", "")) < 10000) {
            errorM.setValue(new ErrorModel("" , "002"));
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
        if (debit.getPayID().startsWith(debit.getPrice().replace("000", "").replace(",","").replace(" ریال", ""))) {
            requestMplGetBillToken.mplGetBillToken(Long.parseLong(debit.getID()), Long.parseLong(debit.getPayID()));
        }
        else {
            requestMplGetBillToken.mplGetBillToken(Long.parseLong(debit.getID()), Long.parseLong(debit.getPrice().replace("000", "").replace(",","").replace(" ریال", "") + debit.getPayID()));
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
            public void onError(ErrorModel error) {
                progressVisibilityDownload.set(View.GONE);
                errorM.setValue(error);
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

    public ObservableField<Integer> getProgressVisibilityData() {
        return progressVisibilityData;
    }

    public void setProgressVisibilityData(ObservableField<Integer> progressVisibilityData) {
        this.progressVisibilityData = progressVisibilityData;
    }

    public ObservableField<Integer> getProgressVisibilityPay() {
        return progressVisibilityPay;
    }

    public void setProgressVisibilityPay(ObservableField<Integer> progressVisibilityPay) {
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

    public ObservableField<Integer> getProgressVisibilityDownload() {
        return progressVisibilityDownload;
    }

    public void setProgressVisibilityDownload(ObservableField<Integer> progressVisibilityDownload) {
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
}
