package net.iGap.module.api.beepTunes;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PurchaseList {

    @SerializedName("count")
    private Long mCount;
    @SerializedName("purchases")
    private List<Purchase> mPurchases;

    public Long getCount() {
        return mCount;
    }

    public void setCount(Long count) {
        mCount = count;
    }

    public List<Purchase> getPurchases() {
        return mPurchases;
    }

    public void setPurchases(List<Purchase> purchases) {
        mPurchases = purchases;
    }

}
