package net.iGap.model.igasht;

import com.google.gson.annotations.SerializedName;

public class IGashtServiceAmount {

    @SerializedName("voucherinfo_id")
    private int voucherinfoId;
    @SerializedName("voucher_type_id")
    private int voucherTypeId;
    @SerializedName("voucher_type")
    private String voucherType;
    @SerializedName("amount")
    private int amount;

    public int getVoucherinfoId() {
        return voucherinfoId;
    }

    public int getVoucherTypeId() {
        return voucherTypeId;
    }

    public String getVoucherType() {
        return voucherType;
    }

    public int getAmount() {
        return amount;
    }


}
