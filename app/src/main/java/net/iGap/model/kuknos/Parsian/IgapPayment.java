package net.iGap.model.kuknos.Parsian;

import com.google.gson.annotations.SerializedName;

public class IgapPayment {

    @SerializedName("token")
    private String token;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
