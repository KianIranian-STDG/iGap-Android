
package net.iGap.fragments.emoji.apiModels;

import com.google.gson.annotations.SerializedName;

public class Activation {

    @SerializedName("status")
    private String status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
