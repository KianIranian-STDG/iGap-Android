package net.iGap.payment;

import com.google.gson.annotations.SerializedName;

public class CheckOrderStatusResponse {

    @SerializedName("status")
    private String status;
    @SerializedName("info")
    private PaymentInfo paymentInfo;

    public CheckOrderStatusResponse(String status, PaymentInfo baseProduct) {
        this.status = status;
        this.paymentInfo = baseProduct;
    }

    public String getStatus() {
        return status;
    }

    public PaymentInfo getPaymentInfo() {
        return paymentInfo;
    }

    public boolean isPaymentSuccess() {
        return status.equals("SUCCESS");
    }
}
