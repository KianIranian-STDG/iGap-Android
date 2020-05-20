package net.iGap.model.paymentPackage;

import com.google.gson.annotations.SerializedName;

public class MciPurchaseResponse {
    @SerializedName("token")
    private String token;

    public String getToken() {
        return token;
    }
}
