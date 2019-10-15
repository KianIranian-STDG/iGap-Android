package net.iGap.electricity_bill.viewmodel;

import android.view.View;

import androidx.databinding.ObservableField;

import net.iGap.api.apiService.BaseAPIViewModel;

public class ElectricityBillPayVM extends BaseAPIViewModel {

    private ObservableField<String> billID;
    private ObservableField<String> billPayID;
    private ObservableField<String> billPrice;
    private ObservableField<String> billTime;
    private ObservableField<Integer> progressVisibilityData;
    private ObservableField<Integer> progressVisibilityPay;

    public ElectricityBillPayVM() {

        billID = new ObservableField<>();
        billPayID = new ObservableField<>();
        billPrice = new ObservableField<>();
        billTime = new ObservableField<>();

        progressVisibilityData = new ObservableField<>(View.VISIBLE);
        progressVisibilityPay = new ObservableField<>(View.GONE);

    }

    public void showBillImage() {

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
}
