package net.iGap.fragments.emoji.struct;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class StickerCategory {

    @SerializedName("createdAt")
    @Expose
    private String createdAt;

    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("id")
    @Expose
    private String id;

    @SerializedName("sort")
    @Expose
    private String sort;

    @SerializedName("updatedAt")
    @Expose
    private String updatedAt;

    @SerializedName("status")
    @Expose
    private String status;

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "StickerCategory [createdAt = " + createdAt + ", name = " + name + ", id = " + id + ", sort = " + sort + ", updatedAt = " + updatedAt + ", status = " + status + "]";
    }


}
