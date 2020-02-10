
package net.iGap.fragments.emoji.apiModels;

import com.google.gson.annotations.SerializedName;

public class CardDetailDataModel {

    @SerializedName("card_no")
    private String cardNo;
    @SerializedName("cvv2")
    private String cvv2;
    @SerializedName("expire_date")
    private String expireDate;
    @SerializedName("second_password")
    private String secondPassword;

    public String getCardNo() {
        return cardNo;
    }

    public void setCardNo(String cardNo) {
        this.cardNo = cardNo;
    }

    public String getCvv2() {
        return cvv2;
    }

    public void setCvv2(String cvv2) {
        this.cvv2 = cvv2;
    }

    public String getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(String expireDate) {
        this.expireDate = expireDate;
    }

    public String getSecondPassword() {
        return secondPassword;
    }

    public void setSecondPassword(String secondPassword) {
        this.secondPassword = secondPassword;
    }

}
