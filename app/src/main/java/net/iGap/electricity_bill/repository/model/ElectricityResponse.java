package net.iGap.electricity_bill.repository.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ElectricityResponse<G> {

    @SerializedName("timeStamp")
    private String timeStamp;
    @SerializedName("status")
    private int status;
    @SerializedName("sessionKey")
    private String sessionKey;
    @SerializedName("data")
    private List<G> data;
    @SerializedName("error")
    private ElecError error;
    @SerializedName("traceno")
    private String traceNum;
    @SerializedName("message")
    private String message;

}
