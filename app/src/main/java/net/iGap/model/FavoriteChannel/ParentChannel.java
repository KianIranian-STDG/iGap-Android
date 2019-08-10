
package net.iGap.model.FavoriteChannel;

import com.google.gson.annotations.SerializedName;

import java.util.List;


public class ParentChannel {
    public static final String TYPE_SLIDE = "advertisement";
    public static final String TYPE_CHANNEL = "channelFeaturedCategory";
    public static final String TYPE_CATEGORY = "channelNormalCategory";


    @SerializedName("data")
    private List<Datum> mData;

    public List<Datum> getData() {
        return mData;
    }

    public void setData(List<Datum> data) {
        mData = data;
    }

}
