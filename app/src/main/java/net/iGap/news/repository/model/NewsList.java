package net.iGap.news.repository.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
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

    public class News {
        @SerializedName("id")
        private String id;
        @SerializedName("title")
        private String title;
        @SerializedName("image")
        private String image;
        @SerializedName("type")
        private int type;

        public News() {
        }

        public News(String id, String title, String image, int type) {
            this.id = id;
            this.title = title;
            this.image = image;
            this.type = type;
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

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }
    }


    public List<News> getFake() {
        List<News> groups = new ArrayList<>();
        groups.add(new News("101", "temp", "", 0));
        groups.add(new News("101", "temp", "", 1));
        groups.add(new News("101", "temp", "", 0));
        groups.add(new News("101", "temp", "", 0));
        groups.add(new News("101", "temp", "", 1));
        return groups;
    }
}
