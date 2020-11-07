package net.iGap.kuknos.Model.Parsian;

import com.google.gson.annotations.SerializedName;

import org.stellar.sdk.Asset;
import org.stellar.sdk.AssetTypeNative;

public class KuknosPaymentOpResponse extends KuknosOperationResponse.OperationResponse {

    @SerializedName("amount")
    protected String amount;
    @SerializedName("asset_type")
    protected String assetType;
    @SerializedName("asset_code")
    protected String assetCode;
    @SerializedName("asset_issuer")
    protected String assetIssuer;
    @SerializedName("from")
    protected String from;
    @SerializedName("to")
    protected String to;
    @SerializedName("starting_balance")
    protected String startingBalance;

    public String getAmount() {
        return amount;
    }

    public Asset getAsset() {
        if (assetType.equals("native")) {
            return new AssetTypeNative();
        } else {
            return Asset.createNonNativeAsset(assetCode, assetIssuer);
        }
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public String getAssetType() {
        return assetType;
    }

    public String getAssetCode() {
        return assetCode;
    }

    public String getAssetIssuer() {
        return assetIssuer;
    }

    public String getStartingBalance() {
        return startingBalance;
    }
}
