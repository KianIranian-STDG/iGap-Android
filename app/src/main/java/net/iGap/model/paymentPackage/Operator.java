
package net.iGap.model.paymentPackage;

import com.google.gson.annotations.SerializedName;

@SuppressWarnings("unused")
public class Operator {

    @SerializedName("key")
    private String mKey;
    @SerializedName("logo")
    private String mLogo;
    @SerializedName("title")
    private String mTitle;

    public String getKey() {
        return mKey;
    }

    public void setKey(String key) {
        mKey = key;
    }

    public String getLogo() {
        return mLogo;
    }

    public void setLogo(String logo) {
        mLogo = logo;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

}
