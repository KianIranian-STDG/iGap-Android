package net.iGap.kuknos.service.model.Parsian;

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
}
