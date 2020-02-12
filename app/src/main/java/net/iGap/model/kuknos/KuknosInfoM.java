package net.iGap.model.kuknos;

import com.google.gson.annotations.SerializedName;

public class KuknosInfoM {

    @SerializedName("phone_number")
    private String phoneNum;
    @SerializedName("national_id")
    private String nationalID;
    @SerializedName("friendly_id")
    private String username;
    @SerializedName("status")
    private String status;
    @SerializedName("created_at")
    private String creatTime;
    @SerializedName("updated_at")
    private String updateTime;

    public KuknosInfoM() {
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreatTime() {
        return creatTime;
    }

    public void setCreatTime(String creatTime) {
        this.creatTime = creatTime;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }
}
