package net.iGap.model.cPay;

import com.google.gson.annotations.SerializedName;

public class ChargeWalletBodyModel {

    @SerializedName("plate")
    private String plaque ;

    @SerializedName("amount")
    private String amount ;

    public ChargeWalletBodyModel() {
    }

    public ChargeWalletBodyModel(String plaque, String amount) {
        this.plaque = plaque;
        this.amount = amount;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getPlaque() {
        return plaque;
    }

    public void setPlaque(String plaque) {
        this.plaque = plaque;
    }

}
