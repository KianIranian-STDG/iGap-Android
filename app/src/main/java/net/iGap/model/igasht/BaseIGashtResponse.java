package net.iGap.model.igasht;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class BaseIGashtResponse<T> {
    @SerializedName("data")
    protected List<T> data;

    public List<T> getData() {
        return data;
    }
}
