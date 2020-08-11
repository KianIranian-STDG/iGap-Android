package net.iGap.model.electricity_bill;

import com.google.gson.annotations.SerializedName;

public class ElecError {
    @SerializedName("ErrorCode")
    private int errorCode;
    @SerializedName("ErrorMsg")
    private int errorMsg;

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public int getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(int errorMsg) {
        this.errorMsg = errorMsg;
    }
}
