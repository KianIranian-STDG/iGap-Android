package net.iGap.fragments.emoji.struct;

import net.iGap.fragments.emoji.apiModels.StickerDataModel;
import net.iGap.fragments.emoji.apiModels.StickerGroupDataModel;
import net.iGap.realm.RealmStickerGroup;
import net.iGap.repository.StickerRepository;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static net.iGap.fragments.emoji.struct.StructIGSticker.ANIMATED_STICKER;
import static net.iGap.fragments.emoji.struct.StructIGSticker.NORMAL_STICKER;

public class StructIGStickerGroup {
    public static final String RECENT_GROUP = "-1";
    public static final String FAVORITE_GROUP = "-2";

    private String groupId;
    private String avatarName;
    private String avatarToken;
    private String name;
    private String avatarPath;
    private String type;
    private String categoryId;
    private boolean isVip;
    private boolean isNew;
    private boolean approved;
    private boolean isInUserList;
    private boolean isReadonly;
    private boolean isGiftable;
    private long avatarSize;
    private long price;
    private int avatarType;
    private List<StructIGSticker> stickers;


    public StructIGStickerGroup(String groupId) {
        this.groupId = groupId;
    }

    public StructIGStickerGroup() {

    }

    public StructIGStickerGroup(StickerGroupDataModel dataModel) {
        setGroupId(dataModel.getId());
        setAvatarPath(StickerRepository.getInstance().getStickerPath(dataModel.getAvatarToken(), dataModel.getAvatarName()));
        setPrice(dataModel.getPrice());
        setName(dataModel.getName());
        setAvatarToken(dataModel.getAvatarToken());
        setAvatarSize(dataModel.getAvatarSize());
        setAvatarName(dataModel.getAvatarName());
        setVip(dataModel.getIsVip());
        setStickers(setStickersWithDataModel(dataModel.getStickers()));
        setNew(dataModel.getIsNew());
        setType(dataModel.getType());
        setCategoryId(dataModel.getCategoryId());
        setGiftable(dataModel.getIsGiftable());
        setInUserList(dataModel.isInUserList());
        setReadonly(dataModel.isReadonly());
    }

    private List<StructIGSticker> setStickersWithDataModel(List<StickerDataModel> dataModels) {
        List<StructIGSticker> stickers = new ArrayList<>();
        for (int i = 0; i < dataModels.size(); i++) {
            StructIGSticker sticker = new StructIGSticker(dataModels.get(i));
            stickers.add(sticker);
        }
        return stickers;
    }

    public StructIGStickerGroup(RealmStickerGroup realmStickersGroup) {
        if (realmStickersGroup != null && realmStickersGroup.isValid()) {
            setGroupId(realmStickersGroup.getId());
            setStickers(realmStickersGroup.getIGGroupStickers());
            setName(realmStickersGroup.getName());
            setAvatarToken(realmStickersGroup.getAvatarToken());
            setAvatarSize(realmStickersGroup.getAvatarSize());
            setAvatarName(realmStickersGroup.getAvatarName());
            setAvatarPath(StickerRepository.getInstance().getStickerPath(realmStickersGroup.getAvatarToken(), realmStickersGroup.getAvatarName()));
            setInUserList(true);
        }
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

    public boolean isApproved() {
        return approved;
    }

    public void setApproved(boolean approved) {
        this.approved = approved;
    }

    public void setNew(boolean aNew) {
        isNew = aNew;
    }

    public boolean isNew() {
        return isNew;
    }

    public List<StructIGSticker> getStickers() {
        return stickers;
    }

    public void setStickers(List<StructIGSticker> stickers) {
        this.stickers = stickers;
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

    public boolean isInUserList() {
        return isInUserList;
    }

    public boolean isReadonly() {
        return isReadonly;
    }

    public void setReadonly(boolean readonly) {
        isReadonly = readonly;
    }

    public void setInUserList(boolean inUserList) {
        isInUserList = inUserList;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setGiftable(boolean giftable) {
        isGiftable = giftable;
    }

    public boolean isGiftable() {
        return isGiftable;
    }
}
