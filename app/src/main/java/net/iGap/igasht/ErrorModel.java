package net.iGap.igasht;

import com.google.gson.annotations.SerializedName;

public class ErrorModel {

    @SerializedName("name")
    private String name;
    @SerializedName("message")
    private String message;

    public ErrorModel(String name, String message) {
        this.name = name;
        this.message = message;
    }

    public String getName() {
        return name;
    }

    public String getMessage() {
        return message;
    }
}
