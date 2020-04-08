package net.iGap.model.kuknos.Parsian;

import com.google.gson.annotations.SerializedName;

public class KuknosOptionStatus {

    @SerializedName("status")
    private boolean status;

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}
