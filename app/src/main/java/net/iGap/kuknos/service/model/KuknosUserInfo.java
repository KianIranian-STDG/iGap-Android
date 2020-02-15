package net.iGap.kuknos.service.model;

import com.google.gson.annotations.SerializedName;

public class KuknosUserInfo {
    @SerializedName("status")
    private String status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
