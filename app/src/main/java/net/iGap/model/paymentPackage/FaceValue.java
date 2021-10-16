
package net.iGap.model.paymentPackage;

import com.google.gson.annotations.SerializedName;

@SuppressWarnings("unused")
public class FaceValue {

    @SerializedName("key")
    private Long mKey;
    @SerializedName("selected")
    private Boolean mSelected;
    @SerializedName("title")
    private String mTitle;

    public Long getKey() {
        return mKey;
    }

    public void setKey(Long key) {
        mKey = key;
    }

    public Boolean getSelected() {
        return mSelected;
    }

    public void setSelected(Boolean selected) {
        mSelected = selected;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

}
