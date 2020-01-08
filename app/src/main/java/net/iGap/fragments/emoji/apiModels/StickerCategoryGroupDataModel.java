package net.iGap.fragments.emoji.apiModels;

import com.google.gson.annotations.Expose;

import java.util.List;

public class StickerCategoryGroupDataModel {
    @Expose
    List<StickerGroupDataModel> data;

    public void setData(List<StickerGroupDataModel> data) {
        this.data = data;
    }

    public List<StickerGroupDataModel> getData() {
        return data;
    }
}
