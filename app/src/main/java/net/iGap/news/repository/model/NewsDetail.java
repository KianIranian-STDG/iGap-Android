package net.iGap.news.repository.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class NewsDetail {

    @SerializedName("titr")
    private String title;
    @SerializedName("rootitr")
    private String rootTitle;
    @SerializedName("fulltext")
    private String body;
    @SerializedName("originalDate")
    private String date;
    @SerializedName("lead")
    private String lead;
    @SerializedName("link")
    private String link;
    @SerializedName("alias")
    private String alias;
    @SerializedName("image")
    private List<NewsImage> images;
    // TODO Must be added to API
    @SerializedName("Original1")
    private String srouce;
    @SerializedName("Original2")
    private String view;
    @SerializedName("Original3")
    private String tags;
    @SerializedName("Original4")
    private String commentCount;
    @SerializedName("Original5")
    private String sourceImage;

    public NewsDetail() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getRootTitle() {
        return rootTitle;
    }

    public void setRootTitle(String rootTitle) {
        this.rootTitle = rootTitle;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getSrouce() {
        return srouce;
    }

    public void setSrouce(String srouce) {
        this.srouce = srouce;
    }

    public String getView() {
        return view;
    }

    public void setView(String view) {
        this.view = view;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(String commentCount) {
        this.commentCount = commentCount;
    }

    public String getLead() {
        return lead;
    }

    public void setLead(String lead) {
        this.lead = lead;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public List<NewsImage> getImages() {
        return images;
    }

    public void setImages(List<NewsImage> images) {
        this.images = images;
    }

    public String getSourceImage() {
        return sourceImage;
    }

    public void setSourceImage(String sourceImage) {
        this.sourceImage = sourceImage;
    }
}
