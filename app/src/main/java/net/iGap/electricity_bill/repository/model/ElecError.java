package net.iGap.electricity_bill.repository.model;

import com.google.gson.annotations.SerializedName;

public class ElecError {
    @SerializedName("ErrorCode")
    private int errorCode;
    @SerializedName("ErrorMsg")
    private int errorMsg;
}
