package net.iGap.news.repository.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class NewsList {

    @SerializedName("Ids")
    private String IDs;
    @SerializedName("content")
    private List<News> news;

    public NewsList() {
    }

    public String getIDs() {
        return IDs;
    }

    public void setIDs(String IDs) {
        this.IDs = IDs;
    }

    public List<News> getNews() {
        return news;
    }

    public void setNews(List<News> news) {
        this.news = news;
    }

    private class News {
        @SerializedName("id")
        private String id;
        @SerializedName("title")
        private String title;
        @SerializedName("image")
        private String image;

        public News() {
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }
    }
}
