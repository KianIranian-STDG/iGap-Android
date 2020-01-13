package net.iGap.mobileBank.repository.model;

public class BankHistoryModel {
    private String title;
    private String amount;
    private String date;

    public BankHistoryModel(String title, String amount, String date) {
        this.title = title;
        this.amount = amount;
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
