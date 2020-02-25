package net.iGap.model.kuknos.Parsian;

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
