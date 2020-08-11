package net.iGap.model.popularChannel;

import com.google.gson.annotations.SerializedName;

import java.util.List;


public class Advertisement {

    @SerializedName("id")
    private String mId;
    @SerializedName("looped")
    private Boolean mLooped;
    @SerializedName("playback_time")
    private int mPlaybackTime;
    @SerializedName("slides")
    private List<Slide> mSlides;
    @SerializedName("title")
    private String mTitle;
    @SerializedName("title_en")
    private String mTitleEn;
    @SerializedName("scale")
    private String mScale;

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public Boolean getLooped() {
        return mLooped;
    }

    public void setLooped(Boolean looped) {
        mLooped = looped;
    }


    public List<Slide> getSlides() {
        return mSlides;
    }

    public void setSlides(List<Slide> slides) {
        mSlides = slides;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }


    public String getmScale() {
        return mScale;
    }

    public void setmScale(String mScale) {
        this.mScale = mScale;
    }

    public int getmPlaybackTime() {
        return mPlaybackTime;
    }

    public void setmPlaybackTime(int mPlaybackTime) {
        this.mPlaybackTime = mPlaybackTime;
    }

    public String getmTitleEn() {
        return mTitleEn;
    }

    public void setmTitleEn(String mTitleEn) {
        this.mTitleEn = mTitleEn;
    }
}
