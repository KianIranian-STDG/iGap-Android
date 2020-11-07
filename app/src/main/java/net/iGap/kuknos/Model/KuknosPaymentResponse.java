package net.iGap.kuknos.Model;

import com.google.gson.annotations.SerializedName;

public class KuknosPaymentResponse {

    @SerializedName("amount")
    private double totalAmount;
    @SerializedName("asset_code")
    private String assetCode;
    @SerializedName("asset_count")
    private String assetCount;
    @SerializedName("asset_price")
    private String assetPrice;
    @SerializedName("hash")
    private String hash;

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getAssetCode() {
        return assetCode;
    }

    public void setAssetCode(String assetCode) {
        this.assetCode = assetCode;
    }

    public String getAssetCount() {
        return assetCount;
    }

    public void setAssetCount(String assetCount) {
        this.assetCount = assetCount;
    }

    public String getAssetPrice() {
        return assetPrice;
    }

    public void setAssetPrice(String assetPrice) {
        this.assetPrice = assetPrice;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }
}
