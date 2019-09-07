package net.iGap.model.popularChannel;


import com.google.gson.annotations.SerializedName;

public class Slide {

    @SerializedName("action_link")
    private String mActionLink;
    @SerializedName("action_type")
    private Long mActionType;
    @SerializedName("id")
    private String mId;
    @SerializedName("image_url")
    private String mImageUrl;
    @SerializedName("title")
    private String mTitle;
    @SerializedName("title_en")
    private String mTitleEn;

    public Long getActionType() {
        return mActionType;
    }

    public void setActionType(Long actionType) {
        mActionType = actionType;
    }

    public void setmActionLink(String mActionLink) {
        this.mActionLink = mActionLink;
    }

    public String getmActionLink() {
        return mActionLink;
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

    public String getTitleEn() {
        return mTitleEn;
    }

    public void setTitleEn(String titleEn) {
        mTitleEn = titleEn;
    }

}
