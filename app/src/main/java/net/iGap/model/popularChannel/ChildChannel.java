package net.iGap.model.popularChannel;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ChildChannel {

    @SerializedName("channels")
    private List<Channel> mChannels;
    @SerializedName("info")
    private Info mInfo;
    @SerializedName("pagination")
    private Pagination mPagination;

    public List<Channel> getChannels() {
        return mChannels;
    }

    public void setChannels(List<Channel> channels) {
        mChannels = channels;
    }

    public Info getInfo() {
        return mInfo;
    }

    public void setInfo(Info info) {
        mInfo = info;
    }

    public Pagination getPagination() {
        return mPagination;
    }

    public void setPagination(Pagination pagination) {
        mPagination = pagination;
    }

}
