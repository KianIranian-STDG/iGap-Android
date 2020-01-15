package net.iGap.mobileBank.repository.model;

import com.google.gson.annotations.SerializedName;

public class LoginResponse {

    @SerializedName("access_token")
    private String accessToken;
    @SerializedName("name")
    private String name;

    public String getAccessToken() {
        return accessToken;
    }

    public String getName() {
        return name;
    }
}
