package net.iGap.model.mobileBank;

public class TransferMoneyCtcModel {

    private String origin;
    private String destination;
    private String bankName;
    private String destName;
    private String expireDate;

    public TransferMoneyCtcModel(String origin, String destination, String bankName, String destName, String expireDate) {
        this.origin = origin;
        this.destination = destination;
        this.bankName = bankName;
        this.destName = destName;
        this.expireDate = expireDate;
    }

    public TransferMoneyCtcModel() {

    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getDestName() {
        return destName;
    }

    public void setDestName(String destName) {
        this.destName = destName;
    }

    public String getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(String expireDate) {
        this.expireDate = expireDate;
    }
}
