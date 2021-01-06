package net.iGap.model.paymentPackage;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class FaceValue {
    @SerializedName("key")
    @Expose
    private Integer key;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("selected")
    @Expose
    private Boolean selected;

    public Integer getKey() {
        return key;
    }

    public void setKey(Integer key) {
        this.key = key;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Boolean getSelected() {
        return selected;
    }

    public void setSelected(Boolean selected) {
        this.selected = selected;
    }
}