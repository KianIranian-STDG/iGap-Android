
package net.iGap.model.PopularChannel;

import com.google.gson.annotations.SerializedName;
import com.nostra13.universalimageloader.utils.L;

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

    public Long getPlaybackTime() {
        return mPlaybackTime;
    }

    public void setPlaybackTime(Long playbackTime) {
        mPlaybackTime = playbackTime;
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

}
