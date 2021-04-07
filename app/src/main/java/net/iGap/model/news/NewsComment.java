package net.iGap.model.news;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class NewsComment {

    @SerializedName("articleId")
    private String articleIDِ;
    @SerializedName("userName")
    private String username;
    @SerializedName("commentContent")
    private String comment;
    @SerializedName("commentDate")
    private String date;

    public NewsComment() {
    }

    public String getArticleIDِ() {
        return articleIDِ;
    }

    public void setArticleIDِ(String articleIDِ) {
        this.articleIDِ = articleIDِ;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
