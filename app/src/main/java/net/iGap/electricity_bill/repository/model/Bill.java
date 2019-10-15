package net.iGap.electricity_bill.repository.model;

public class Bill {

    private String ID;
    private String payID;
    private String price;
    private String dueTime;

    public Bill(String ID, String payID, String price, String dueTime) {
        this.ID = ID;
        this.payID = payID;
        this.price = price;
        this.dueTime = dueTime;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getPayID() {
        return payID;
    }

    public void setPayID(String payID) {
        this.payID = payID;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDueTime() {
        return dueTime;
    }

    public void setDueTime(String dueTime) {
        this.dueTime = dueTime;
    }
}
