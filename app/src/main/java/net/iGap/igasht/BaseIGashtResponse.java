package net.iGap.igasht;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class BaseIGashtResponse<T> {
    @SerializedName("data")
    private List<T> data;

    public List<T> getData() {
        return data;
    }
}