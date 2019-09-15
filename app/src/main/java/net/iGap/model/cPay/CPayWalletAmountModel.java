package net.iGap.model.cPay;

import com.google.gson.annotations.SerializedName;

public class CPayWalletAmountModel {

    @SerializedName("account_inventory")
    private String amount;

    public CPayWalletAmountModel() {
    }

    public String getAmount() {
        return amount;
    }
}
