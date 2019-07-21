
package net.iGap.model.PopularChannel;

import com.google.gson.annotations.SerializedName;

import java.util.List;


public class Advertisement {

    @SerializedName("id")
    private String mId;
    @SerializedName("looped")
    private Boolean mLooped;
    @SerializedName("playback_time")
    private Long mPlaybackTime;
    @SerializedName("slides")
    private List<Slide> mSlides;
    @SerializedName("title")
    private String mTitle;
    @SerializedName("titleEn")
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

    public String getTitleEn() {
        return mTitleEn;
    }

    public void setTitleEn(String mTitleEn) {
        this.mTitleEn = mTitleEn;
    }

    public String getmScale() {
        return mScale;
    }

    public void setmScale(String mScale) {
        this.mScale = mScale;
    }

    public Long getmPlaybackTime() {
        return mPlaybackTime;
    }

    public void setmPlaybackTime(Long mPlaybackTime) {
        this.mPlaybackTime = mPlaybackTime;
    }
}
