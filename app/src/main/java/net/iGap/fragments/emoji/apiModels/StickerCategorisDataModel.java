
package net.iGap.fragments.emoji.apiModels;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class StickerCategorisDataModel {

    @SerializedName("data")
    private List<StickerCategoryDataModel> data;

    public List<StickerCategoryDataModel> getData() {
        return data;
    }

    public void setData(List<StickerCategoryDataModel> data) {
        this.data = data;
    }

}
