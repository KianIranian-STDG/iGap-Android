package net.iGap.model.electricity_bill;

import com.google.gson.annotations.SerializedName;

public class PaidBill {

    @SerializedName("payment_identifier")
    private int paymentID;
    @SerializedName("payment_date")
    private String paymentDate;
    @SerializedName("payment_amt")
    private String paymentAmount;
    @SerializedName("bank_code")
    private String bankCode;
    @SerializedName("ref_code")
    private String refCode;
    @SerializedName("chanel_type")
    private String chanelType;
    @SerializedName("process_date")
    private int processDate;

    public int getPaymentID() {
        return paymentID;
    }

    public void setPaymentID(int paymentID) {
        this.paymentID = paymentID;
    }

    public String getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(String paymentDate) {
        this.paymentDate = paymentDate;
    }

    public String getPaymentAmount() {
        return paymentAmount;
    }

    public void setPaymentAmount(String paymentAmount) {
        this.paymentAmount = paymentAmount;
    }

    public String getBankCode() {
        return bankCode;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    public String getRefCode() {
        return refCode;
    }

    public void setRefCode(String refCode) {
        this.refCode = refCode;
    }

    public String getChanelType() {
        return chanelType;
    }

    public void setChanelType(String chanelType) {
        this.chanelType = chanelType;
    }

    public int getProcessDate() {
        return processDate;
    }

    public void setProcessDate(int processDate) {
        this.processDate = processDate;
    }
}
