package com.iGap.realm;

import com.iGap.proto.ProtoGlobal;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmResults;
import io.realm.Sort;
import io.realm.annotations.PrimaryKey;

public class RealmAvatar extends RealmObject {

    @PrimaryKey
    private long id;
    private long ownerId;
    private RealmAttachment file;

    public RealmAvatar() {
    }

    public RealmAvatar(long id) {
        this.id = id;
    }

    public static void put(long ownerId, ProtoGlobal.Avatar input) {
        if (!input.hasFile()) {
            return;
        }

        Realm realm = Realm.getDefaultInstance();
        RealmResults<RealmAvatar> ownerAvatars = realm.where(RealmAvatar.class).equalTo(RealmAvatarFields.OWNER_ID, ownerId).findAll();

        boolean exists = false;
        for (RealmAvatar avatar : ownerAvatars) {
            if (avatar.getFile() != null && avatar.getFile().getToken().equalsIgnoreCase(input.getFile().getToken())) {
                exists = true;
                break;
            }
        }

        if (!exists) {
            RealmAvatar avatar = realm.createObject(RealmAvatar.class);
            avatar.setId(input.getId());
            avatar.setOwnerId(ownerId);
            avatar.setFile(RealmAttachment.build(input.getFile()));
        }
        realm.close();
    }

    private static long getCorrectId(Realm realm) {
        RealmResults results = realm.where(RealmThumbnail.class).findAllSorted("id", Sort.DESCENDING);

        long id = 1;
        if (results.size() > 0) {
            id = realm.where(RealmThumbnail.class).findAllSorted("id", Sort.DESCENDING).first().getId();
            id++;
        }

        return id;
    }

    /**
     * use this method just for group or channel , because chat don't have avatar in room(chat
     * avatar exist in channel info)
     */

    public static RealmAvatar convert(final ProtoGlobal.Room room) {
        Realm realm = Realm.getDefaultInstance();
        ProtoGlobal.File file = null;
        switch (room.getType()) {
            case GROUP:
                ProtoGlobal.GroupRoom groupRoom = room.getGroupRoom();
                file = groupRoom.getAvatar().getFile();
                break;
            case CHANNEL:
                ProtoGlobal.ChannelRoom channelRoom = room.getChannelRoom();
                file = channelRoom.getAvatar().getFile();
                break;
        }

        //small thumbnail
        ProtoGlobal.Thumbnail smallThumbnail = file.getSmallThumbnail();
        RealmThumbnail realmThumbnailSmall = realm.createObject(RealmThumbnail.class);
        realmThumbnailSmall.setId(getCorrectId(realm));
        realmThumbnailSmall.setSize(smallThumbnail.getSize());
        realmThumbnailSmall.setWidth(smallThumbnail.getWidth());
        realmThumbnailSmall.setHeight(smallThumbnail.getHeight());
        realmThumbnailSmall.setCacheId(smallThumbnail.getCacheId());

        //large thumbnail
        ProtoGlobal.Thumbnail largeThumbnail = file.getLargeThumbnail();
        RealmThumbnail realmThumbnailLarge = realm.createObject(RealmThumbnail.class);
        realmThumbnailLarge.setId(getCorrectId(realm));
        realmThumbnailLarge.setSize(largeThumbnail.getSize());
        realmThumbnailLarge.setWidth(largeThumbnail.getWidth());
        realmThumbnailLarge.setHeight(largeThumbnail.getHeight());
        realmThumbnailLarge.setCacheId(largeThumbnail.getCacheId());

        //File info for avatar
        RealmAvatar realmAvatar = realm.where(RealmAvatar.class).equalTo(RealmAvatarFields.OWNER_ID, room.getId()).findFirst();
        if (realmAvatar == null) {
            realmAvatar = realm.createObject(RealmAvatar.class);
            realmAvatar.setOwnerId(room.getId());
            realmAvatar.setId(System.nanoTime());
        }
        realmAvatar.setFile(RealmAttachment.build(file));

        realm.close();

        return realmAvatar;
    }

    public static RealmAvatar convert(long userId, final RealmAttachment attachment) {
        Realm realm = Realm.getDefaultInstance();

        // don't put it into transaction
        RealmAvatar realmAvatar = realm.where(RealmAvatar.class).equalTo(RealmAvatarFields.OWNER_ID, userId).findFirst();
        if (realmAvatar == null) {
            realmAvatar = realm.createObject(RealmAvatar.class);
            realmAvatar.setId(attachment.getId());
            realmAvatar.setOwnerId(userId);
        }
        realmAvatar.setFile(attachment);

        realm.close();

        return realmAvatar;
    }

    public long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(long ownerId) {
        this.ownerId = ownerId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public RealmAttachment getFile() {
        return file;
    }

    public void setFile(RealmAttachment file) {
        this.file = file;
    }
}
