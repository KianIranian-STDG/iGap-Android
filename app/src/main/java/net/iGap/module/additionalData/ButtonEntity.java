package net.iGap.module.additionalData;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ButtonEntity {
    @SerializedName("actionType")
    @Expose
    private Integer actionType;
    @SerializedName("label")
    @Expose
    private String lable;
    @SerializedName("imageUrl")
    @Expose
    private String imageUrl;
    @SerializedName("value")
    @Expose
    private String value;

    private String jsonObject;

    public String getJsonObject() {
        return jsonObject;
    }

    public void setJsonObject(String jsonObject) {
        this.jsonObject = jsonObject;
    }

    public Integer getActionType() {
        return actionType;
    }

    public void setActionType(Integer actionType) {
        this.actionType = actionType;
    }

    public String getLable() {
        return lable;
    }

    public void setLable(String lable) {
        this.lable = lable;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

}
