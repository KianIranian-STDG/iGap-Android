package net.iGap.model.electricity_bill;

@Deprecated
public class Bill {

    private String ID;
    private String title;
    private String payID;
    private String price;
    private String dueTime;
    private boolean loading;

    public Bill() {
    }

    public Bill(String ID, String payID, String price, String dueTime) {
        this.ID = ID;
        this.payID = payID;
        this.price = price;
        this.dueTime = dueTime;
    }

    public Bill(String name, String ID, String payID, String price, String dueTime) {
        this.ID = ID;
        this.payID = payID;
        this.price = price;
        this.dueTime = dueTime;
        this.title = name;
    }

    public Bill(String ID, String payID, String price, String dueTime, boolean loading) {
        this.ID = ID;
        this.payID = payID;
        this.price = price;
        this.dueTime = dueTime;
        this.loading = loading;
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isLoading() {
        return loading;
    }

    public void setLoading(boolean loading) {
        this.loading = loading;
    }
}
