package net.iGap.model.FavoriteChannel;

import com.google.gson.annotations.SerializedName;

public class Category {
    @SerializedName("id")
    private String mId;
    @SerializedName("title")
    private String mTitle;
    @SerializedName("icon")
    private String mIcon;
    @SerializedName("slug")
    private String mSlug;
    @SerializedName("titleEn")
    private String mTitleEn;


    public String getmId() {
        return mId;
    }

    public void setmId(String mId) {
        this.mId = mId;
    }

    public String getmTitle() {
        return mTitle;
    }

    public void setmTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public String getmIcon() {
        return mIcon;
    }

    public void setmIcon(String mIcon) {
        this.mIcon = mIcon;
    }

    public String getmSlug() {
        return mSlug;
    }

    public void setmSlug(String mSlug) {
        this.mSlug = mSlug;
    }

    public String getmTitleEn() {
        return mTitleEn;
    }

    public void setmTitleEn(String mTitleEn) {
        this.mTitleEn = mTitleEn;
    }
}
