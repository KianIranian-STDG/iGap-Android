package net.iGap.helper.emoji.struct;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import net.iGap.helper.emoji.struct.StructItemSticker;

import java.io.Serializable;
import java.util.List;

public class StructGroupSticker implements Serializable {

    @SerializedName("createdAt")
    @Expose
    private Long createdAt;
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("refId")
    @Expose
    private Long refId;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("avatarToken")
    @Expose
    private String avatarToken;
    @SerializedName("uri")
    @Expose
    private String uri;
    @SerializedName("avatarSize")
    @Expose
    private int avatarSize;
    @SerializedName("avatarName")
    @Expose
    private String avatarName;
    @SerializedName("price")
    @Expose
    private Long price;
    @SerializedName("isVip")
    @Expose
    private Boolean isVip;
    @SerializedName("sort")
    @Expose
    private Integer sort;
    @SerializedName("createdBy")
    @Expose
    private Long createdBy;
    @SerializedName("stickers")
    @Expose
    private List<StructItemSticker> stickers = null;

    public Long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Long createdAt) {
        this.createdAt = createdAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getRefId() {
        return refId;
    }

    public void setRefId(Long refId) {
        this.refId = refId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAvatarToken() {
        return avatarToken;
    }

    public void setAvatarToken(String avatarToken) {
        this.avatarToken = avatarToken;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public int getAvatarSize() {
        return avatarSize;
    }

    public void setAvatarSize(int avatarSize) {
        this.avatarSize = avatarSize;
    }

    public String getAvatarName() {
        return avatarName;
    }

    public void setAvatarName(String avatarName) {
        this.avatarName = avatarName;
    }

    public Boolean getVip() {
        return isVip;
    }

    public void setVip(Boolean vip) {
        isVip = vip;
    }

    public Long getPrice() {
        return price;
    }

    public void setPrice(Long price) {
        this.price = price;
    }

    public Boolean getIsVip() {
        return isVip;
    }

    public void setIsVip(Boolean isVip) {
        this.isVip = isVip;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    public Long getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }

    public List<StructItemSticker> getStickers() {
        return stickers;
    }

    public void setStickers(List<StructItemSticker> stickers) {
        this.stickers = stickers;
    }

}