package net.iGap.kuknos.service.model;

public class KuknosSendM {

    private String RWalletID;
    private String text;
    private String amount;
    private String assetcode;

    public KuknosSendM() {
    }

    public KuknosSendM(String RWalletID, String text, String amount, String assetcode) {
        this.RWalletID = RWalletID;
        this.text = text;
        this.amount = amount;
        this.assetcode = assetcode;
    }

    public String getRWalletID() {
        return RWalletID;
    }

    public void setRWalletID(String RWalletID) {
        this.RWalletID = RWalletID;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getAssetcode() {
        return assetcode;
    }

    public void setAssetcode(String assetcode) {
        this.assetcode = assetcode;
    }
}
