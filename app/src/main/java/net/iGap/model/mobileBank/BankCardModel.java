package net.iGap.model.mobileBank;

import com.google.gson.annotations.SerializedName;

public class BankCardModel {

    @SerializedName("card_status")
    private String cardStatus;
    @SerializedName("card_status_cause")
    private String cardStatusCause;
    @SerializedName("card_type")
    private String cardType;
    @SerializedName("customer_first_name")
    private String customerFirstName;
    @SerializedName("customer_last_name")
    private String customerLastName;
    @SerializedName("expire_date")
    private String expireDate;
    @SerializedName("issue_date")
    private String issueDate;
    @SerializedName("pan")
    private String pan;

    transient private String cardBankName;

    public BankCardModel(String cardBankName, String pan) {
        this.pan = pan;
        this.cardBankName = cardBankName;
    }

    public String getCardStatus() {
        return cardStatus;
    }

    public String getCardStatusCause() {
        return cardStatusCause;
    }

    public String getCardType() {
        return cardType;
    }

    public String getCustomerFirstName() {
        return customerFirstName;
    }

    public String getCustomerLastName() {
        return customerLastName;
    }

    public String getExpireDate() {
        return expireDate;
    }

    public String getIssueDate() {
        return issueDate;
    }

    public String getPan() {
        return pan;
    }

    public String getCardBankName() {
        return cardBankName;
    }

    public void setCardBankName(String cardBankName) {
        this.cardBankName = cardBankName;
    }
}
