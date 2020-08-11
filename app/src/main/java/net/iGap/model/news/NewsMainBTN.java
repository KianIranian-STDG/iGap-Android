package net.iGap.model.news;

import com.google.gson.annotations.SerializedName;

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
        if (!color.startsWith("#"))
            color = "#" + color;
        if (!(color.length() == 7 || color.length() == 4))
            return "#000";
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getLink() {
        if (!link.startsWith("http") && !link.startsWith("igap"))
            link = "http://" + link;
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}
