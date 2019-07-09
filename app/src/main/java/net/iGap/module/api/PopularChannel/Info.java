
package net.iGap.module.api.PopularChannel;

import com.google.gson.annotations.SerializedName;

public class Info {

    @SerializedName("looped")
    private Boolean mLooped;
    @SerializedName("playback_time")
    private Long mPlaybackTime;
    @SerializedName("title")
    private String mTitle;

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

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

}
