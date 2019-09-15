package net.iGap.news.repository.model;

import com.google.gson.annotations.SerializedName;

public class NewsSlider {

    @SerializedName("image")
    private String image;
    @SerializedName("title")
    private String title;
    @SerializedName("description")
    private String desc;

    public NewsSlider() {
    }

    public NewsSlider(String image, String title, String desc) {
        this.image = image;
        this.title = title;
        this.desc = desc;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
