package com.iGap.realm;

import com.iGap.proto.ProtoGlobal;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Alireza Eskandarpour Shoferi (meNESS) on 9/29/2016.
 */
public class RealmMessageThumbnail extends RealmObject {
    @PrimaryKey
    private long id;
    private long messageId;
    private long size;
    private int width;
    private int height;
    private String cacheId;

    public static void create(long id, final long messageId, final ProtoGlobal.Thumbnail thumbnail) {
        Realm realm = Realm.getDefaultInstance();
        RealmMessageThumbnail realmMessageThumbnail = realm.createObject(RealmMessageThumbnail.class);
        realmMessageThumbnail.setCacheId(thumbnail.getCacheId());
        realmMessageThumbnail.setWidth(thumbnail.getWidth());
        realmMessageThumbnail.setSize(thumbnail.getSize());
        realmMessageThumbnail.setHeight(thumbnail.getHeight());
        realmMessageThumbnail.setId(id);
        realmMessageThumbnail.setMessageId(messageId);

        realm.close();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getMessageId() {
        return messageId;
    }

    public void setMessageId(long messageId) {
        this.messageId = messageId;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public String getCacheId() {
        return cacheId;
    }

    public void setCacheId(String cacheId) {
        this.cacheId = cacheId;
    }
}
