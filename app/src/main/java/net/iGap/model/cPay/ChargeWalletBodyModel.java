package net.iGap.model.cPay;

import com.google.gson.annotations.SerializedName;

public class ChargeWalletBodyModel {

    @SerializedName("plate")
    private String plaque;

    @SerializedName("amount")
    private long amount;

    public ChargeWalletBodyModel() {
    }

    public ChargeWalletBodyModel(String plaque, long amount) {
        this.plaque = plaque;
        this.amount = amount;
    }

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

    public String getPlaque() {
        return plaque;
    }

    public void setPlaque(String plaque) {
        this.plaque = plaque;
    }

}
