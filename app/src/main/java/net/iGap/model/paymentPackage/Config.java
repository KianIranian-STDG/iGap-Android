
package net.iGap.model.paymentPackage;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Config {

    @SerializedName("data")
    @Expose
    private List<ConfigData> mData = null;

    public List<ConfigData> getData() {
        return mData;
    }

    public void setData(List<ConfigData> data) {
        mData = data;
    }

}
