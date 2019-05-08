package net.iGap.fragments.emoji.struct;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class StructCategoryResult {

    @SerializedName("data")
    @Expose
    private StickerCategory[] data;

    @SerializedName("ok")
    @Expose
    private boolean ok;

    public StickerCategory[] getStickerCategories() {
        return data;
    }

    public void setStickerCategories(StickerCategory[] stickerCategories) {
        this.data = stickerCategories;
    }

    public boolean isOk() {
        return ok;
    }

    public void setOk(boolean ok) {
        this.ok = ok;
    }

    @Override
    public String toString() {
        return "StructCategoryResult [stickerCategories = , ok = " + ok + "]";
    }

}
