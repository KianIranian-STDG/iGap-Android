package net.iGap.model;


public class GalleryAlbumModel {

    private String id;
    private String caption = "";
    private String cover;

    public GalleryAlbumModel() {
    }

    public GalleryAlbumModel(String id, String caption, String cover) {
        this.id = id;
        this.caption = caption;
        this.cover = cover;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }
}
