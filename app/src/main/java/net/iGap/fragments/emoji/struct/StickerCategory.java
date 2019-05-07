package net.iGap.fragments.emoji.struct;

public class StickerCategory {
    private String createdAt;

    private String name;

    private String id;

    private String sort;

    private String updatedAt;

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
