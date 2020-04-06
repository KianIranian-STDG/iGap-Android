package net.iGap.model.igasht;

import androidx.annotation.Nullable;

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

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj instanceof IGashtVouchers) {
            return voucherId == ((IGashtVouchers) obj).getVoucherId();
        }
        return super.equals(obj);
    }
}
