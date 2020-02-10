
package net.iGap.fragments.emoji.apiModels;

import com.google.gson.annotations.SerializedName;

public class Info {

    @SerializedName("scale")
    private String scale;
    @SerializedName("title")
    private String title;
    @SerializedName("title_en")
    private String titleEn;

    public String getScale() {
        return scale;
    }

    public void setScale(String scale) {
        this.scale = scale;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitleEn() {
        return titleEn;
    }

    public void setTitleEn(String titleEn) {
        this.titleEn = titleEn;
    }

}
