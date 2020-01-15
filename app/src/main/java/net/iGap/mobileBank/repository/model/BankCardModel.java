package net.iGap.mobileBank.repository.model;

public class BankCardModel {

    private String cardBankName;
    private String cardNum;

    public BankCardModel(String cardBankName, String cardNum) {
        this.cardBankName = cardBankName;
        this.cardNum = cardNum;
    }

    public String getCardBankName() {
        return cardBankName;
    }

    public void setCardBankName(String cardBankName) {
        this.cardBankName = cardBankName;
    }

    public String getCardNum() {
        return cardNum;
    }

    public void setCardNum(String cardNum) {
        this.cardNum = cardNum;
    }
}
