package net.iGap.kuknos.service.model;

public class KuknosTradeHistoryM {

    private String sell;
    private String receive;
    private String amount;
    private String date;

    public KuknosTradeHistoryM() {
    }

    public KuknosTradeHistoryM(String sell, String receive, String amount, String date) {
        this.sell = sell;
        this.receive = receive;
        this.amount = amount;
        this.date = date;
    }

    public String getSell() {
        return sell;
    }

    public void setSell(String sell) {
        this.sell = sell;
    }

    public String getReceive() {
        return receive;
    }

    public void setReceive(String receive) {
        this.receive = receive;
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
