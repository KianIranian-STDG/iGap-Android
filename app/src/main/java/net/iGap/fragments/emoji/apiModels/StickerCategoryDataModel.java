
package net.iGap.fragments.emoji.apiModels;

import com.google.gson.annotations.SerializedName;

public class StickerCategoryDataModel {

    @SerializedName("id")
    private String id;
    @SerializedName("name")
    private String name;
    @SerializedName("sort")
    private Long sort;
    @SerializedName("status")
    private String status;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getSort() {
        return sort;
    }

    public void setSort(Long sort) {
        this.sort = sort;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
