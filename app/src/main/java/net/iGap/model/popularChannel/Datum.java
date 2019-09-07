package net.iGap.model.popularChannel;

import com.google.gson.annotations.SerializedName;

import java.util.List;


public class Datum {

    @SerializedName("data_count")
    private Long mDataCount;
    @SerializedName("id")
    private String mId;
    @SerializedName("info")
    private Info mInfo;
    @SerializedName("slides")
    private List<Slide> mSlides;
    @SerializedName("type")
    private String mType;
    @SerializedName("channels")
    private List<Channel> channels;
    @SerializedName("categories")
    private List<Category> categories;


    public Long getDataCount() {
        return mDataCount;
    }

    public void setDataCount(Long dataCount) {
        mDataCount = dataCount;
    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public Info getInfo() {
        return mInfo;
    }

    public void setInfo(Info info) {
        mInfo = info;
    }

    public List<Slide> getSlides() {
        return mSlides;
    }

    public void setSlides(List<Slide> slides) {
        mSlides = slides;
    }

    public String getType() {
        return mType;
    }

    public void setType(String type) {
        mType = type;
    }

    public List<Channel> getChannels() {
        return channels;
    }

    public void setChannels(List<Channel> channels) {
        this.channels = channels;
    }

    public List<Category> getCategories() {
        return categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }
}
