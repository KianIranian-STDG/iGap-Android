package net.iGap.news.repository.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class NewsComment {

    @SerializedName("Ids")
    private String IDs;
    @SerializedName("content")
    private List<Comment> comments;

    public NewsComment() {
    }

    public String getIDs() {
        return IDs;
    }

    public void setIDs(String IDs) {
        this.IDs = IDs;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public List<Comment> getFakeData() {
        List<Comment> comments = new ArrayList<>();
        comments.add(new Comment("ahmad", "good!", "19/04/98", "h.amini@yahoo.com"));
        comments.add(new Comment("ahmad", "good!", "19/04/98", "h.amini@yahoo.com"));
        comments.add(new Comment("ahmad", "good!", "19/04/98", "h.amini@yahoo.com"));
        comments.add(new Comment("ahmad", "good!", "19/04/98", "h.amini@yahoo.com"));
        comments.add(new Comment("ahmad", "good!", "19/04/98", "h.amini@yahoo.com"));
        comments.add(new Comment("ahmad", "good!", "19/04/98", "h.amini@yahoo.com"));
        return comments;
    }

    public class Comment {

        private String author;
        private String body;
        private String date;
        private String email;

        public Comment(String author, String body, String date, String email) {
            this.author = author;
            this.body = body;
            this.date = date;
            this.email = email;
        }

        public String getAuthor() {
            return author;
        }

        public void setAuthor(String author) {
            this.author = author;
        }

        public String getBody() {
            return body;
        }

        public void setBody(String body) {
            this.body = body;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }
    }
}
