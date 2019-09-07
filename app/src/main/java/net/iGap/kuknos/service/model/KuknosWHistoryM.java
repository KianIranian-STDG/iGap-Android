package net.iGap.kuknos.service.model;

public class KuknosWHistoryM {

    private String date;
    private String amount;
    private String desc;

    public KuknosWHistoryM() {
    }

    public KuknosWHistoryM(String date, String amount, String desc) {
        this.date = date;
        this.amount = amount;
        this.desc = desc;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
