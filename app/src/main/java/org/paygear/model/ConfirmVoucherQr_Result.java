package org.paygear.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ConfirmVoucherQr_Result implements Serializable {
    @SerializedName("message")
    public String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
