package net.iGap.kuknos.service.model;

import com.google.gson.annotations.SerializedName;

public class KuknosFederation {

    @SerializedName("public_key")
    private String publicKey;

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

}