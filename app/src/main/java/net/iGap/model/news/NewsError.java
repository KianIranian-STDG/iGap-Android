package net.iGap.model.news;

public class NewsError {

    private String title;
    private String message;
    private boolean state;
    private int resID;

    public NewsError(boolean state, String title, String message, int resID) {
        this.state = state;
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
        return state;
    }

    public boolean isState() {
        return state;
    }

    public void setState(boolean state) {
        this.state = state;
    }

    public int getResID() {
        return resID;
    }

    public void setResID(int resID) {
        this.resID = resID;
    }

}
