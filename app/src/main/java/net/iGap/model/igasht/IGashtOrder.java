package net.iGap.model.igasht;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class IGashtOrder {
    @SerializedName("phone_number")
    private String phoneNumber;
    @SerializedName("country_id")
    private int countryId;
    @SerializedName("province_id")
    private int provinceId;
    @SerializedName("location_id")
    private int locationId;

    public List<IGashtVouchers> getVouchers() {
        return vouchers;
    }

    public void setVouchers(List<IGashtVouchers> vouchers) {
        this.vouchers = vouchers;
    }

    @SerializedName("vouchers")
    private List<IGashtVouchers> vouchers;
    @SerializedName("domain")
    private String mDomain;
    @SerializedName("user_cellphone")
    private String mUserCellphone;

    public IGashtOrder(String phoneNumber, int countryId, int provinceId, int locationId, List<IGashtVouchers> vouchers) {
        this.phoneNumber = phoneNumber;
        this.countryId = countryId;
        this.provinceId = provinceId;
        this.locationId = locationId;
        this.vouchers = vouchers;
    }
}
