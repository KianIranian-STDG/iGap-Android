package net.iGap.model.igasht;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class IGashtServiceAmount {

    @SerializedName("voucherinfo_id")
    private int voucherinfoId;
    @SerializedName("voucher_type_id")
    private int voucherTypeId;
    @SerializedName("voucher_type")
    private String voucherType;
    @SerializedName("amount")
    private int amount;
    @SerializedName("english_name")
    private String mEnglishName;
    @SerializedName("is_free")
    private Boolean mIsFree;
    @SerializedName("languages")
    private List<Language> mLanguages;
    @SerializedName("service_times")
    private List<Object> mServiceTimes;
    @SerializedName("voucher_capacity")
    private String mVoucherCapacity;

    public String getmEnglishName() {
        return mEnglishName;
    }

    public void setmEnglishName(String mEnglishName) {
        this.mEnglishName = mEnglishName;
    }

    public Boolean getmIsFree() {
        return mIsFree;
    }

    public void setmIsFree(Boolean mIsFree) {
        this.mIsFree = mIsFree;
    }

    public List<Language> getmLanguages() {
        return mLanguages;
    }

    public void setmLanguages(List<Language> mLanguages) {
        this.mLanguages = mLanguages;
    }

    public List<Object> getmServiceTimes() {
        return mServiceTimes;
    }

    public void setmServiceTimes(List<Object> mServiceTimes) {
        this.mServiceTimes = mServiceTimes;
    }

    public String getmVoucherCapacity() {
        return mVoucherCapacity;
    }

    public void setmVoucherCapacity(String mVoucherCapacity) {
        this.mVoucherCapacity = mVoucherCapacity;
    }

    public void setVoucherinfoId(int voucherinfoId) {
        this.voucherinfoId = voucherinfoId;
    }

    public void setVoucherTypeId(int voucherTypeId) {
        this.voucherTypeId = voucherTypeId;
    }

    public void setVoucherType(String voucherType) {
        this.voucherType = voucherType;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

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
