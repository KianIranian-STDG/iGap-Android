package net.iGap.module.api.beepTunes;

import com.google.gson.annotations.SerializedName;

public class DownloadLinks {

    @SerializedName("H320")
    private String h360;
    @SerializedName("L128")
    private String l128;
    @SerializedName("L64")
    private String l64;

    public String getH320() {
        return h360;
    }

    public void setH360(String h360) {
        this.h360 = h360;
    }

    public String getL128() {
        return l128;
    }

    public void setL128(String l128) {
        this.l128 = l128;
    }

    public String getL64() {
        return l64;
    }

    public void setL64(String l64) {
        this.l64 = l64;
    }

}
