package net.iGap.kuknos.Model.Parsian;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class KuknosRefundHistory {

    @SerializedName("refunds")
    private List<Refund> refunds;

    public KuknosRefundHistory() {
    }

    public KuknosRefundHistory(List<Refund> refunds) {
        this.refunds = refunds;
    }

    public List<Refund> getRefunds() {
        return refunds;
    }

    public void setRefunds(List<Refund> refunds) {
        this.refunds = refunds;
    }

    public class Refund {

        @SerializedName("ref_no")
        private int refNumber;
        @SerializedName("asset_code")
        private String assetCode;
        @SerializedName("asset_count")
        private float assetCount;
        @SerializedName("asset_net_count")
        private float assetNetCount;
        @SerializedName("fee")
        private float fee;
        @SerializedName("amount")
        private long amount;
        @SerializedName("settlement_status")
        private String settlementStatus;
        @SerializedName("settlement_description")
        private String settlementDescription;
        @SerializedName("hash")
        private String hash;
        @SerializedName("refund_type")
        private String refundType;

        public Refund() {
        }

        public Refund(int refNumber, String assetCode, float assetCount, float assetNetCount, float fee, long amount, String settlementStatus, String settlementDescription, String hash, String refundType) {
            this.refNumber = refNumber;
            this.assetCode = assetCode;
            this.assetCount = assetCount;
            this.assetNetCount = assetNetCount;
            this.fee = fee;
            this.amount = amount;
            this.settlementStatus = settlementStatus;
            this.settlementDescription = settlementDescription;
            this.hash = hash;
            this.refundType = refundType;

        }

        public int getRefNumber() {
            return refNumber;
        }

        public void setRefNumber(int refNumber) {
            this.refNumber = refNumber;
        }

        public String getAssetCode() {
            return assetCode;
        }

        public void setAssetCode(String assetCode) {
            this.assetCode = assetCode;
        }

        public float getAssetCount() {
            return assetCount;
        }

        public void setAssetCount(float assetCount) {
            this.assetCount = assetCount;
        }

        public float getAssetNetCount() {
            return assetNetCount;
        }

        public void setAssetNetCount(float assetNetCount) {
            this.assetNetCount = assetNetCount;
        }

        public float getFee() {
            return fee;
        }

        public void setFee(float fee) {
            this.fee = fee;
        }

        public long getAmount() {
            return amount;
        }

        public void setAmount(long amount) {
            this.amount = amount;
        }

        public String getSettlementStatus() {
            return settlementStatus;
        }

        public void setSettlementStatus(String settlementStatus) {
            this.settlementStatus = settlementStatus;
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
}

