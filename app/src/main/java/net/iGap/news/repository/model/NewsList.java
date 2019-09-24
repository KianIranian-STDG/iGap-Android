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
        @SerializedName("originalSource")
        private String source;
        @SerializedName("rootTitle")
        private String rootTitle;
        @SerializedName("date")
        private String date;
        @SerializedName("views")
        private String viewNum;

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
            if (image == null || image.isEmpty())
                return null;
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

        public String getSource() {
            return source;
        }

        public void setSource(String source) {
            this.source = source;
        }

        public String getRootTitle() {
            return rootTitle;
        }

        public void setRootTitle(String rootTitle) {
            this.rootTitle = rootTitle;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getViewNum() {
            return viewNum;
        }

        public void setViewNum(String viewNum) {
            this.viewNum = viewNum;
        }
    }


    public List<News> getFake() {
        List<News> groups = new ArrayList<>();
        groups.add(new News("101", "temp", null, 0));
        groups.add(new News("101", "temp", null, 1));
        groups.add(new News("101", "temp", null, 0));
        groups.add(new News("101", "temp", null, 0));
        groups.add(new News("101", "temp", null, 1));
        return groups;
    }
}
