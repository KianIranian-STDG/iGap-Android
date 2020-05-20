package net.iGap.model.payment;

import com.google.gson.annotations.SerializedName;

public class CheckOrderStatusResponse {

    @SerializedName("status")
    private String status;
    @SerializedName("message")
    private String message;
    @SerializedName("info")
    private PaymentInfo paymentInfo;

    public String getStatus() {
        return status;
    }

    public PaymentInfo getPaymentInfo() {
        return paymentInfo;
    }

    public String getMessage() {
        return message;
    }

    public boolean isPaymentSuccess() {
        return status.equals("SUCCESS");
    }
}
