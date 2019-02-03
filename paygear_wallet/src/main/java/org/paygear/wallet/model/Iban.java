package org.paygear.wallet.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Iban implements Serializable {

    public Iban(String iban, boolean isDefault) {
        this.iban = iban;
        this.isDefault = isDefault;
    }

    @SerializedName("iban")
    public String iban;

    @SerializedName("default")
    public boolean isDefault;

}
