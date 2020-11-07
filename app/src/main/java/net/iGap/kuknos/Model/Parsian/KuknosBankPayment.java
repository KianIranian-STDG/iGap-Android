package net.iGap.kuknos.Model.Parsian;

import com.google.gson.annotations.SerializedName;

public class KuknosBankPayment {

    @SerializedName("token")
    private String token;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
