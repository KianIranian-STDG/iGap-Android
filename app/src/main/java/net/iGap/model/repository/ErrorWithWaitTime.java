package net.iGap.model.repository;

public class ErrorWithWaitTime {

    private int majorCode;
    private int minorCode;
    private int waitTime;

    public ErrorWithWaitTime(int majorCode, int minorCode, int waitTime) {
        this.majorCode = majorCode;
        this.minorCode = minorCode;
        this.waitTime = waitTime;
    }

    public int getMajorCode() {
        return majorCode;
    }

    public int getMinorCode() {
        return minorCode;
    }

    public int getWaitTime() {
        return waitTime;
    }
}
