package net.iGap.model.qrCodePayment;

import com.google.gson.annotations.SerializedName;

public class Token {

    @SerializedName("token")
    private String mToken;

    public String getToken() {
        return mToken;
    }

    public void setToken(String token) {
        this.mToken = token;
    }
}
