
package net.iGap.model.discoveryWeather;

import com.google.gson.annotations.SerializedName;

public class Daily {

    @SerializedName("condition")
    private String mCondition;
    @SerializedName("date_time")
    private String mDateTime;
    @SerializedName("description")
    private String mDescription;
    @SerializedName("feels_like")
    private FeelsLike mFeelsLike;
    @SerializedName("humidity")
    private Long mHumidity;
    @SerializedName("icon")
    private String mIcon;
    @SerializedName("pressure")
    private Long mPressure;
    @SerializedName("sunrise_date_time")
    private String mSunriseDateTime;
    @SerializedName("sunset_date_time")
    private String mSunsetDateTime;
    @SerializedName("temperature")
    private Temperature mTemperature;
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

    public FeelsLike getFeelsLike() {
        return mFeelsLike;
    }

    public void setFeelsLike(FeelsLike feelsLike) {
        mFeelsLike = feelsLike;
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

    public Temperature getTemperature() {
        return mTemperature;
    }

    public void setTemperature(Temperature temperature) {
        mTemperature = temperature;
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
