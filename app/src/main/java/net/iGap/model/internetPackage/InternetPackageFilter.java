package net.iGap.model.internetPackage;

import com.google.gson.annotations.SerializedName;

public class InternetPackageFilter {

    @SerializedName("title")
    private String title;

    @SerializedName("type")
    private String type;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
