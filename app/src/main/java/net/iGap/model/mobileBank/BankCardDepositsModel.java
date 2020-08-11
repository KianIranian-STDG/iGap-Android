package net.iGap.model.mobileBank;

import com.google.gson.annotations.SerializedName;

public class BankCardDepositsModel {

    @SerializedName("deposit_number")
    private String deposit;

    @SerializedName("card_deposit_type")
    private String type;

    public String getDeposit() {
        return deposit;
    }

    public void setDeposit(String deposit) {
        this.deposit = deposit;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
