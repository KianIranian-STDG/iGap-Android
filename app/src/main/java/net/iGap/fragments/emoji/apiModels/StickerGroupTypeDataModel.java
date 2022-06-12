package net.iGap.fragments.emoji.apiModels;

import com.google.gson.annotations.SerializedName;

public class StickerGroupTypeDataModel {

    @SerializedName("key")
    private String key;

    @SerializedName("label")
    private String label;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

}
