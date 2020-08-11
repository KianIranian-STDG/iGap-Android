package net.iGap.model.news;

import android.text.format.DateUtils;

import com.google.gson.annotations.SerializedName;

import net.iGap.helper.HelperCalander;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class NewsDetail {

    @SerializedName("titr")
    private String title;
    @SerializedName("rootitr")
    private String rootTitle;
    @SerializedName("fulltext")
    private String body;
    @SerializedName("publishDate")
    private String date;
    @SerializedName("lead")
    private String lead;
    @SerializedName("internalLink")
    private String link;
    @SerializedName("alias")
    private String alias;
    @SerializedName("image")
    private List<NewsImage> images;
    @SerializedName("source")
    private String source;
    @SerializedName("Original4")
    private String commentCount;
    @SerializedName("sourceLogo")
    private String sourceImage;
    @SerializedName("viewNumber")
    private String view;
    @SerializedName("tags")
    private String tags;

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

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
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
        return getTime();
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

    private String getTime() {
        if (date == null || date.isEmpty())
            return "";

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat time_sdf = new SimpleDateFormat("HH:mm");
        try {
            Date mDate = sdf.parse(date);
            long timeInMilliseconds = mDate.getTime();
            return HelperCalander.checkHijriAndReturnTime(timeInMilliseconds / DateUtils.SECOND_IN_MILLIS) + " | " + time_sdf.format(mDate.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
            return date;
        }
    }
}
