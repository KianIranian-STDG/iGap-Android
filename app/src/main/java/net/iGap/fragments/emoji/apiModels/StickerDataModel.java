
package net.iGap.fragments.emoji.apiModels;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class StickerDataModel {

    @SerializedName("_id")
    private String id;
    @SerializedName("createdAt")
    private String createdAt;
    @SerializedName("fileName")
    private String fileName;
    @SerializedName("fileSize")
    private int fileSize;
    @SerializedName("groupId")
    private String groupId;
    @SerializedName("name")
    private String name;
    @SerializedName("refId")
    private long refId;
    @SerializedName("sort")
    private long sort;
    @SerializedName("giftAmount")
    private long giftAmount;
    @SerializedName("status")
    private String status;
    @SerializedName("tags")
    private List<String> tags;
    @SerializedName("token")
    private String token;
    @SerializedName("updatedAt")
    private String updatedAt;
    @SerializedName("isFavorite")
    private boolean isFavorite;

    public String getId() {
        return id;
    }

    public void setId(String _id) {
        this.id = _id;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public int getFileSize() {
        return fileSize;
    }

    public void setFileSize(int fileSize) {
        this.fileSize = fileSize;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getRefId() {
        return refId;
    }

    public void setRefId(long refId) {
        this.refId = refId;
    }

    public long getSort() {
        return sort;
    }

    public void setSort(long sort) {
        this.sort = sort;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setGiftAmount(long giftAmount) {
        this.giftAmount = giftAmount;
    }

    public long getGiftAmount() {
        return giftAmount;
    }
}
