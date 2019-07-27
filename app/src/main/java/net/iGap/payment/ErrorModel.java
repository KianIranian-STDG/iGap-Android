package net.iGap.payment;

import com.google.gson.annotations.SerializedName;

public class ErrorModel {

    @SerializedName("name")
    private String name;
    @SerializedName("message")
    private String message;

    public String getName() {
        return name;
    }

    public String getMessage() {
        return message;
    }
}
