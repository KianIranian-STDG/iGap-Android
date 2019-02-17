package net.iGap.fragments.emoji.struct;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class StructStickerResult implements Serializable {

    @SerializedName("ok")
    @Expose
    private boolean isSuccess;


    public boolean isSuccess() {
        return isSuccess;
    }

    public void isSuccess(boolean isSuccess) {
        this.isSuccess = isSuccess;
    }
}
