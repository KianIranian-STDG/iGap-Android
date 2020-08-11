package net.iGap.fragments.emoji.struct;

import net.iGap.fragments.emoji.apiModels.StickerCategoryDataModel;

public class StructIGStickerCategory {
    private String id;
    private String name;
    private Long sort;
    private String status;

    public StructIGStickerCategory(StickerCategoryDataModel stickerCategoryDataModel) {
        setId(stickerCategoryDataModel.getId());
        setName(stickerCategoryDataModel.getName());
        setSort(stickerCategoryDataModel.getSort());
        setStatus(stickerCategoryDataModel.getStatus());
    }

    public StructIGStickerCategory() {
    }

    public String getId() {
        return id;
    }

    public StructIGStickerCategory(String id, String name, Long sort, String status) {
        this.id = id;
        this.name = name;
        this.sort = sort;
        this.status = status;
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
