
package net.iGap.model.paymentPackage;

import java.util.List;
import com.google.gson.annotations.SerializedName;

@SuppressWarnings("unused")
public class Config {

    @SerializedName("data")
    private List<ConfigData> mData;

    public List<ConfigData> getData() {
        return mData;
    }

    public void setData(List<ConfigData> data) {
        mData = data;
    }

}
