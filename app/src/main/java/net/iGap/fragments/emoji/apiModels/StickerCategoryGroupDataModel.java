package net.iGap.fragments.emoji.apiModels;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class StickerCategoryGroupDataModel {
    @SerializedName("data")
    List<StickerGroupDataModel> data;

    public void setData(List<StickerGroupDataModel> data) {
        this.data = data;
    }

    public List<StickerGroupDataModel> getData() {
        return data;
    }
}
