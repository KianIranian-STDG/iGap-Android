
package net.iGap.model.discoveryWeather;

import com.google.gson.annotations.SerializedName;

public class Cities {

    @SerializedName("id")
    private String mId;
    @SerializedName("lat")
    private String mLat;
    @SerializedName("lon")
    private String mLon;
    @SerializedName("title")
    private String mTitle;

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public String getLat() {
        return mLat;
    }

    public void setLat(String lat) {
        mLat = lat;
    }

    public String getLon() {
        return mLon;
    }

    public void setLon(String lon) {
        mLon = lon;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

}
