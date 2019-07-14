package net.iGap.kuknos.service.model;

public class KuknosLoginM {

    private String userPhoneNum;
    private String userID;

    public KuknosLoginM(String userPhoneNum, String userID) {
        this.userPhoneNum = userPhoneNum;
        this.userID = userID;
    }

    public String getUserPhoneNum() {
        return userPhoneNum;
    }

    public void setUserPhoneNum(String userPhoneNum) {
        this.userPhoneNum = userPhoneNum;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public boolean isUserIDValid() {
        if (userID.length() != 10)
            return false;
        else
            return true;
    }
}
