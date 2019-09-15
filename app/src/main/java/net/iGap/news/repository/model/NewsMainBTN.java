package net.iGap.news.repository.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class NewsMainBTN {

    @SerializedName("id")
    private int id;
    @SerializedName("title")
    private String title;
    @SerializedName("color")
    private int color;

    public NewsMainBTN() {
    }

    public NewsMainBTN(int id, String title, int color) {
        this.id = id;
        this.title = title;
        this.color = color;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }
}
