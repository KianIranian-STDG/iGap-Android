package net.iGap.model.igasht;

import com.google.gson.annotations.SerializedName;

@SuppressWarnings("unused")
public class ExtraDetail {

    @SerializedName("attentions")
    private String mAttentions;
    @SerializedName("closing_times")
    private String mClosingTimes;
    @SerializedName("coffee_shop")
    private Boolean mCoffeeShop;
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
    private Boolean mPrayerRoom;
    @SerializedName("resturant")
    private Boolean mResturant;
    @SerializedName("wc")
    private Boolean mWc;
    @SerializedName("weelchair_ramp")
    private Boolean mWeelchairRamp;
    @SerializedName("wifi")
    private Boolean mWifi;
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

    public Boolean getCoffeeShop() {
        return mCoffeeShop;
    }

    public void setCoffeeShop(Boolean coffeeShop) {
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

    public Boolean getPrayerRoom() {
        return mPrayerRoom;
    }

    public void setPrayerRoom(Boolean prayerRoom) {
        mPrayerRoom = prayerRoom;
    }

    public Boolean getResturant() {
        return mResturant;
    }

    public void setResturant(Boolean resturant) {
        mResturant = resturant;
    }

    public Boolean getWc() {
        return mWc;
    }

    public void setWc(Boolean wc) {
        mWc = wc;
    }

    public Boolean getWeelchairRamp() {
        return mWeelchairRamp;
    }

    public void setWeelchairRamp(Boolean weelchairRamp) {
        mWeelchairRamp = weelchairRamp;
    }

    public Boolean getmWifi() {
        return mWifi;
    }

    public void setWifi(Boolean wifi) {
        mWifi = wifi;
    }

    public String getWorkingTime() {
        return mWorkingTime;
    }

    public void setWorkingTime(String workingTime) {
        mWorkingTime = workingTime;
    }

}
