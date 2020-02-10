package net.iGap.fragments.emoji.apiModels;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class StickersDataModel {
    @SerializedName("data")
    private List<StickerDataModel> data;

    public void setData(List<StickerDataModel> data) {
        this.data = data;
    }

    public List<StickerDataModel> getData() {
        return data;
    }
}
