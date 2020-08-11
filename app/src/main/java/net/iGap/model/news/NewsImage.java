package net.iGap.model.news;

import com.google.gson.annotations.SerializedName;

public class NewsImage {
    @SerializedName("Original")
    private String original;
    @SerializedName("thumb128")
    private String tmb128;
    @SerializedName("thumb256")
    private String tmb256;
    @SerializedName("thumb512")
    private String tmb512;

    public String getOriginal() {
        if (original == null || original.isEmpty())
            return null;
        return original;
    }

    public void setOriginal(String original) {
        this.original = original;
    }

    public String getTmb128() {
        if (tmb128 == null || tmb128.isEmpty())
            return null;
        return tmb128;
    }

    public void setTmb128(String tmb128) {
        this.tmb128 = tmb128;
    }

    public String getTmb256() {
        if (tmb256 == null || tmb256.isEmpty())
            return null;
        return tmb256;
    }

    public void setTmb256(String tmb256) {
        this.tmb256 = tmb256;
    }

    public String getTmb512() {
        if (tmb512 == null || tmb512.isEmpty())
            return null;
        return tmb512;
    }

    public void setTmb512(String tmb512) {
        this.tmb512 = tmb512;
    }
}
