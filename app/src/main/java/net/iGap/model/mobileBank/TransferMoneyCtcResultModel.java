package net.iGap.model.mobileBank;

public class TransferMoneyCtcResultModel {

    private String key;
    private String value;

    public TransferMoneyCtcResultModel(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public TransferMoneyCtcResultModel() {
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

}
