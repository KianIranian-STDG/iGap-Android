package net.iGap.model.electricity_bill;

public class BillInfo {

    private String billType;
    private String serverID;
    private String billID;
    private String gasID;
    private String mobileNum;
    private String title;
    private String phoneNum;
    private String areaCode;

    public String getBillType() {
        return billType;
        /*switch (billType) {
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
        }*/
    }

    public void setBillType(String billType) {
        this.billType = billType;
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
}
