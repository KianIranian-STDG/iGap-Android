package net.iGap.igasht;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ProvinceListResponse {

    @SerializedName("data")
    private List<IGashtProvince> provinces;

    public List<IGashtProvince> getProvinces() {
        return provinces;
    }
}
