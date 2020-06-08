
package net.iGap.model.discoveryWeather;

import com.google.gson.annotations.SerializedName;

public class FeelsLike {

    @SerializedName("day")
    private Long mDay;
    @SerializedName("eve")
    private Long mEve;
    @SerializedName("morn")
    private Long mMorn;
    @SerializedName("night")
    private Long mNight;

    public Long getDay() {
        return mDay;
    }

    public void setDay(Long day) {
        mDay = day;
    }

    public Long getEve() {
        return mEve;
    }

    public void setEve(Long eve) {
        mEve = eve;
    }

    public Long getMorn() {
        return mMorn;
    }

    public void setMorn(Long morn) {
        mMorn = morn;
    }

    public Long getNight() {
        return mNight;
    }

    public void setNight(Long night) {
        mNight = night;
    }

}
