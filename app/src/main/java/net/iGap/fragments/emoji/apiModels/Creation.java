
package net.iGap.fragments.emoji.apiModels;

import com.google.gson.annotations.Expose;

public class Creation {
    @Expose
    private String mobileNumber;
    @Expose
    private String nationalCode;
    @Expose
    private String status;
    @Expose
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
