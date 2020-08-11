package net.iGap.model.electricity_bill;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ElectricityResponse {

    @SerializedName("timeStamp")
    private String timeStamp;
    @SerializedName("status")
    private int status;
    @SerializedName("sessionKey")
    private String sessionKey;
    @SerializedName("error")
    private List<ElecError> error;
    @SerializedName("traceno")
    private String traceNum;
    @SerializedName("message")
    private String message;

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getSessionKey() {
        return sessionKey;
    }

    public void setSessionKey(String sessionKey) {
        this.sessionKey = sessionKey;
    }

    public List<ElecError> getError() {
        return error;
    }

    public void setError(List<ElecError> error) {
        this.error = error;
    }

    public String getTraceNum() {
        return traceNum;
    }

    public void setTraceNum(String traceNum) {
        this.traceNum = traceNum;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
