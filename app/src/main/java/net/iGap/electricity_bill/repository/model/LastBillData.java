package net.iGap.electricity_bill.repository.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class LastBillData {

    @SerializedName("ext")
    private String ext;
    @SerializedName("document")
    private String documentBase64;
    @SerializedName("payment_dead_line")
    private String paymentDeadLine;
    @SerializedName("payment_identifier")
    private String paymentID;
    @SerializedName("bill_identifier")
    private String billID;

    public String getExt() {
        return ext;
    }

    public void setExt(String ext) {
        this.ext = ext;
    }

    public String getDocumentBase64() {
        return documentBase64;
    }

    public void setDocumentBase64(String documentBase64) {
        this.documentBase64 = documentBase64;
    }

    public String getPaymentDeadLine() {
        return paymentDeadLine;
    }

    public void setPaymentDeadLine(String paymentDeadLine) {
        this.paymentDeadLine = paymentDeadLine;
    }

    public String getPaymentID() {
        return paymentID;
    }

    public void setPaymentID(String paymentID) {
        this.paymentID = paymentID;
    }

    public String getBillID() {
        return billID;
    }

    public void setBillID(String billID) {
        this.billID = billID;
    }
}
