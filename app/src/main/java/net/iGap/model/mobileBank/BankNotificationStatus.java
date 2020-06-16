package net.iGap.model.mobileBank;

import com.google.gson.annotations.SerializedName;

public class BankNotificationStatus {

    @SerializedName("status")
    private boolean status;

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}
