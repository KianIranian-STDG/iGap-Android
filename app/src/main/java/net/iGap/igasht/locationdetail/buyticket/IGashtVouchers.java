package net.iGap.igasht.locationdetail.buyticket;

import com.google.gson.annotations.SerializedName;

public class IGashtVouchers {

    @SerializedName("voucher_id")
    private int voucherId;
    @SerializedName("count")
    private int count;

    public IGashtVouchers(int voucherId, int count) {
        this.voucherId = voucherId;
        this.count = count;
    }

    public int getVoucherId() {
        return voucherId;
    }

    public int getCount() {
        return count;
    }
}
