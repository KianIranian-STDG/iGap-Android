package net.iGap.kuknos.service.model;

import com.google.gson.annotations.SerializedName;

public class KuknoscheckUserM {

    @SerializedName("token")
    private String token;

    public KuknoscheckUserM() {
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}