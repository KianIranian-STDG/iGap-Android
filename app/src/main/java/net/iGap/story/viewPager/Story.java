package net.iGap.story.viewPager;

import android.graphics.Bitmap;

import java.io.Serializable;

public class Story implements Serializable {

    private String url;
    private Bitmap bitmap;
    private String txt;
    private long storyData;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTxt() {
        return txt;
    }

    public void setTxt(String txt) {
        this.txt = txt;
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

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }
}
