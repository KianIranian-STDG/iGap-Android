package net.iGap.kuknos.service.model.Parsian;

import com.google.gson.annotations.SerializedName;

import org.stellar.sdk.responses.AssetResponse;
import org.stellar.sdk.responses.Response;

import java.util.List;

public class KuknosAsset {

    @SerializedName("assets")
    private List<Asset> assets;

    public List<Asset> getAssets() {
        return assets;
    }

    public void setAssets(List<Asset> assets) {
        this.assets = assets;
    }

    public class Asset extends Response {
        @SerializedName("asset_type")
        private String assetType;
        @SerializedName("asset_code")
        private String assetCode;
        @SerializedName("asset_issuer")
        private String assetIssuer;
        @SerializedName("paging_token")
        private String pagingToken;
        @SerializedName("amount")
        private String amount;
        @SerializedName("num_accounts")
        private int numAccounts;
        @SerializedName("flags")
        private AssetResponse.Flags flags;
        @SerializedName("_links")
        private AssetResponse.Links links;
        @SerializedName("label")
        private String label;
        @SerializedName("remain_amount")
        private int remainAmount;
        @SerializedName("issued_amount")
        private int initialAmount;
        @SerializedName("buy_rate")
        private int buyRate;
        @SerializedName("sell_rate")
        private int sellRate;

        public org.stellar.sdk.Asset getAsset() {
            return org.stellar.sdk.Asset.create(this.assetType, this.assetCode, this.assetIssuer);
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

        public String getAssetIssuer() {
            return assetIssuer;
        }

        public void setAssetIssuer(String assetIssuer) {
            this.assetIssuer = assetIssuer;
        }

        public String getPagingToken() {
            return pagingToken;
        }

        public void setPagingToken(String pagingToken) {
            this.pagingToken = pagingToken;
        }

        public String getAmount() {
            return amount;
        }

        public void setAmount(String amount) {
            this.amount = amount;
        }

        public int getNumAccounts() {
            return numAccounts;
        }

        public void setNumAccounts(int numAccounts) {
            this.numAccounts = numAccounts;
        }

        public AssetResponse.Flags getFlags() {
            return flags;
        }

        public void setFlags(AssetResponse.Flags flags) {
            this.flags = flags;
        }

        public AssetResponse.Links getLinks() {
            return links;
        }

        public void setLinks(AssetResponse.Links links) {
            this.links = links;
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public int getRemainAmount() {
            return remainAmount;
        }

        public void setRemainAmount(int remainAmount) {
            this.remainAmount = remainAmount;
        }

        public int getInitialAmount() {
            return initialAmount;
        }

        public void setInitialAmount(int initialAmount) {
            this.initialAmount = initialAmount;
        }

        public int getBuyRate() {
            return buyRate;
        }

        public void setBuyRate(int buyRate) {
            this.buyRate = buyRate;
        }

        public int getSellRate() {
            return sellRate;
        }

        public void setSellRate(int sellRate) {
            this.sellRate = sellRate;
        }
    }
}