package net.iGap.story.viewPager;

import java.io.Serializable;

public class Story implements Serializable {

    private String url;
    private long storyData;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isVideo() {
        return url.contains(".mp4");
    }

    public void setStoryData(long storyData) {
        this.storyData = storyData;
    }

    public long getStoryData() {
        return storyData;
    }
}
