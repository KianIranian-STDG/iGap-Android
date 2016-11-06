package com.iGap.realm;

import com.iGap.proto.ProtoGlobal;
import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmResults;
import io.realm.RealmThumbnailRealmProxy;
import io.realm.Sort;
import io.realm.annotations.PrimaryKey;
import org.parceler.Parcel;

/**
 * Created by Alireza Eskandarpour Shoferi (meNESS) on 9/29/2016.
 */
@Parcel(implementations = { RealmThumbnailRealmProxy.class },
    value = Parcel.Serialization.BEAN,
    analyze = { RealmThumbnail.class }) public class RealmThumbnail extends RealmObject {
    @PrimaryKey private long id;
    private long messageId;
    private long size;
    private int width;
    private int height;
    private String cacheId;

    private static long getCorrectId(Realm realm) {
        RealmResults results = realm.where(RealmThumbnail.class).findAllSorted("id", Sort.DESCENDING);

        long id = 1;
        if (results.size() > 0) {
            id = realm.where(RealmThumbnail.class).findAllSorted("id", Sort.DESCENDING).first().getId();
            id++;
        }

        return id;
    }

    public static void create(long id, final long messageId, final ProtoGlobal.Thumbnail thumbnail) {
        Realm realm = Realm.getDefaultInstance();
        RealmThumbnail realmThumbnail = realm.createObject(RealmThumbnail.class);
        realmThumbnail.setCacheId(thumbnail.getCacheId());
        realmThumbnail.setWidth(thumbnail.getWidth());
        realmThumbnail.setSize(thumbnail.getSize());
        realmThumbnail.setHeight(thumbnail.getHeight());
        realmThumbnail.setId(getCorrectId(realm));
        realmThumbnail.setMessageId(messageId);

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
