
package net.iGap.fragments.emoji.apiModels;

import com.google.gson.annotations.Expose;

import java.util.List;

public class UserGiftStickersDataModel {

    @Expose
    private List<UserStickers> data;

    public List<UserStickers> getData() {
        return data;
    }

    public void setData(List<UserStickers> data) {
        this.data = data;
    }

}
