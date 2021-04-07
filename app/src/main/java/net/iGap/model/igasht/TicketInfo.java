package net.iGap.model.igasht;

import com.google.gson.annotations.SerializedName;

import net.iGap.G;
import net.iGap.model.igasht.IGashtVoucherInfo;

import java.util.List;

public class TicketInfo {

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
    private List<IGashtVoucherInfo> voucherinfo;
    @SerializedName("created")
    private float created;

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

    public List<IGashtVoucherInfo> getVoucherinfo() {
        return voucherinfo;
    }

    public float getCreated() {
        return created;
    }

    public String getLocationNameWithLanguage() {
        switch (G.selectedLanguage) {
            case "en":
                return getLocationEnglishName();
            case "fa":
                return getLocationName();
            default:
                return getLocationName();
        }
    }
}
