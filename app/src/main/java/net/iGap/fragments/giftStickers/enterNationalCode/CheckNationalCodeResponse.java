package net.iGap.fragments.giftStickers.enterNationalCode;

import com.google.gson.annotations.SerializedName;

public class CheckNationalCodeResponse {
    @SerializedName("success")
    private boolean success;

    public boolean isSuccess() {
        return success;
    }
}
