package net.iGap.model.mobileBank;

import com.google.gson.annotations.SerializedName;

public class BaseMobileBankResponse<T> {
    @SerializedName("message")
    private String message;
    @SerializedName("data")
    private T data;

    public String getMessage() {
        return message;
    }

    public T getData() {
        return data;
    }
}
