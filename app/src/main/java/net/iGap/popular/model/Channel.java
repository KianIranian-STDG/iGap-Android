package net.iGap.popular.model;

import android.graphics.drawable.Drawable;

public class Channel {
    private Drawable channelImage;
    private String channelTitle;
    public Drawable getChannelImage() {
        return channelImage;
    }

    public void setChannelImage(Drawable channelImage) {
        this.channelImage = channelImage;
    }

    public String getChannelTitle() {
        return channelTitle;
    }

    public void setChannelTitle(String channelTitle) {
        this.channelTitle = channelTitle;
    }


}
