package net.iGap.model.news;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class NewsFPList {

    @SerializedName("category")
    private String category;
    @SerializedName("categoryId")
    private String catID;
    @SerializedName("news")
    private List<News> news;

    public NewsFPList(String category, String catID, List<News> news) {
        this.category = category;
        this.catID = catID;
        this.news = news;
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

    public String getCatID() {
        return catID;
    }

    public void setCatID(String catID) {
        this.catID = catID;
    }

    public class News {
        @SerializedName("source")
        private String source;
        @SerializedName("colorRooTitr")
        private String colorRootTitile;
        @SerializedName("colorTitr")
        private String colorTitle;
        @SerializedName("color")
        private String color;
        @SerializedName("contents")
        private NewsContent contents;

        public News() {
        }

        public News(String source, NewsContent contents) {
            this.source = source;
            this.contents = contents;
        }

        public String getSource() {
            return source;
        }

        public void setSource(String source) {
            this.source = source;
        }

        public NewsContent getContents() {
            return contents;
        }

        public void setContents(NewsContent contents) {
            this.contents = contents;
        }

        public String getColorRootTitile() {
            if (colorRootTitile == null || !(colorRootTitile.length() == 6 || colorRootTitile.length() == 3))
                return "#000000";
            if (!colorRootTitile.startsWith("#"))
                return "#" + colorRootTitile;
            return colorRootTitile;
        }

        public void setColorRootTitile(String colorRootTitile) {
            this.colorRootTitile = colorRootTitile;
        }

        public String getColorTitle() {
            if (colorTitle == null || !(colorTitle.length() == 6 || colorTitle.length() == 3))
                return "#000000";
            if (!colorTitle.startsWith("#"))
                return "#" + colorTitle;
            return colorTitle;
        }

        public void setColorTitle(String colorTitle) {
            this.colorTitle = colorTitle;
        }

        public String getColor() {
            if (color == null || !(color.length() == 6 || color.length() == 3))
                return "#000000";
            if (!color.startsWith("#"))
                return "#" + color;
            return color;
        }

        public void setColor(String color) {
            this.color = color;
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
        @SerializedName("alias")
        private String alias;
        @SerializedName("image")
        private List<NewsImage> image;
        @SerializedName("originalDate")
        private String originalDate;
        @SerializedName("publishedDate")
        private String publishedDate;
        @SerializedName("internalLink")
        private String internalLink;
        @SerializedName("externalLink")
        private String externalLink;

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

        public List<NewsImage> getImage() {
            return image;
        }

        public void setImage(List<NewsImage> image) {
            this.image = image;
        }

        public String getAlias() {
            return alias;
        }

        public void setAlias(String alias) {
            this.alias = alias;
        }
    }

}
