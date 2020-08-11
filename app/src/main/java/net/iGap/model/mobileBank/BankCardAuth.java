package net.iGap.model.mobileBank;

import com.google.gson.annotations.SerializedName;

public class BankCardAuth {

    @SerializedName("cvv2")
    private String cvv;
    @SerializedName("exp_date")
    private String expireDate;
    @SerializedName("pin")
    private String password;
    @SerializedName("pin_type")
    private String pinType;
    @SerializedName("track2")
    private String trankNumber;

    public BankCardAuth(String cvv, String expireDate, String password, String pinType, String trankNumber) {
        this.cvv = cvv;
        this.expireDate = expireDate;
        this.password = password;
        this.pinType = pinType;
        this.trankNumber = trankNumber;
    }

    public String getCvv() {
        return cvv;
    }

    public void setCvv(String cvv) {
        this.cvv = cvv;
    }

    public String getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(String expireDate) {
        this.expireDate = expireDate;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPinType() {
        return pinType;
    }

    public void setPinType(String pinType) {
        this.pinType = pinType;
    }

    public String getTrankNumber() {
        return trankNumber;
    }

    public void setTrankNumber(String trankNumber) {
        this.trankNumber = trankNumber;
    }
}
