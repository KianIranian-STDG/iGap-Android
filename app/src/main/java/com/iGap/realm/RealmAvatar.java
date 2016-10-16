package com.iGap.realm;

import com.iGap.proto.ProtoGlobal;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;


public class RealmAvatar extends RealmObject {

    public RealmAvatar() {
    }

    public RealmAvatar(long id) {
        this.id = id;
    }

    @PrimaryKey
    private long id;
    private long ownerId;

    public long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(long ownerId) {
        this.ownerId = ownerId;
    }

    private RealmAttachment file;

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

    /**
     * use this method just for group or channel , because chat don't have avatar in room(chat avatar exist in channel info)
     *
     * @param room
     * @return
     */

    public static RealmAvatar convert(final ProtoGlobal.Room room, Realm realm) {  //TODO [Saeed Mozaffari] [2016-10-04 5:40 PM] - check this code . i guess is wrong!!! maybe overwriting realmAvatar
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
        realmThumbnailSmall.setId(System.nanoTime());
        realmThumbnailSmall.setSize(smallThumbnail.getSize());
        realmThumbnailSmall.setWidth(smallThumbnail.getWidth());
        realmThumbnailSmall.setHeight(smallThumbnail.getHeight());
        realmThumbnailSmall.setCacheId(smallThumbnail.getCacheId());

        //large thumbnail
        ProtoGlobal.Thumbnail largeThumbnail = file.getLargeThumbnail();
        RealmThumbnail realmThumbnailLarge = realm.createObject(RealmThumbnail.class);
        realmThumbnailLarge.setId(System.nanoTime());
        realmThumbnailLarge.setSize(largeThumbnail.getSize());
        realmThumbnailLarge.setWidth(largeThumbnail.getWidth());
        realmThumbnailLarge.setHeight(largeThumbnail.getHeight());
        realmThumbnailLarge.setCacheId(largeThumbnail.getCacheId());

        //File info for avatar
        RealmAvatar realmAvatar = realm.where(RealmAvatar.class).equalTo("ownerId", room.getId()).findFirst();
        if (realmAvatar == null) {
            realmAvatar = realm.createObject(RealmAvatar.class);
            realmAvatar.setOwnerId(room.getId());
            realmAvatar.setId(System.nanoTime());
        }
        realmAvatar.setFile(RealmAttachment.build(file));

        return realmAvatar;
    }

    public static RealmAvatar convert(final ProtoGlobal.Room room) {
        Realm realm = Realm.getDefaultInstance();
        final RealmAvatar[] realmAvatar = {null};

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realmAvatar[0] = convert(room, realm);
            }
        });

        realm.close();

        return realmAvatar[0];
    }

    public static RealmAvatar convert(long userId, final RealmAttachment attachment) {
        Realm realm = Realm.getDefaultInstance();

        // don't put it into transaction
        RealmAvatar realmAvatar = realm.where(RealmAvatar.class).equalTo("ownerId", userId).findFirst();
        if (realmAvatar == null) {
            realmAvatar = realm.createObject(RealmAvatar.class);
            realmAvatar.setId(attachment.getId());
            realmAvatar.setOwnerId(userId);
        }
        realmAvatar.setFile(attachment);

        realm.close();

        return realmAvatar;
    }

    public static RealmAvatar convert(ProtoGlobal.RegisteredUser user, Realm realm) {

        ProtoGlobal.File file = user.getAvatar().getFile();

        //small thumbnail
        ProtoGlobal.Thumbnail smallThumbnail = file.getSmallThumbnail();
        RealmThumbnail realmThumbnailSmall = realm.createObject(RealmThumbnail.class);
        realmThumbnailSmall.setId(System.nanoTime());
        realmThumbnailSmall.setSize(smallThumbnail.getSize());
        realmThumbnailSmall.setWidth(smallThumbnail.getWidth());
        realmThumbnailSmall.setHeight(smallThumbnail.getHeight());
        realmThumbnailSmall.setCacheId(smallThumbnail.getCacheId());

        //large thumbnail
        ProtoGlobal.Thumbnail largeThumbnail = file.getLargeThumbnail();
        RealmThumbnail realmThumbnailLarge = realm.createObject(RealmThumbnail.class);
        realmThumbnailLarge.setId(System.nanoTime());
        realmThumbnailLarge.setSize(largeThumbnail.getSize());
        realmThumbnailLarge.setWidth(largeThumbnail.getWidth());
        realmThumbnailLarge.setHeight(largeThumbnail.getHeight());
        realmThumbnailLarge.setCacheId(largeThumbnail.getCacheId());

        //File info for avatar
        RealmAvatar realmAvatar = realm.where(RealmAvatar.class).equalTo("ownerId", user.getId()).findFirst();
        if (realmAvatar == null) {
            realmAvatar = realm.createObject(RealmAvatar.class);
            realmAvatar.setOwnerId(user.getId());
            realmAvatar.setId(System.nanoTime());
        }
        realmAvatar.setFile(RealmAttachment.build(file));

        return realmAvatar;
    }
}
