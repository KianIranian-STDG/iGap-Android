package net.iGap.model.cPay;

import com.google.gson.annotations.SerializedName;

public class ChargeWalletModel {

    @SerializedName("token")
    private String token;

    public String getToken() {
        return token;
    }
}
