
package net.iGap.fragments.emoji.apiModels;

import com.google.gson.annotations.Expose;

public class Activation {

    @Expose
    private String status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
