package net.iGap.kuknos.Model.Parsian;

import com.google.gson.annotations.SerializedName;

public class KuknosUserRefundResponse {

    @SerializedName("ref_no")
    private String refNo;
    @SerializedName("asset_code")
    private String assetCode;
    @SerializedName("asset_count")
    private String assetCount;
    @SerializedName("asset_net_count")
    private String assetNetCount;
    @SerializedName("fee")
    private String fee;
    @SerializedName("amount")
    private String amount;
    @SerializedName("settlement_status")
    private String settlementStatus;
    @SerializedName("settlement_description")
    private String settlementDescription;
    @SerializedName("hash")
    private String hash;
    @SerializedName("refund_type")
    private String refundType;
    @SerializedName("insert_date")
    private String insertDate;


    public String getRefNo() {
        return refNo;
    }

    public void setRefNo(String refNo) {
        this.refNo = refNo;
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

    public String getAssetNetCount() {
        return assetNetCount;
    }

    public void setAssetNetCount(String assetNetCount) {
        this.assetNetCount = assetNetCount;
    }

    public String getFee() {
        return fee;
    }

    public void setFee(String fee) {
        this.fee = fee;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getSettlementStatus() {
        return settlementStatus;
    }

    public void setSettlementStatus(String settlementStatus) {
        this.settlementStatus = settlementStatus;
    }

    public String getInsertDate() {
        return insertDate;
    }

    public void setInsertDate(String insertDate) {
        this.insertDate = insertDate;
    }

    public String getSettlementDescription() {
        return settlementDescription;
    }

    public void setSettlementDescription(String settlementDescription) {
        this.settlementDescription = settlementDescription;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getRefundType() {
        return refundType;
    }

    public void setRefundType(String refundType) {
        this.refundType = refundType;
    }
}
