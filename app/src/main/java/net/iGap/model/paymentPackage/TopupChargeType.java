
package net.iGap.model.paymentPackage;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TopupChargeType {
    @SerializedName("key")
    @Expose
    private String key;
    @SerializedName("title")
    @Expose
    private String title;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}