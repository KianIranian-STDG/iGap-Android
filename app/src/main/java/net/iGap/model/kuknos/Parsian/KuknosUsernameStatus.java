package net.iGap.model.kuknos.Parsian;

import com.google.gson.annotations.SerializedName;

public class KuknosUsernameStatus {

    @SerializedName("status")
    private boolean exist;

    public boolean isExist() {
        return exist;
    }

    public void setExist(boolean exist) {
        this.exist = exist;
    }
}
