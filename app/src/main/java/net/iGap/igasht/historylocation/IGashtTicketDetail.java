package net.iGap.igasht.historylocation;

import com.google.gson.annotations.SerializedName;

import java.security.Timestamp;
import java.util.List;

public class IGashtTicketDetail {
    @SerializedName("voucher_number")
    private String voucherNumber;
    @SerializedName("total_amount")
    private int totalAmount;
    @SerializedName("location_id")
    private int locationId;
    @SerializedName("location_name")
    private String locationName;
    @SerializedName("location_english_name")
    private String locationEnglishName;
    @SerializedName("sale_type_id")
    private int saleTypeId;
    @SerializedName("sale_type_name")
    private String saleTypeName;
    @SerializedName("voucher_info")
    private List<IGashtVoucherInfo> voucherInfo;
    @SerializedName("created")
    private Timestamp timestamp;

    public String getVoucherNumber() {
        return voucherNumber;
    }

    public int getTotalAmount() {
        return totalAmount;
    }

    public int getLocationId() {
        return locationId;
    }

    public String getLocationName() {
        return locationName;
    }

    public String getLocationEnglishName() {
        return locationEnglishName;
    }

    public int getSaleTypeId() {
        return saleTypeId;
    }

    public String getSaleTypeName() {
        return saleTypeName;
    }

    public List<IGashtVoucherInfo> getVoucherInfo() {
        return voucherInfo;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }
}
