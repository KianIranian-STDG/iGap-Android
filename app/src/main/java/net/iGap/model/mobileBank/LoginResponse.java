package net.iGap.model.mobileBank;

import com.google.gson.annotations.SerializedName;

public class LoginResponse {

    @SerializedName("access_token")
    private String accessToken;

    public String getAccessToken() {
        return accessToken;
    }

}
