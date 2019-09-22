package net.iGap.news.repository.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class NewsFPList {

    @SerializedName("category")
    private String category;
    @SerializedName("categoryId")
    private String catID;
    @SerializedName("news")
    private List<News> news;
    @SerializedName("color")
    private int color;

    public NewsFPList() {
    }

    public NewsFPList(String category, String catID, List<News> news, int color) {
        this.category = category;
        this.catID = catID;
        this.news = news;
        this.color = color;
    }

    public NewsFPList addFakeData() {
        List<NewsContent> tempContent = new ArrayList<>();
        tempContent.add(new NewsContent("101", "Root Title", "Title", "Lead",
                null, "10/10/1993", "11/10/1993", "www.google.com", "www.google.com"));
        List<News> tempNews = new ArrayList<>();
        tempNews.add(new News("bbc", tempContent));
        NewsFPList news = new NewsFPList("sport", "101", tempNews, 0);
        return news;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public List<News> getNews() {
        return news;
    }

    public void setNews(List<News> news) {
        this.news = news;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public class News {
        @SerializedName("source")
        private String source;
        @SerializedName("contents")
        private List<NewsContent> contents;

        public News() {
        }

        public News(String source, List<NewsContent> contents) {
            this.source = source;
            this.contents = contents;
        }

        public String getSource() {
            return source;
        }

        public void setSource(String source) {
            this.source = source;
        }

        public List<NewsContent> getContents() {
            return contents;
        }

        public void setContents(List<NewsContent> contents) {
            this.contents = contents;
        }
    }

    public class NewsContent {
        @SerializedName("id")
        private String id;
        @SerializedName("rootitr")
        private String rootTitle;
        @SerializedName("titr")
        private String title;
        @SerializedName("lead")
        private String lead;
        @SerializedName("image")
        private List<Image> image;
        @SerializedName("originalDate")
        private String originalDate;
        @SerializedName("publishedDate")
        private String publishedDate;
        @SerializedName("internalLink")
        private String internalLink;
        @SerializedName("externalLink")
        private String externalLink;

        public NewsContent() {
        }

        public NewsContent(String id, String rootTitle, String title, String lead, List<Image> image,
                           String originalDate, String publishedDate, String internalLink, String externalLink) {
            this.id = id;
            this.rootTitle = rootTitle;
            this.title = title;
            this.lead = lead;
            this.image = image;
            this.originalDate = originalDate;
            this.publishedDate = publishedDate;
            this.internalLink = internalLink;
            this.externalLink = externalLink;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getRootTitle() {
            return rootTitle;
        }

        public void setRootTitle(String rootTitle) {
            this.rootTitle = rootTitle;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getLead() {
            return lead;
        }

        public void setLead(String lead) {
            this.lead = lead;
        }

        public String getOriginalDate() {
            return originalDate;
        }

        public void setOriginalDate(String originalDate) {
            this.originalDate = originalDate;
        }

        public String getPublishedDate() {
            return publishedDate;
        }

        public void setPublishedDate(String publishedDate) {
            this.publishedDate = publishedDate;
        }

        public String getInternalLink() {
            return internalLink;
        }

        public void setInternalLink(String internalLink) {
            this.internalLink = internalLink;
        }

        public String getExternalLink() {
            return externalLink;
        }

        public void setExternalLink(String externalLink) {
            this.externalLink = externalLink;
        }

        public List<Image> getImage() {
            return image;
        }

        public void setImage(List<Image> image) {
            this.image = image;
        }
    }

    public class Image {
        @SerializedName("Original")
        private String original;
        @SerializedName("thumb128")
        private String tmb128;
        @SerializedName("thumb256")
        private String tmb256;
        @SerializedName("thumb512")
        private String tmb512;

        public Image() {
        }

        public String getOriginal() {
            return original;
        }

        public void setOriginal(String original) {
            this.original = original;
        }

        public String getTmb128() {
            return tmb128;
        }

        public void setTmb128(String tmb128) {
            this.tmb128 = tmb128;
        }

        public String getTmb256() {
            return tmb256;
        }

        public void setTmb256(String tmb256) {
            this.tmb256 = tmb256;
        }

        public String getTmb512() {
            return tmb512;
        }

        public void setTmb512(String tmb512) {
            this.tmb512 = tmb512;
        }
    }

}
