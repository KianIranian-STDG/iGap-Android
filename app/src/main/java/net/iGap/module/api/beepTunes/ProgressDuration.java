package net.iGap.module.api.beepTunes;

public class ProgressDuration {

    private int current;
    private int total;
    private long id;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getCurrent() {
        return current;
    }

    public void setCurrent(int current) {
        this.current = current;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public String getCurrentTime() {
        return getTimeString(current * 1000);
    }

    public String getTotalTime() {
        return getTimeString(total * 1000);
    }

    private String getTimeString(long millis) {

        int minutes = (int) ((millis % (1000 * 60 * 60)) / (1000 * 60));
        int seconds = (int) (((millis % (1000 * 60 * 60)) % (1000 * 60)) / 1000);
        String time = String.format("%02d", minutes) + ":" + String.format("%02d", seconds);

        return time;
    }
}