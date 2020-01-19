package net.iGap.fragments.emoji.apiModels;

import com.google.gson.annotations.Expose;

public class IssueDataModel {
    @Expose
    private String token;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
