package net.iGap.electricity_bill.repository.model;

import com.google.gson.annotations.SerializedName;

public class BranchDebit {

    @SerializedName("bill_identifier")
    private String billID;
    @SerializedName("total_register_debt")
    private int totalRegisterDebt;
    @SerializedName("payment_identifier")
    private String paymentID;
    @SerializedName("total_bill_debt")
    private String totalBillDebt;
    @SerializedName("other_account_debt")
    private String otherAccountDebt;
    @SerializedName("payment_dead_line")
    private String paymentDeadLineDate;
    @SerializedName("last_read_date")
    private String lastReadDate;
    @SerializedName("last_gross_amt")
    private int lastGrossAmount;
    @SerializedName("last_sale_year")
    private int lastSaleYear;
    @SerializedName("last_sale_prd")
    private String lastSalePeriod;
    @SerializedName("lastupdatetime")
    private String lastUpdateTime;

    public String getBillID() {
        return billID;
    }

    public void setBillID(String billID) {
        this.billID = billID;
    }

    public int getTotalRegisterDebt() {
        return totalRegisterDebt;
    }

    public void setTotalRegisterDebt(int totalRegisterDebt) {
        this.totalRegisterDebt = totalRegisterDebt;
    }

    public String getPaymentID() {
        return paymentID;
    }

    public void setPaymentID(String paymentID) {
        this.paymentID = paymentID;
    }

    public String getTotalBillDebt() {
        return totalBillDebt;
    }

    public void setTotalBillDebt(String totalBillDebt) {
        this.totalBillDebt = totalBillDebt;
    }

    public String getOtherAccountDebt() {
        return otherAccountDebt;
    }

    public void setOtherAccountDebt(String otherAccountDebt) {
        this.otherAccountDebt = otherAccountDebt;
    }

    public String getPaymentDeadLineDate() {
        return paymentDeadLineDate;
    }

    public void setPaymentDeadLineDate(String paymentDeadLineDate) {
        this.paymentDeadLineDate = paymentDeadLineDate;
    }

    public String getLastReadDate() {
        return lastReadDate;
    }

    public void setLastReadDate(String lastReadDate) {
        this.lastReadDate = lastReadDate;
    }

    public int getLastGrossAmount() {
        return lastGrossAmount;
    }

    public void setLastGrossAmount(int lastGrossAmount) {
        this.lastGrossAmount = lastGrossAmount;
    }

    public int getLastSaleYear() {
        return lastSaleYear;
    }

    public void setLastSaleYear(int lastSaleYear) {
        this.lastSaleYear = lastSaleYear;
    }

    public String getLastSalePeriod() {
        return lastSalePeriod;
    }

    public void setLastSalePeriod(String lastSalePeriod) {
        this.lastSalePeriod = lastSalePeriod;
    }

    public String getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(String lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }
}
