package net.iGap.fragments.emoji.apiModels;

import com.google.gson.annotations.SerializedName;

public class IssueDataModel {
    @SerializedName("token")
    private String token;
    @SerializedName("id")
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
