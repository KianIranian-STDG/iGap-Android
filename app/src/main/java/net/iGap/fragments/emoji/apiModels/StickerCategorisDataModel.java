
package net.iGap.fragments.emoji.apiModels;

import com.google.gson.annotations.Expose;

import java.util.List;

public class StickerCategorisDataModel {

    @Expose
    private List<StickerCategoryDataModel> data;

    public List<StickerCategoryDataModel> getData() {
        return data;
    }

    public void setData(List<StickerCategoryDataModel> data) {
        this.data = data;
    }

}
