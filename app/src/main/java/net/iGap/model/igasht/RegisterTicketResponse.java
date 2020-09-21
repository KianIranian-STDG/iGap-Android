package net.iGap.model.igasht;

import com.google.gson.annotations.SerializedName;

public class RegisterTicketResponse {

    @SerializedName("details")
    private Details mDetails;
    @SerializedName("message")
    private String mMessage;
    @SerializedName("name")
    private String mName;

    public Details getDetails() {
        return mDetails;
    }

    public void setDetails(Details details) {
        mDetails = details;
    }

    public String getMessage() {
        return mMessage;
    }

    public void setMessage(String message) {
        mMessage = message;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }
}
