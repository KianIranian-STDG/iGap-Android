package net.iGap.model.cPay;

import com.google.gson.annotations.SerializedName;

public class RegisterPlaqueModel {

    @SerializedName("PAN")
    private String pan;

    public String getPan() {
        return pan;
    }
}
