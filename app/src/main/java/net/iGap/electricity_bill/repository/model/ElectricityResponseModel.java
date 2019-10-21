package net.iGap.electricity_bill.repository.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ElectricityResponseModel<G> extends ElectricityResponse{

    @SerializedName("data")
    private G data;

    public G getData() {
        return data;
    }

    public void setData(G data) {
        this.data = data;
    }
}
