package net.iGap.payment;

import com.google.gson.annotations.SerializedName;

public class CheckOrderResponse {

    @SerializedName("info")
    private PaymentInfo info;
    @SerializedName("redirect_url")
    private String redirectUrl;

    public PaymentInfo getInfo() {
        return info;
    }

    public String getRedirectUrl() {
        return redirectUrl;
    }
}
