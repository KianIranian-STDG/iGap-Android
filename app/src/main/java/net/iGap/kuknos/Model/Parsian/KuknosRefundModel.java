package net.iGap.kuknos.Model.Parsian;

import com.google.gson.annotations.SerializedName;

public class KuknosRefundModel {

    @SerializedName("asset-type")
    private String assetType;
    @SerializedName("asset_code")
    private String assetCode;
    @SerializedName("refund_type")
    private String refundType;
    @SerializedName("max_refund")
    private int maxRefund;
    @SerializedName("min_refund")
    private int minRefund;
    @SerializedName("destination_public_key")
    private String publicKey;
    @SerializedName("fee_fixed")
    private int feeFixed;

    public KuknosRefundModel() {

    }

    public KuknosRefundModel(String assetType, String assetCode, String refundType, int maxRefund, int minRefund, String publicKey, int feeFixed) {
        this.assetType = assetType;
        this.assetCode = assetCode;
        this.refundType = refundType;
        this.maxRefund = maxRefund;
        this.minRefund = minRefund;
        this.publicKey = publicKey;
        this.feeFixed = feeFixed;
    }

    public String getAssetType() {
        return assetType;
    }

    public void setAssetType(String assetType) {
        this.assetType = assetType;
    }

    public String getAssetCode() {
        return assetCode;
    }

    public void setAssetCode(String assetCode) {
        this.assetCode = assetCode;
    }

    public String getRefundType() {
        return refundType;
    }

    public void setRefundType(String refundType) {
        this.refundType = refundType;
    }

    public int getMaxRefund() {
        return maxRefund;
    }

    public void setMaxRefund(int maxRefund) {
        this.maxRefund = maxRefund;
    }

    public int getMinRefund() {
        return minRefund;
    }

    public void setMinRefund(int minRefund) {
        this.minRefund = minRefund;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public int getFeeFixed() {
        return feeFixed;
    }

    public void setFeeFixed(int feeFixed) {
        this.feeFixed = feeFixed;
    }
}
