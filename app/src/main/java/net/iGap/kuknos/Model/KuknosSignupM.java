package net.iGap.kuknos.Model;

public class KuknosSignupM {

    private String username;
    private String name;
    private String phoneNum;
    private String email;
    private String NID;
    private String pinCode;
    private String keyString;
    private boolean isRegistered = false;

    public KuknosSignupM() {
    }

    public KuknosSignupM(boolean isRegistered) {
        this.isRegistered = isRegistered;
    }

    public KuknosSignupM(String name, String phoneNum, String email, String NID, String keyString, String username, boolean isRegistered) {
        this.name = name;
        this.phoneNum = phoneNum;
        this.email = email;
        this.NID = NID;
        this.isRegistered = isRegistered;
        this.keyString = keyString;
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPinCode() {
        return pinCode;
    }

    public void setPinCode(String pinCode) {
        this.pinCode = pinCode;
    }

    public String getKeyString() {
        return keyString;
    }

    public void setKeyString(String keyString) {
        this.keyString = keyString;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public String getNID() {
        return NID;
    }

    public void setNID(String NID) {
        this.NID = NID;
    }

    public boolean isRegistered() {
        return isRegistered;
    }

    public void setRegistered(boolean registered) {
        isRegistered = registered;
    }
}
