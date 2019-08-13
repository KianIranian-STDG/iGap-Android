package net.iGap.internetpackage;

import com.google.gson.annotations.SerializedName;

public class InternetPackage {

    @SerializedName("type")
    private String type;
    @SerializedName("cost")
    private int cost;
    @SerializedName("systems")
    private String systems;
    @SerializedName("description")
    private String description;

    public String getType() {
        return type;
    }

    public int getCost() {
        return cost;
    }

    public String getSystems() {
        return systems;
    }

    public String getDescription() {
        return description;
    }
}
