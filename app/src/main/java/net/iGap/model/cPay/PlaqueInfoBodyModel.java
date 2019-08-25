package net.iGap.model.cPay;

import com.google.gson.annotations.SerializedName;

public class PlaqueInfoBodyModel {

    @SerializedName("plate")
    private String plaque ;

    public PlaqueInfoBodyModel() {
    }

    public PlaqueInfoBodyModel(String plaque) {
        this.plaque = plaque;
    }

    public String getPlaque() {
        return plaque;
    }

    public void setPlaque(String plaque) {
        this.plaque = plaque;
    }
}
