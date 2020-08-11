package net.iGap.module.api.beepTunes;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Slide {

    @SerializedName("action_type")
    private Long actionType;
    @Expose
    private Long height;
    @Expose
    private String id;
    @SerializedName("image_url")
    private String imageUrl;
    @Expose
    private String title;
    @Expose
    private Long width;

    public Long getActionType() {
        return actionType;
    }

    public void setActionType(Long actionType) {
        this.actionType = actionType;
    }

    public Long getHeight() {
        return height;
    }

    public void setHeight(Long height) {
        this.height = height;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Long getWidth() {
        return width;
    }

    public void setWidth(Long width) {
        this.width = width;
    }

}
