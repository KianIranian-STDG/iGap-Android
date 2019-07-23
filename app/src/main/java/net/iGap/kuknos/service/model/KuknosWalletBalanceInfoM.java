package net.iGap.kuknos.service.model;

public class KuknosWalletBalanceInfoM {

    private String balance;
    private String assetType;
    private String assetCode;
    private String assetPicURL;

    public KuknosWalletBalanceInfoM() {
    }

    public KuknosWalletBalanceInfoM(String balance, String assetType, String assetCode, String assetPicURL) {
        this.balance = balance;
        this.assetType = assetType;
        this.assetCode = assetCode;
        this.assetPicURL = assetPicURL;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
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

    public String getAssetPicURL() {
        return assetPicURL;
    }

    public void setAssetPicURL(String assetPicURL) {
        this.assetPicURL = assetPicURL;
    }
}
