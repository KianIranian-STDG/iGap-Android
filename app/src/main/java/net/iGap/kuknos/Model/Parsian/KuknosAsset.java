package net.iGap.kuknos.Model.Parsian;

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
        private double remainAmount;
        @SerializedName("issued_amount")
        private double initialAmount;
        @SerializedName("buy_rate")
        private int buyRate;
        @SerializedName("sell_rate")
        private int sellRate;
        @SerializedName("transfer_limit")
        private double iGapTransferLimit;
        @SerializedName("trust_limit")
        private double trustLimit;
        @SerializedName("sale_limit_min")
        private double saleMin;
        @SerializedName("sale_limit_max")
        private double saleMax;
        @SerializedName("whitepaper")
        private String regulations;
        private boolean isTrusted = false;


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

        public double getRemainAmount() {
            return remainAmount;
        }

        public void setRemainAmount(int remainAmount) {
            this.remainAmount = remainAmount;
        }

        public double getInitialAmount() {
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

        public double getiGapTransferLimit() {
            return iGapTransferLimit;
        }

        public double getTrustLimit() {
            return trustLimit;
        }

        public double getSaleMin() {
            return saleMin;
        }

        public double getSaleMax() {
            return saleMax;
        }

        public String getRegulations() {
            return regulations;
        }

        public boolean isTrusted() {
            return isTrusted;
        }

        public void setTrusted(boolean trusted) {
            isTrusted = trusted;
        }
    }
}
