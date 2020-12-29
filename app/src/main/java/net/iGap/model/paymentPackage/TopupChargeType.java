
package net.iGap.model.paymentPackage;

import com.google.gson.annotations.SerializedName;

public class TopupChargeType {

    @SerializedName("key")
    private String mKey;
    @SerializedName("title")
    private String mTitle;

    public String getKey() {
        return mKey;
    }

    public void setKey(String key) {
        mKey = key;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

}
