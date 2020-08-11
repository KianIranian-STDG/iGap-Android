package net.iGap.model.news;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class NewsGroup {

    @SerializedName("Ids")
    private String IDs;
    @SerializedName("content")
    private List<Groups> groups;

    public NewsGroup() {
    }

    public String getIDs() {
        return IDs;
    }

    public void setIDs(String IDs) {
        this.IDs = IDs;
    }

    public List<Groups> getGroups() {
        return groups;
    }

    public void setGroups(List<Groups> groups) {
        this.groups = groups;
    }

    public List<Groups> getFake() {
        List<Groups> groups = new ArrayList<>();
        groups.add(new Groups("101", "temp", "", "", null));
        groups.add(new Groups("101", "temp", "", "", null));
        groups.add(new Groups("101", "temp", "", "", null));
        groups.add(new Groups("101", "temp", "", "", null));
        return groups;
    }

    public class Groups {

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

        public Groups(String id, String title, String alias, String description, String image) {
            this.id = id;
            this.title = title;
            this.alias = alias;
            this.description = description;
            this.image = image;
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
            if (image == null || image.isEmpty())
                return null;
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }
    }
}
