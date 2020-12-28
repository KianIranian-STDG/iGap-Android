
package net.iGap.model.discoveryWeather;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class WeatherInfo {

    @SerializedName("condition")
    private String mCondition;
    @SerializedName("daily")
    private List<Daily> mDaily;
    @SerializedName("date_time")
    private String mDateTime;
    @SerializedName("description")
    private String mDescription;
    @SerializedName("feels_like")
    private Long mFeelsLike;
    @SerializedName("hourly")
    private List<Hourly> mHourly;
    @SerializedName("humidity")
    private Long mHumidity;
    @SerializedName("icon")
    private String mIcon;
    @SerializedName("lat")
    private String mLat;
    @SerializedName("lon")
    private String mLon;
    @SerializedName("pressure")
    private Long mPressure;
    @SerializedName("sunrise_date_time")
    private String mSunriseDateTime;
    @SerializedName("sunset_date_time")
    private String mSunsetDateTime;
    @SerializedName("temperature")
    private Long mTemperature;
    @SerializedName("title")
    private String mTitle;
    @SerializedName("uv_index")
    private Double mUvIndex;
    @SerializedName("wind_deg")
    private Long mWindDeg;
    @SerializedName("wind_speed")
    private Double mWindSpeed;

    public String getCondition() {
        return mCondition;
    }

    public void setCondition(String condition) {
        mCondition = condition;
    }

    public List<Daily> getDaily() {
        return mDaily;
    }

    public void setDaily(List<Daily> daily) {
        mDaily = daily;
    }

    public String getDateTime() {
        return mDateTime;
    }

    public void setDateTime(String dateTime) {
        mDateTime = dateTime;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        mDescription = description;
    }

    public Long getFeelsLike() {
        return mFeelsLike;
    }

    public void setFeelsLike(Long feelsLike) {
        mFeelsLike = feelsLike;
    }

    public List<Hourly> getHourly() {
        return mHourly;
    }

    public void setHourly(List<Hourly> hourly) {
        mHourly = hourly;
    }

    public Long getHumidity() {
        return mHumidity;
    }

    public void setHumidity(Long humidity) {
        mHumidity = humidity;
    }

    public String getIcon() {
        return mIcon;
    }

    public void setIcon(String icon) {
        mIcon = icon;
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

    public Long getPressure() {
        return mPressure;
    }

    public void setPressure(Long pressure) {
        mPressure = pressure;
    }

    public String getSunriseDateTime() {
        return mSunriseDateTime;
    }

    public void setSunriseDateTime(String sunriseDateTime) {
        mSunriseDateTime = sunriseDateTime;
    }

    public String getSunsetDateTime() {
        return mSunsetDateTime;
    }

    public void setSunsetDateTime(String sunsetDateTime) {
        mSunsetDateTime = sunsetDateTime;
    }

    public Long getTemperature() {
        return mTemperature;
    }

    public void setTemperature(Long temperature) {
        mTemperature = temperature;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public Double getUvIndex() {
        return mUvIndex;
    }

    public void setUvIndex(Double uvIndex) {
        mUvIndex = uvIndex;
    }

    public Long getWindDeg() {
        return mWindDeg;
    }

    public void setWindDeg(Long windDeg) {
        mWindDeg = windDeg;
    }

    public Double getWindSpeed() {
        return mWindSpeed;
    }

    public void setWindSpeed(Double windSpeed) {
        mWindSpeed = windSpeed;
    }

}
