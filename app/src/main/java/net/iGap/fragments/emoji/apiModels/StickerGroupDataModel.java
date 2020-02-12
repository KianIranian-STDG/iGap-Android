
package net.iGap.fragments.emoji.apiModels;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class StickerGroupDataModel {

    @SerializedName("_id")
    private String id;
    @SerializedName("avatarName")
    private String avatarName;
    @SerializedName("avatarSize")
    private long avatarSize;
    @SerializedName("avatarToken")
    private String avatarToken;
    @SerializedName("categoryId")
    private String categoryId;
    @SerializedName("createdAt")
    private long createdAt;
    @SerializedName("createdBy")
    private long createdBy;
    @SerializedName("isGiftable")
    private boolean isGiftable;
    @SerializedName("isNew")
    private boolean isNew;
    @SerializedName("isVip")
    private boolean isVip;
    @SerializedName("name")
    private String name;
    @SerializedName("price")
    private long price;
    @SerializedName("sort")
    private int sort;
    @SerializedName("status")
    private String status;
    @SerializedName("stickers")
    private List<StickerDataModel> stickers;
    @SerializedName("type")
    private String type;
    @SerializedName("updatedAt")
    private String updatedAt;
    @SerializedName("isInUserList")
    private boolean isInUserList;
    @SerializedName("isReadonly")
    private boolean isReadonly;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAvatarName() {
        return avatarName;
    }

    public void setAvatarName(String avatarName) {
        this.avatarName = avatarName;
    }

    public long getAvatarSize() {
        return avatarSize;
    }

    public void setAvatarSize(long avatarSize) {
        this.avatarSize = avatarSize;
    }

    public String getAvatarToken() {
        return avatarToken;
    }

    public void setAvatarToken(String avatarToken) {
        this.avatarToken = avatarToken;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public long getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(long createdBy) {
        this.createdBy = createdBy;
    }

    public boolean getIsGiftable() {
        return isGiftable;
    }

    public void setIsGiftable(boolean isGiftable) {
        this.isGiftable = isGiftable;
    }

    public boolean getIsNew() {
        return isNew;
    }

    public void setIsNew(boolean isNew) {
        this.isNew = isNew;
    }

    public boolean getIsVip() {
        return isVip;
    }

    public void setIsVip(boolean isVip) {
        this.isVip = isVip;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getPrice() {
        return price;
    }

    public void setPrice(long price) {
        this.price = price;
    }

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<StickerDataModel> getStickers() {
        return stickers;
    }

    public void setStickers(List<StickerDataModel> stickers) {
        this.stickers = stickers;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public boolean isInUserList() {
        return isInUserList;
    }

    public void setInUserList(boolean inUserList) {
        isInUserList = inUserList;
    }

    public boolean isReadonly() {
        return isReadonly;
    }

    public void setReadonly(boolean readonly) {
        isReadonly = readonly;
    }
}
