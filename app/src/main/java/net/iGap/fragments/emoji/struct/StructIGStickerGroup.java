package net.iGap.fragments.emoji.struct;

import com.vanniktech.emoji.sticker.struct.StructGroupSticker;
import com.vanniktech.emoji.sticker.struct.StructItemSticker;

import net.iGap.fragments.emoji.HelperDownloadSticker;
import net.iGap.realm.RealmStickers;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static net.iGap.fragments.emoji.struct.StructIGSticker.ANIMATED_STICKER;
import static net.iGap.fragments.emoji.struct.StructIGSticker.NORMAL_STICKER;

public class StructIGStickerGroup {
    public static final String RECENT_GROUP = "-1";

    private String groupId;
    private long createdAt;
    private long refId;
    private String name;
    private String avatarToken;
    private String avatarPath = "";
    private long avatarSize;
    private String avatarName;
    private long price;
    private boolean isVip;
    private int sort;
    private boolean approved;
    private long createdBy;
    private boolean isFavorite;
    private int avatarType;
    private boolean inMySticker;
    private List<StructIGSticker> stickers;

    public StructIGStickerGroup(String groupId) {
        this.groupId = groupId;
    }

    public StructIGStickerGroup() {

    }

    public StructIGStickerGroup setValueWithRealmStickers(RealmStickers realmStickersGroup) {
        if (realmStickersGroup != null && realmStickersGroup.isValid()) {
            setGroupId(realmStickersGroup.getSt_id());
            setStickers(realmStickersGroup.getIGGroupStickers());
            setSort(realmStickersGroup.getSort());
            setRefId(realmStickersGroup.getRefId());
            setPrice(realmStickersGroup.getPrice());
            setName(realmStickersGroup.getName());
            setFavorite(realmStickersGroup.isFavorite());
            setCreatedBy(realmStickersGroup.getCreatedBy());
            setCreatedAt(realmStickersGroup.getCreatedAt());
            setAvatarToken(realmStickersGroup.getAvatarToken());
            setAvatarSize(realmStickersGroup.getAvatarSize());
            setAvatarName(realmStickersGroup.getAvatarName());
            setApproved(realmStickersGroup.isApproved());
            setAvatarPath(realmStickersGroup.getUri());
            setVip(realmStickersGroup.isVip());
            setInMySticker(true);
        }
        return this;
    }

    public void setValueWithOldStruct(StructGroupSticker structGroupSticker) {
        if (structGroupSticker != null) {
            setSort(structGroupSticker.getSort());
            setRefId(structGroupSticker.getRefId());
            setPrice(structGroupSticker.getPrice());
            setName(structGroupSticker.getName());
            setFavorite(structGroupSticker.getIsFavorite());
            setCreatedBy(structGroupSticker.getCreatedBy());
            setCreatedAt(structGroupSticker.getCreatedAt());
            setAvatarToken(structGroupSticker.getAvatarToken());
            setAvatarSize(structGroupSticker.getAvatarSize());
            setAvatarName(structGroupSticker.getAvatarName());
            setAvatarPath(structGroupSticker.getUri());
            setVip(structGroupSticker.getIsVip());
            setStickersWithOldStruct(structGroupSticker.getStickers());
            setInMySticker(false);
        }
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public long getRefId() {
        return refId;
    }

    public void setRefId(long refId) {
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

    public int getAvatarType() {
        return avatarType;
    }

    public void setAvatarType(int avatarType) {
        this.avatarType = avatarType;
    }

    public void setAvatarPath(String avatarPath) {
        this.avatarPath = avatarPath;

        if (this.avatarPath == null || this.avatarPath.equals(""))
            avatarType = 100;
        else if (this.avatarPath.endsWith(".json")) {
            avatarType = ANIMATED_STICKER;
        } else {
            avatarType = NORMAL_STICKER;
        }
    }

    public String getAvatarPath() {
        return avatarPath;
    }

    public long getAvatarSize() {
        return avatarSize;
    }

    public void setAvatarSize(long avatarSize) {
        this.avatarSize = avatarSize;
    }

    public String getAvatarName() {
        return avatarName;
    }

    public void setAvatarName(String avatarName) {
        this.avatarName = avatarName;
    }

    public long getPrice() {
        return price;
    }

    public void setPrice(long price) {
        this.price = price;
    }

    public boolean isVip() {
        return isVip;
    }

    public void setVip(boolean vip) {
        isVip = vip;
    }

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    public boolean isApproved() {
        return approved;
    }

    public void setApproved(boolean approved) {
        this.approved = approved;
    }

    public long getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(long createdBy) {
        this.createdBy = createdBy;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }

    public List<StructIGSticker> getStickers() {
        return stickers;
    }

    public void setStickers(List<StructIGSticker> stickers) {
        this.stickers = stickers;
    }

    private void setStickersWithOldStruct(List<StructItemSticker> stickers) {
        List<StructIGSticker> stickers1 = new ArrayList<>();
        for (int i = 0; i < stickers.size(); i++) {
            StructIGSticker structIGSticker = new StructIGSticker();
            structIGSticker.setToken(stickers.get(i).getToken());
            structIGSticker.setPath(HelperDownloadSticker.downloadStickerPath(stickers.get(i).getToken(), stickers.get(i).getAvatarName()));
            structIGSticker.setId(stickers.get(i).getId());
            structIGSticker.setGroupId(stickers.get(i).getGroupId());
            structIGSticker.setFileSize(stickers.get(i).getAvatarSize());
            structIGSticker.setFileName(stickers.get(i).getAvatarName());
            structIGSticker.setName(stickers.get(i).getName());
            stickers1.add(structIGSticker);
        }
        this.stickers = stickers1;
    }

    public boolean hasData() {
        return getStickers() != null && getStickers().size() > 0;
    }

    public int getStickersSize() {
        return getStickers() != null ? getStickers().size() : 0;
    }

    public boolean hasFileOnLocal() {
        return new File(avatarPath).exists() && new File(avatarPath).canRead();
    }

    public void setInMySticker(boolean inMySticker) {
        this.inMySticker = inMySticker;
    }

    public boolean isInMySticker() {
        return inMySticker;
    }
}
