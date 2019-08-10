package net.iGap.kuknos.service.model;

import com.google.gson.annotations.SerializedName;

public class KuknosInfoM {

    @SerializedName("phone_number")
    private String phoneNum;
    @SerializedName("national_id")
    private String nationalID;
    @SerializedName("status")
    private String status;

    public KuknosInfoM() {
    }

    public KuknosInfoM(String phoneNum, String nationalID, String status) {
        this.phoneNum = phoneNum;
        this.nationalID = nationalID;
        this.status = status;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public String getNationalID() {
        return nationalID;
    }

    public void setNationalID(String nationalID) {
        this.nationalID = nationalID;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
