package net.iGap.model.news;

import com.google.gson.annotations.SerializedName;

public class NewsPN {
    @SerializedName("Ids")
    private String IDs;
    @SerializedName("content")
    private News news;

    public NewsPN() {
    }

    public String getIDs() {
        return IDs;
    }

    public void setIDs(String IDs) {
        this.IDs = IDs;
    }

    public News getNews() {
        return news;
    }

    public void setNews(News news) {
        this.news = news;
    }

    private class News {
        @SerializedName("id")
        private String id;
        @SerializedName("title")
        private String title;
        @SerializedName("originalSource")
        private String source;
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

        public String getSource() {
            return source;
        }

        public void setSource(String source) {
            this.source = source;
        }

        public String getImage() {
            if (image == null || image.isEmpty())
                return null;
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }
    }
}
