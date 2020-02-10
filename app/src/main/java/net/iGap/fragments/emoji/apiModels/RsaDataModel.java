package net.iGap.fragments.emoji.apiModels;

import com.google.gson.annotations.SerializedName;

public class RsaDataModel {
    @SerializedName("data")
    private String data;

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
