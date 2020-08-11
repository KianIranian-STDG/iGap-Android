package net.iGap.model.cPay;

import com.google.gson.annotations.SerializedName;

public class PlaqueBodyModel {

    @SerializedName("plate")
    private String plaque;

    public PlaqueBodyModel() {
    }

    public PlaqueBodyModel(String plaque) {
        this.plaque = plaque;
    }

    public String getPlaque() {
        return plaque;
    }

    public void setPlaque(String plaque) {
        this.plaque = plaque;
    }
}
