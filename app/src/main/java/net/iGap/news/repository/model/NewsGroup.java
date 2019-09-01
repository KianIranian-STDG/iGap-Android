package net.iGap.news.repository.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class NewsGroup {

    @SerializedName("Ids")
    private String IDs;
    @SerializedName("content")
    private List<Groups> content;

    public NewsGroup() {
    }

    public List<Groups> getContent() {
        return content;
    }

    public void setContent(List<Groups> content) {
        this.content = content;
    }

    private class Groups {

        @SerializedName("id")
        private String id;
        @SerializedName("title")
        private String title;
        @SerializedName("alias")
        private String alias;
        @SerializedName("description")
        private String description;
        @SerializedName("image")
        private String image;

        public Groups() {
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

        public String getAlias() {
            return alias;
        }

        public void setAlias(String alias) {
            this.alias = alias;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }
    }
}
