package net.iGap.mobileBank.repository.model;

import com.google.gson.annotations.SerializedName;

public class BankShebaModel {

    @SerializedName("iban")
    private String sheba ;

    public String getSheba() {
        return sheba;
    }

    public void setSheba(String sheba) {
        this.sheba = sheba;
    }
}
