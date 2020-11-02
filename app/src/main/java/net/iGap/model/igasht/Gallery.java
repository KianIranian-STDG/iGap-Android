package net.iGap.model.igasht;

import com.google.gson.annotations.SerializedName;

public class Gallery {

    @SerializedName("url")
    private String imageUrl;

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
