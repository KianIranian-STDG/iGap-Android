package net.iGap.model.news;

import com.google.gson.annotations.SerializedName;

public class NewsSubmitComment {

    @SerializedName("articleid")
    private String newsID;
    @SerializedName("comment")
    private String comment;
    @SerializedName("author")
    private String author;
    @SerializedName("email")
    private String email;

    public NewsSubmitComment(String newsID, String comment, String author, String email) {
        this.newsID = newsID;
        this.comment = comment;
        this.author = author;
        this.email = email;
    }

    public String getNewsID() {
        return newsID;
    }

    public void setNewsID(String newsID) {
        this.newsID = newsID;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
