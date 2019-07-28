package net.iGap.igasht.locationdetail.buyticket;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class IGashtVouchers {

    @SerializedName("voucher_id")
    @Expose
    private int voucherId;
    @SerializedName("count")
    @Expose
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
