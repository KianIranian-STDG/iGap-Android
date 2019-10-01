package net.iGap.news.repository.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class NewsMainBTN {

    @SerializedName("id")
    private String id;
    @SerializedName("title")
    private String title;
    @SerializedName("color")
    private String color;
    @SerializedName("link")
    private String link;

    public NewsMainBTN() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}
