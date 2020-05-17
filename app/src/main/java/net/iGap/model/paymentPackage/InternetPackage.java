package net.iGap.model.paymentPackage;

import com.google.gson.annotations.SerializedName;

public class InternetPackage {

    @SerializedName("type")
    private int type;
    @SerializedName("cost")
    private int cost;
    @SerializedName("description")
    private String description;
    @SerializedName("traffic")
    private String trafficId;
    @SerializedName("duration")
    private String durationId;

    public String getTrafficId() {
        return trafficId;
    }

    public String getDurationId() {
        return durationId;
    }

    public int getType() {
        return type;
    }

    public int getCost() {
        return cost;
    }

    public String getDescription() {
        return description;
    }
}
