package net.iGap.realm;

import net.iGap.fragments.emoji.struct.StructIGSticker;

import io.realm.Realm;
import io.realm.RealmObject;

public class RealmStickerItem extends RealmObject {
    private String id;
    private String fileName;
    private String groupId;
    private String name;
    private String token;
    private boolean isFavorite;
    private long recentTime;
    private long fileSize;

    public static RealmStickerItem put(Realm realm, StructIGSticker structIGSticker) {
        RealmStickerItem realmStickerItem = realm.where(RealmStickerItem.class).equalTo(RealmStickerItemFields.ID, structIGSticker.getId()).findFirst();

        if (realmStickerItem == null)
            realmStickerItem = realm.createObject(RealmStickerItem.class);

        realmStickerItem.setId(structIGSticker.getId());
        realmStickerItem.setFileName(structIGSticker.getFileName());
        realmStickerItem.setFileSize(structIGSticker.getFileSize());
        realmStickerItem.setGroupId(structIGSticker.getGroupId());
        realmStickerItem.setName(structIGSticker.getName());
        realmStickerItem.setToken(structIGSticker.getToken());
        realmStickerItem.setFavorite(structIGSticker.isFavorite());
        return realmStickerItem;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }

    public void setRecentTime(long recentTime) {
        this.recentTime = recentTime;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public String getId() {
        return id;
    }

    public String getFileName() {
        return fileName;
    }

    public String getGroupId() {
        return groupId;
    }

    public String getName() {
        return name;
    }

    public String getToken() {
        return token;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setRecent() {
        setRecentTime(System.currentTimeMillis());
    }

    public long getRecentTime() {
        return recentTime;
    }

    public long getFileSize() {
        return fileSize;
    }
}
