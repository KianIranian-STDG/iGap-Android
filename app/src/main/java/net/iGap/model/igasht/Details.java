package net.iGap.model.igasht;

import com.google.gson.annotations.SerializedName;


public class Details {

    @SerializedName("body")
    private IGashtOrder mBody;
    @SerializedName("error")
    private Error mError;
    @SerializedName("path")
    private String mPath;

    public IGashtOrder getBody() {
        return mBody;
    }

    public void setBody(IGashtOrder body) {
        mBody = body;
    }

    public Error getError() {
        return mError;
    }

    public void setError(Error error) {
        mError = error;
    }

    public String getPath() {
        return mPath;
    }

    public void setPath(String path) {
        mPath = path;
    }

}
