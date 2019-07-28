package net.iGap.igasht.locationdetail.buyticket;

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

    private int count;
    private String title;

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

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
