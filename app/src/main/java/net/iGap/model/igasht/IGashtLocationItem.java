package net.iGap.model.igasht;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import net.iGap.G;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class IGashtLocationItem implements Parcelable {

    @SerializedName("id")
    private int id;
    @SerializedName("payment_id")
    private String paymentId;
    @SerializedName("warehoff")
    private boolean warehoff;
    @SerializedName("has_QR")
    private boolean hasQR;
    @SerializedName("print_pos")
    private boolean printPos;
    @SerializedName("location_name")
    private String locationName;
    @SerializedName("activation")
    private boolean activation;
    @SerializedName("location_english_name")
    private String locationEnglishName;
    @SerializedName("location_address")
    private String locationAddress;
    @SerializedName("location_english_address")
    private String locationEnglishAddress;
    @SerializedName("location")
    private String location;
    @SerializedName("text")
    private LocationDetail detail;
    @SerializedName("extra")
    private ExtraDetail mExtraDetail;
    @SerializedName("bulky_sale")
    private Boolean mBulkySale;
    @SerializedName("description")
    private String mDescription;
    @SerializedName("introduction")
    private String mIntroduction;
    @SerializedName("latitude")
    private Double mLatitude;
    @SerializedName("longitude")
    private Double mLongitude;
    @SerializedName("mandatory_entrance")
    private Boolean mMandatoryEntrance;
    @SerializedName("presell_activation")
    private Boolean mPresellActivation;


    private IGashtLocationItem(@NotNull Parcel in) {
        id = in.readInt();
        paymentId = in.readString();
        warehoff = in.readByte() != 0;
        hasQR = in.readByte() != 0;
        printPos = in.readByte() != 0;
        locationName = in.readString();
        activation = in.readByte() != 0;
        locationEnglishName = in.readString();
        locationAddress = in.readString();
        locationEnglishAddress = in.readString();
        location = in.readString();
        detail = in.readParcelable(LocationDetail.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(paymentId);
        dest.writeByte((byte) (warehoff ? 1 : 0));
        dest.writeByte((byte) (hasQR ? 1 : 0));
        dest.writeByte((byte) (printPos ? 1 : 0));
        dest.writeString(locationName);
        dest.writeByte((byte) (activation ? 1 : 0));
        dest.writeString(locationEnglishName);
        dest.writeString(locationAddress);
        dest.writeString(locationEnglishAddress);
        dest.writeString(location);
        dest.writeParcelable(detail, flags);
    }

    public static final Creator<IGashtLocationItem> CREATOR = new Creator<IGashtLocationItem>() {
        @NotNull
        @Contract("_ -> new")
        @Override
        public IGashtLocationItem createFromParcel(Parcel in) {
            return new IGashtLocationItem(in);
        }

        @NotNull
        @Contract(value = "_ -> new", pure = true)
        @Override
        public IGashtLocationItem[] newArray(int size) {
            return new IGashtLocationItem[size];
        }
    };

    public String getNameWithLanguage() {
        switch (G.selectedLanguage) {
            case "en":
                return getLocationEnglishName();
            case "fa":
                return getLocationName();
            default:
                return getLocationName();
        }
    }

    public String getAddressWithLanguage() {
        switch (G.selectedLanguage) {
            case "en":
                return getLocationEnglishAddress();
            case "fa":
                return getLocationAddress();
            default:
                return getLocationAddress();
        }
    }


    public int getId() {
        return id;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public boolean isWarehoff() {
        return warehoff;
    }

    public boolean isHasQR() {
        return hasQR;
    }

    public boolean isPrintPos() {
        return printPos;
    }

    public String getLocationName() {
        return locationName;
    }

    public boolean isActivation() {
        return activation;
    }

    public String getLocationEnglishName() {
        return locationEnglishName;
    }

    public String getLocationAddress() {
        return locationAddress;
    }

    public String getLocationEnglishAddress() {
        return locationEnglishAddress;
    }

    public String getLocation() {
        return location;
    }

    public LocationDetail getDetail() {
        return detail;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    public ExtraDetail getmExtraDetail() {
        return mExtraDetail;
    }

    public void setmExtraDetail(ExtraDetail mExtraDetail) {
        this.mExtraDetail = mExtraDetail;
    }

    public Boolean getmBulkySale() {
        return mBulkySale;
    }

    public void setmBulkySale(Boolean mBulkySale) {
        this.mBulkySale = mBulkySale;
    }

    public String getmDescription() {
        return mDescription;
    }

    public void setmDescription(String mDescription) {
        this.mDescription = mDescription;
    }

    public String getmIntroduction() {
        return mIntroduction;
    }

    public void setmIntroduction(String mIntroduction) {
        this.mIntroduction = mIntroduction;
    }

    public Double getmLatitude() {
        return mLatitude;
    }

    public void setmLatitude(Double mLatitude) {
        this.mLatitude = mLatitude;
    }

    public Double getmLongitude() {
        return mLongitude;
    }

    public void setmLongitude(Double mLongitude) {
        this.mLongitude = mLongitude;
    }

    public Boolean getmMandatoryEntrance() {
        return mMandatoryEntrance;
    }

    public void setmMandatoryEntrance(Boolean mMandatoryEntrance) {
        this.mMandatoryEntrance = mMandatoryEntrance;
    }

    public Boolean getmPresellActivation() {
        return mPresellActivation;
    }

    public void setmPresellActivation(Boolean mPresellActivation) {
        this.mPresellActivation = mPresellActivation;
    }
}
