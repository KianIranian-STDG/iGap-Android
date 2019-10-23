package net.iGap.electricity_bill.repository.model;

public class BillMessage {

    private String title;
    private String message;
    private boolean error;
    private int resID;

    public BillMessage() {
    }

    public BillMessage(boolean error, String title, String message, int resID) {
        this.error = error;
        this.title = title;
        this.message = message;
        this.resID = resID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean getState() {
        return error;
    }

    public int getResID() {
        return resID;
    }

    public void setResID(int resID) {
        this.resID = resID;
    }

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }
}
