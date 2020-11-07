package net.iGap.kuknos.Model;

public class KuknosSendM {

    private String src;
    private String dest;
    private String memo;
    private String amount;
    private String assetCode;
    private String assetInssuer;

    public KuknosSendM() {
    }

    public String getSrc() {
        return src;
    }

    public void setSrc(String src) {
        this.src = src;
    }

    public String getDest() {
        return dest;
    }

    public void setDest(String dest) {
        this.dest = dest;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getAssetCode() {
        return assetCode;
    }

    public void setAssetCode(String assetCode) {
        this.assetCode = assetCode;
    }

    public String getAssetInssuer() {
        return assetInssuer;
    }

    public void setAssetInssuer(String assetInssuer) {
        this.assetInssuer = assetInssuer;
    }
}
