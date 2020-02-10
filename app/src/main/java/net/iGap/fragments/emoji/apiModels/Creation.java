
package net.iGap.fragments.emoji.apiModels;

import com.google.gson.annotations.SerializedName;

public class Creation {
    @SerializedName("mobileNumber")
    private String mobileNumber;
    @SerializedName("nationalCode")
    private String nationalCode;
    @SerializedName("status")
    private String status;
    @SerializedName("userId")
    private String userId;

    public String getNationalCode() {
        return nationalCode;
    }

    public void setNationalCode(String nationalCode) {
        this.nationalCode = nationalCode;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

}
