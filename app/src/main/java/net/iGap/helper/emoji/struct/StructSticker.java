package net.iGap.helper.emoji.struct;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class StructSticker {

    @SerializedName("ok")
    @Expose
    private Boolean ok;
    @SerializedName("data")
    @Expose
    private List<StructGroupSticker> data = null;

    public Boolean getOk() {
        return ok;
    }

    public void setOk(Boolean ok) {
        this.ok = ok;
    }

    public List<StructGroupSticker> getData() {
        return data;
    }

    public void setData(List<StructGroupSticker> data) {
        this.data = data;
    }

}