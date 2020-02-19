package net.iGap.model.kuknos.Parsian;

import com.google.gson.annotations.SerializedName;

public class KuknosHash {

    @SerializedName("hash")
    private String hash;

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }
}
