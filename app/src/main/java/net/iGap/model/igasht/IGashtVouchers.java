package net.iGap.model.igasht;

import androidx.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

public class IGashtVouchers {

    @SerializedName("count")
    private int count;
    @SerializedName("service_time_id")
    private Long mServiceTimeId;
    @SerializedName("voucher_id")
    private int voucherId;

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

    public Long getmServiceTimeId() {
        return mServiceTimeId;
    }

    public void setmServiceTimeId(Long mServiceTimeId) {
        this.mServiceTimeId = mServiceTimeId;
    }
}
