package net.iGap.api.errorhandler;

import androidx.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

public class ErrorModel {

    @SerializedName("name")
    private String name;
    @SerializedName("message")
    private String message;
    private boolean needToRefresh = false;

    @Nullable
    private Throwable throwable;

    public ErrorModel() {
    }

    public ErrorModel(String name, String message, boolean needToRefresh) {
        this.name = name;
        this.message = message;
        this.needToRefresh = needToRefresh;
    }

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

    public boolean isNeedToRefresh() {
        return needToRefresh;
    }

    public void setNeedToRefresh(boolean needToRefresh) {
        this.needToRefresh = needToRefresh;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Nullable
    public Throwable getThrowable() {
        return throwable;
    }

    public void setThrowable(@Nullable Throwable throwable) {
        this.throwable = throwable;
    }
}
