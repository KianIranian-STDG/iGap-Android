package net.iGap.realm;

import net.iGap.fragments.emoji.struct.StructIGSticker;
import net.iGap.fragments.emoji.struct.StructIGStickerGroup;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmObject;

public class RealmStickerGroup extends RealmObject {
    private String id;
    private String name;
    private String type;
    private String avatarName;
    private String avatarToken;
    private String categoryId;
    private boolean isGiftable;
    private long avatarSize;
    private RealmList<RealmStickerItem> stickerItems;

    public static RealmStickerGroup put(Realm realm, StructIGStickerGroup stickerGroup) {
        RealmStickerGroup realmStickerGroup = realm.where(RealmStickerGroup.class).equalTo("id", stickerGroup.getGroupId()).findFirst();

        if (realmStickerGroup == null) {
            realmStickerGroup = realm.createObject(RealmStickerGroup.class);
        }
        realmStickerGroup.setId(stickerGroup.getGroupId());
        realmStickerGroup.setName(stickerGroup.getName());
        realmStickerGroup.setType(stickerGroup.getType());
        realmStickerGroup.setAvatarName(stickerGroup.getAvatarName());
        realmStickerGroup.setAvatarToken(stickerGroup.getAvatarToken());
        realmStickerGroup.setCategoryId(stickerGroup.getCategoryId());
        realmStickerGroup.setGiftable(stickerGroup.isGiftable());
        realmStickerGroup.setAvatarSize(stickerGroup.getAvatarSize());

        RealmList<RealmStickerItem> stickerItems = new RealmList<>();
        for (int i = 0; i < stickerGroup.getStickersSize(); i++) {
            stickerItems.add(RealmStickerItem.put(realm, stickerGroup.getStickers().get(i)));
        }
        realmStickerGroup.setStickerItems(stickerItems);

        return realmStickerGroup;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setAvatarName(String avatarName) {
        this.avatarName = avatarName;
    }

    public void setAvatarToken(String avatarToken) {
        this.avatarToken = avatarToken;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public void setGiftable(boolean giftable) {
        isGiftable = giftable;
    }

    public void setAvatarSize(long avatarSize) {
        this.avatarSize = avatarSize;
    }

    public void setStickerItems(RealmList<RealmStickerItem> stickerItems) {
        this.stickerItems = stickerItems;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getAvatarName() {
        return avatarName;
    }

    public String getAvatarToken() {
        return avatarToken;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public boolean isGiftable() {
        return isGiftable;
    }

    public long getAvatarSize() {
        return avatarSize;
    }

    public RealmList<RealmStickerItem> getStickerItems() {
        return stickerItems;
    }

    public void removeFromRealm() {
        if (stickerItems != null) {
            for (Iterator<RealmStickerItem> iterator = stickerItems.iterator(); iterator.hasNext(); ) {
                RealmStickerItem stickersDetails = iterator.next();
                if (stickersDetails != null) {
                    iterator.remove();
                    stickersDetails.deleteFromRealm();
                }
            }
        }

        deleteFromRealm();
    }

    public List<StructIGSticker> getIGGroupStickers() {
        if (getStickerItems() == null || getStickerItems().size() <= 0 || !getStickerItems().isValid())
            return new ArrayList<>();

        List<StructIGSticker> stickerDetails = new ArrayList<>();

        for (RealmStickerItem stickerItem : getStickerItems()) {
            StructIGSticker structIGSticker = new StructIGSticker(stickerItem);
            stickerDetails.add(structIGSticker);
        }

        return stickerDetails;
    }
}
