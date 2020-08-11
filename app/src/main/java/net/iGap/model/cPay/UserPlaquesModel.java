package net.iGap.model.cPay;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class UserPlaquesModel {

    @SerializedName("data")
    private List<String> data;

    public void setData(List<String> data) {
        this.data = data;
    }

    public List<String> getData() {
        return data;
    }
}
