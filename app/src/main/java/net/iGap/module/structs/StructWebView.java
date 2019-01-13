package net.iGap.module.structs;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class StructWebView {

    @SerializedName("url")
    @Expose
    private String url = "";


    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
