package net.iGap.fragments.emoji.struct;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.vanniktech.emoji.sticker.struct.StructGroupSticker;

public class StructEachSticker {

    @SerializedName("ok")
    @Expose
    private Boolean ok;
    @SerializedName("data")
    @Expose
    private StructGroupSticker data = null;

    public Boolean getOk() {
        return ok;
    }

    public void setOk(Boolean ok) {
        this.ok = ok;
    }

    public StructGroupSticker getData() {
        return data;
    }

    public void setData(StructGroupSticker data) {
        this.data = data;
    }

}