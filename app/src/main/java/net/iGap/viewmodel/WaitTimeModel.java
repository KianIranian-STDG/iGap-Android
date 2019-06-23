package net.iGap.viewmodel;

public class WaitTimeModel {
    private int title;
    private long time;
    private int majorCod;

    public WaitTimeModel(int title, long time, int majorCod) {
        this.title = title;
        this.time = time;
        this.majorCod = majorCod;
    }

    public int getTitle() {
        return title;
    }

    public long getTime() {
        return time;
    }

    public int getMajorCod() {
        return majorCod;
    }
}
