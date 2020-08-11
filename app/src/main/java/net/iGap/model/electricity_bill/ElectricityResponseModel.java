package net.iGap.model.electricity_bill;

import com.google.gson.annotations.SerializedName;

public class ElectricityResponseModel<G> extends ElectricityResponse {

    @SerializedName("data")
    private G data;

    public G getData() {
        return data;
    }

    public void setData(G data) {
        this.data = data;
    }
}
