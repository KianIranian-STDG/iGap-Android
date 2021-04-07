package net.iGap.model.electricity_bill;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ElectricityResponseList<G> extends ElectricityResponse{

    @SerializedName("data")
    private List<G> dataList;

    public List<G> getDataList() {
        return dataList;
    }

    public void setDataList(List<G> dataList) {
        this.dataList = dataList;
    }
}
