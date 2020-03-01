package net.iGap.model.payment;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CheckOrderResponse {

    @SerializedName("info")
    private PaymentInfo info;
    @SerializedName("features")
    private List<PaymentFeature> discountOption;
    @SerializedName("redirect_url")
    private String redirectUrl;

    public PaymentInfo getInfo() {
        return info;
    }

    public String getRedirectUrl() {
        return redirectUrl;
    }

    public List<PaymentFeature> getDiscountOption() {
        return discountOption;
    }
}
