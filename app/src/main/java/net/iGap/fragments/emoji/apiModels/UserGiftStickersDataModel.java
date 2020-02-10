
package net.iGap.fragments.emoji.apiModels;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class UserGiftStickersDataModel {

    @SerializedName("data")
    private List<UserStickers> data;

    public List<UserStickers> getData() {
        return data;
    }

    public void setData(List<UserStickers> data) {
        this.data = data;
    }

}
