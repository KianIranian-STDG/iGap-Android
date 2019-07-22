
package net.iGap.model.PopularChannel;

import com.google.gson.annotations.SerializedName;

import java.util.List;


public class Channel {
    public static final String TYPE_PRIVATE = "PRIVATE";
    public static final String TYPE_PUBLIC = "PUBLIC";

    @SerializedName("categories")
    private List<String> mCategories;
    @SerializedName("icon")
    private String mIcon;
    @SerializedName("id")
    private String mId;
    @SerializedName("slug")
    private String mSlug;
    @SerializedName("title")
    private String mTitle;
    @SerializedName("titleEn")
    private String mTitleEn;
    @SerializedName("type")
    private String mType;

    public List<String> getCategories() {
        return mCategories;
    }

    public void setCategories(List<String> categories) {
        mCategories = categories;
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

    public String getTitleEn() {
        return mTitleEn;
    }

    public void setTitleEn(String titleEn) {
        mTitleEn = titleEn;
    }

    public String getmType() {
        return mType;
    }

    public void setmType(String mType) {
        this.mType = mType;
    }
}
