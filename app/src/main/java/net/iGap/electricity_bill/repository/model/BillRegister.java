package net.iGap.electricity_bill.repository.model;

public class BillRegister {

    private String ID;
    private String mobileNum;
    private String NID;
    private String email;
    private String title;
    private boolean isSMSEnable;
    private boolean isAppEnable;
    private boolean isEmailEnable;
    private boolean isPrintEnable;

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getMobileNum() {
        return mobileNum;
    }

    public void setMobileNum(String mobileNum) {
        this.mobileNum = mobileNum;
    }

    public String getNID() {
        return NID;
    }

    public void setNID(String NID) {
        this.NID = NID;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isSMSEnable() {
        return isSMSEnable;
    }

    public void setSMSEnable(boolean SMSEnable) {
        isSMSEnable = SMSEnable;
    }

    public boolean isAppEnable() {
        return isAppEnable;
    }

    public void setAppEnable(boolean appEnable) {
        isAppEnable = appEnable;
    }

    public boolean isEmailEnable() {
        return isEmailEnable;
    }

    public void setEmailEnable(boolean emailEnable) {
        isEmailEnable = emailEnable;
    }

    public boolean isPrintEnable() {
        return isPrintEnable;
    }

    public void setPrintEnable(boolean printEnable) {
        isPrintEnable = printEnable;
    }
}
