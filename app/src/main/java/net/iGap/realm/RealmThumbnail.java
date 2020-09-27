/*
 * This is the source code of iGap for Android
 * It is licensed under GNU AGPL v3.0
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright © 2017 , iGap - www.iGap.net
 * iGap Messenger | Free, Fast and Secure instant messaging application
 * The idea of the Kianiranian Company - www.kianiranian.com
 * All rights reserved.
 */

package net.iGap.realm;

import net.iGap.proto.ProtoGlobal;

import org.parceler.Parcel;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.net_iGap_realm_RealmThumbnailRealmProxy;

@Parcel(implementations = {net_iGap_realm_RealmThumbnailRealmProxy.class}, value = Parcel.Serialization.BEAN, analyze = {RealmThumbnail.class})
public class RealmThumbnail extends RealmObject {
    @PrimaryKey
    public long id;
    public long messageId;
    public long size;
    public int width;
    public int height;
    public String cacheId;

    public static RealmThumbnail put(Realm realm, long id, final long messageId, final ProtoGlobal.Thumbnail thumbnail) {
        RealmThumbnail realmThumbnail = realm.createObject(RealmThumbnail.class, id);
        realmThumbnail.setCacheId(thumbnail.getCacheId());
        realmThumbnail.setWidth(thumbnail.getWidth());
        realmThumbnail.setSize(thumbnail.getSize());
        realmThumbnail.setHeight(thumbnail.getHeight());
        realmThumbnail.setMessageId(messageId);

        return realmThumbnail;
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
