package net.iGap.model.electricity_bill;

import android.text.format.DateUtils;

import com.google.gson.annotations.SerializedName;

import net.iGap.helper.HelperCalander;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

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
        if (paymentDeadLine == null || paymentDeadLine.isEmpty())
            return "";

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss ");
        try {
            Date mDate = sdf.parse(paymentDeadLine.replace("T", " ").replace("Z", " "));
            long timeInMilliseconds = mDate.getTime();
            return HelperCalander.checkHijriAndReturnTime(timeInMilliseconds / DateUtils.SECOND_IN_MILLIS);
        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
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
