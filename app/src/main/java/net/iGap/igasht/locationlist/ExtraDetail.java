
package net.iGap.igasht.locationlist;

import com.google.gson.annotations.SerializedName;

@SuppressWarnings("unused")
public class ExtraDetail {

    @SerializedName("attentions")
    private String mAttentions;
    @SerializedName("closing_times")
    private String mClosingTimes;
    @SerializedName("coffee_shop")
    private String mCoffeeShop;
    @SerializedName("content")
    private String mContent;
    @SerializedName("lat")
    private String mLat;
    @SerializedName("location_name")
    private String mLocationName;
    @SerializedName("long")
    private String mLong;
    @SerializedName("main_picture")
    private String mMainPicture;
    @SerializedName("parking")
    private Boolean mParking;
    @SerializedName("prayer_room")
    private String mPrayerRoom;
    @SerializedName("resturant")
    private String mResturant;
    @SerializedName("wc")
    private String mWc;
    @SerializedName("weelchair_ramp")
    private String mWeelchairRamp;
    @SerializedName("wifi")
    private String mWifi;
    @SerializedName("working_time")
    private String mWorkingTime;

    public String getAttentions() {
        return mAttentions;
    }

    public void setAttentions(String attentions) {
        mAttentions = attentions;
    }

    public String getClosingTimes() {
        return mClosingTimes;
    }

    public void setClosingTimes(String closingTimes) {
        mClosingTimes = closingTimes;
    }

    public String getCoffeeShop() {
        return mCoffeeShop;
    }

    public void setCoffeeShop(String coffeeShop) {
        mCoffeeShop = coffeeShop;
    }

    public String getContent() {
        return mContent;
    }

    public void setContent(String content) {
        mContent = content;
    }

    public String getLat() {
        return mLat;
    }

    public void setLat(String lat) {
        mLat = lat;
    }

    public String getLocationName() {
        return mLocationName;
    }

    public void setLocationName(String locationName) {
        mLocationName = locationName;
    }

    public void setmLong(String mLong) {
        this.mLong = mLong;
    }

    public String getmLong() {
        return mLong;
    }

        public String getMainPicture() {
        return mMainPicture;
    }

    public void setMainPicture(String mainPicture) {
        mMainPicture = mainPicture;
    }

    public Boolean getParking() {
        return mParking;
    }

    public void setParking(Boolean parking) {
        mParking = parking;
    }

    public String getPrayerRoom() {
        return mPrayerRoom;
    }

    public void setPrayerRoom(String prayerRoom) {
        mPrayerRoom = prayerRoom;
    }

    public String getResturant() {
        return mResturant;
    }

    public void setResturant(String resturant) {
        mResturant = resturant;
    }

    public String getWc() {
        return mWc;
    }

    public void setWc(String wc) {
        mWc = wc;
    }

    public String getWeelchairRamp() {
        return mWeelchairRamp;
    }

    public void setWeelchairRamp(String weelchairRamp) {
        mWeelchairRamp = weelchairRamp;
    }

    public boolean getWifi() {
        return mWifi.equals("true");
    }

    public void setWifi(String wifi) {
        mWifi = wifi;
    }

    public String getWorkingTime() {
        return mWorkingTime;
    }

    public void setWorkingTime(String workingTime) {
        mWorkingTime = workingTime;
    }

}
