
package net.iGap.model.PopularChannel;

import com.google.gson.annotations.SerializedName;

public class Slide {

    @SerializedName("action_type")
    private Long mActionType;
    @SerializedName("height")
    private Long mHeight;
    @SerializedName("id")
    private String mId;
    @SerializedName("image_url")
    private String mImageUrl;
    @SerializedName("title")
    private String mTitle;
    @SerializedName("width")
    private Long mWidth;

    public Long getActionType() {
        return mActionType;
    }

    public void setActionType(Long actionType) {
        mActionType = actionType;
    }

    public Long getHeight() {
        return mHeight;
    }

    public void setHeight(Long height) {
        mHeight = height;
    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public String getImageUrl() {
        return mImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        mImageUrl = imageUrl;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public Long getWidth() {
        return mWidth;
    }

    public void setWidth(Long width) {
        mWidth = width;
    }

}
