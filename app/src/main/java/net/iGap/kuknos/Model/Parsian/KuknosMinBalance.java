package net.iGap.kuknos.Model.Parsian;

import com.google.gson.annotations.SerializedName;

public class KuknosMinBalance {

    @SerializedName("PMN")
    private float minBalance;

    public KuknosMinBalance() {
    }

    public KuknosMinBalance(float minBalance) {
        this.minBalance = minBalance;
    }

    public float getMinBalance() {
        return minBalance;
    }

    public void setMinBalance(float minBalance) {
        this.minBalance = minBalance;
    }
}
