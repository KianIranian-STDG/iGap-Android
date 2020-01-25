package net.iGap.fragments.emoji.apiModels;

import com.google.gson.annotations.Expose;

public class RsaDataModel {
    @Expose
    private String data;

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
