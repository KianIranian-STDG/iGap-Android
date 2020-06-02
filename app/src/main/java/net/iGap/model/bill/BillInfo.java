package net.iGap.model.bill;

public class BillInfo {

    public enum BillType {ELECTRICITY, GAS, PHONE, MOBILE}

    private BillType billType;
    private String billTypeString;
    private String serverID;
    private String billID;
    private String gasID;
    private String mobileNum;
    private String title;
    private String phoneNum;
    private String areaCode;


    public String getBillTypeString() {
        if (billTypeString != null)
            return billTypeString;
        switch (billType) {
            case ELECTRICITY:
                return "ELECTRICITY";
            case GAS:
                return "GAS";
            case PHONE:
                return "PHONE";
            case MOBILE:
                return "MOBILE_MCI";
            default:
                return "";
        }
    }

    public void setBillTypeString(String billTypeString) {
        this.billTypeString = billTypeString;
    }

    public String getBillID() {
        return billID;
    }

    public void setBillID(String billID) {
        this.billID = billID;
    }

    public String getMobileNum() {
        return mobileNum;
    }

    public void setMobileNum(String mobileNum) {
        this.mobileNum = mobileNum;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public String getAreaCode() {
        return areaCode;
    }

    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }

    public String getGasID() {
        return gasID;
    }

    public void setGasID(String gasID) {
        this.gasID = gasID;
    }

    public String getServerID() {
        return serverID;
    }

    public void setServerID(String serverID) {
        this.serverID = serverID;
    }

    public BillType getBillType() {
        return billType;
    }

    public void setBillType(BillType billType) {
        this.billType = billType;
    }
}
