package net.iGap.fragments.emoji.apiModels;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class StickerGroupTypesDataModel {
    
    @SerializedName("sticker_group_types")
    private List<StickerGroupTypeDataModel> data;

    public List<StickerGroupTypeDataModel> getData() {
        return data;
    }

    public void setData(List<StickerGroupTypeDataModel> data) {
        this.data = data;
    }
}
