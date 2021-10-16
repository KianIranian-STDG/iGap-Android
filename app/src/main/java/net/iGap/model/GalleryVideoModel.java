package net.iGap.model;


import androidx.annotation.Nullable;

public class GalleryVideoModel {

    private String id;
    private String caption = "";
    private String path;
    private String mediaType;

    public GalleryVideoModel() {
    }

    public GalleryVideoModel(String id, String caption, String path) {
        this.id = id;
        this.caption = caption;
        this.path = path;
    }

    public GalleryVideoModel(String id, String caption, String path, String mediaType) {
        this.id = id;
        this.caption = caption;
        this.path = path;
        this.mediaType = mediaType;
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

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getMediaType() {
        return mediaType;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj instanceof GalleryVideoModel) {
            return ((GalleryVideoModel) obj).getId().equals(this.id);
        }
        return super.equals(obj);
    }
}
