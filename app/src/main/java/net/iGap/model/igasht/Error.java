
package net.iGap.model.igasht;

import com.google.gson.annotations.SerializedName;


public class Error {

    @SerializedName("Message")
    private String mMessage;
    @SerializedName("MessageCode")
    private Long mMessageCode;
    @SerializedName("PersianMessage")
    private String mPersianMessage;
    @SerializedName("Status")
    private Long mStatus;

    public String getMessage() {
        return mMessage;
    }

    public void setMessage(String message) {
        mMessage = message;
    }

    public Long getMessageCode() {
        return mMessageCode;
    }

    public void setMessageCode(Long messageCode) {
        mMessageCode = messageCode;
    }

    public String getPersianMessage() {
        return mPersianMessage;
    }

    public void setPersianMessage(String persianMessage) {
        mPersianMessage = persianMessage;
    }

    public Long getStatus() {
        return mStatus;
    }

    public void setStatus(Long status) {
        mStatus = status;
    }

}
