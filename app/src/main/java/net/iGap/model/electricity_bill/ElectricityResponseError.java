package net.iGap.model.electricity_bill;

import com.google.gson.annotations.SerializedName;

public class ElectricityResponseError {

    @SerializedName("name")
    private String name;
    @SerializedName("message")
    private int message;
    @SerializedName("details")
    private ElectricityResponse details;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getMessage() {
        return message;
    }

    public void setMessage(int message) {
        this.message = message;
    }

    public ElectricityResponse getDetails() {
        return details;
    }

    public void setDetails(ElectricityResponse details) {
        this.details = details;
    }
}
