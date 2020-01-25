package net.iGap.fragments.emoji.apiModels;

import com.google.gson.annotations.Expose;

public class IssueDataModel {
    @Expose
    private String token;
    @Expose
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
