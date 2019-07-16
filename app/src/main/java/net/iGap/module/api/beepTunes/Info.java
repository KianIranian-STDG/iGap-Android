
package net.iGap.module.api.beepTunes;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Info {

    @Expose
    private Boolean looped;
    @SerializedName("playback_time")
    private Long playbackTime;
    @Expose
    private String title;

    public Boolean getLooped() {
        return looped;
    }

    public void setLooped(Boolean looped) {
        this.looped = looped;
    }

    public Long getPlaybackTime() {
        return playbackTime;
    }

    public void setPlaybackTime(Long playbackTime) {
        this.playbackTime = playbackTime;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

}
