
package net.iGap.module.api.PopularChannel;

import com.google.gson.annotations.SerializedName;

import java.util.List;


public class ParentChannel {

    @SerializedName("data")
    private List<Datum> mData;

    public List<Datum> getData() {
        return mData;
    }

    public void setData(List<Datum> data) {
        mData = data;
    }

}
