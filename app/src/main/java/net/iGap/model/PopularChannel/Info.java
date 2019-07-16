
package net.iGap.model.PopularChannel;

import com.google.gson.annotations.SerializedName;

public class Info {

    @SerializedName("advertisement")
    private Advertisement mAdvertisement;
    @SerializedName("createdAt")
    private String mCreatedAt;
    @SerializedName("has_ad")
    private Boolean mHasAd;
    @SerializedName("icon")
    private String mIcon;
    @SerializedName("id")
    private String mId;
    @SerializedName("slug")
    private String mSlug;
    @SerializedName("title")
    private String mTitle;
    @SerializedName("type")
    private String mType;
    @SerializedName("updatedAt")
    private String mUpdatedAt;
    @SerializedName("scale")
    private String mScale;

    public Advertisement getAdvertisement() {
        return mAdvertisement;
    }

    public void setAdvertisement(Advertisement advertisement) {
        mAdvertisement = advertisement;
    }

    public String getCreatedAt() {
        return mCreatedAt;
    }

    public void setCreatedAt(String createdAt) {
        mCreatedAt = createdAt;
    }

    public Boolean getHasAd() {
        return mHasAd;
    }

    public void setHasAd(Boolean hasAd) {
        mHasAd = hasAd;
    }

    public String getIcon() {
        return mIcon;
    }

    public void setIcon(String icon) {
        mIcon = icon;
    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public String getSlug() {
        return mSlug;
    }

    public void setSlug(String slug) {
        mSlug = slug;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getType() {
        return mType;
    }

    public void setType(String type) {
        mType = type;
    }

    public String getUpdatedAt() {
        return mUpdatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        mUpdatedAt = updatedAt;
    }

    public String getScale() {
        return mScale;
    }

    public void setScale(String mScale) {
        this.mScale = mScale;
    }
}
