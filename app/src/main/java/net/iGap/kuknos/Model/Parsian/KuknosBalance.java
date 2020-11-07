package net.iGap.kuknos.Model.Parsian;

import com.google.gson.annotations.SerializedName;

import org.stellar.sdk.Asset;
import org.stellar.sdk.AssetTypeNative;

import java.util.List;

public class KuknosBalance {

    @SerializedName("assets")
    private List<Balance> assets;

    public List<Balance> getAssets() {
        return assets;
    }

    public void setAssets(List<Balance> assets) {
        this.assets = assets;
    }

    public static class Balance {
        @SerializedName("asset_type")
        private String assetType;
        @SerializedName("asset_code")
        private String assetCode;
        @SerializedName("asset_issuer")
        private String assetIssuer;
        @SerializedName("limit")
        private String limit;
        @SerializedName("balance")
        private String balance;
        @SerializedName("buying_liabilities")
        private String buyingLiabilities;
        @SerializedName("selling_liabilities")
        private String sellingLiabilities;
        @SerializedName("is_authorized")
        private Boolean isAuthorized;
        @SerializedName("last_modified_ledger")
        private Integer lastModifiedLedger;

        public Asset getAsset() {
            if (assetType.equals("native")) {
                return new AssetTypeNative();
            } else {
                return Asset.createNonNativeAsset(assetCode, getAssetIssuer());
            }
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

        public String getLimit() {
            return limit;
        }

        public void setLimit(String limit) {
            this.limit = limit;
        }

        public String getBalance() {
            return balance;
        }

        public void setBalance(String balance) {
            this.balance = balance;
        }

        public String getBuyingLiabilities() {
            return buyingLiabilities;
        }

        public void setBuyingLiabilities(String buyingLiabilities) {
            this.buyingLiabilities = buyingLiabilities;
        }

        public String getSellingLiabilities() {
            return sellingLiabilities;
        }

        public void setSellingLiabilities(String sellingLiabilities) {
            this.sellingLiabilities = sellingLiabilities;
        }

        public Boolean getAuthorized() {
            return isAuthorized;
        }

        public void setAuthorized(Boolean authorized) {
            isAuthorized = authorized;
        }

        public Integer getLastModifiedLedger() {
            return lastModifiedLedger;
        }

        public void setLastModifiedLedger(Integer lastModifiedLedger) {
            this.lastModifiedLedger = lastModifiedLedger;
        }
    }
}
