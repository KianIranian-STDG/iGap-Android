package com.iGap.realm;

import com.iGap.proto.ProtoGlobal;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;


public class RealmAvatar extends RealmObject {

    @PrimaryKey
    private long id;
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

    private static RealmAvatar realmAvatar = null;

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
        RealmAvatar realmAvatar = realm.where(RealmAvatar.class).equalTo("id", room.getId()).findFirst();
        if (realmAvatar == null) {
            realmAvatar = realm.createObject(RealmAvatar.class);
            realmAvatar.setId(room.getId());
        }
        realmAvatar.setFile(RealmAttachment.build(file));

        return realmAvatar;
    }

    /**
     * use this method just for group or channel .
     *
     * @param room
     * @return
     */


    public static RealmAvatar convert(final ProtoGlobal.Room room) {
        Realm realm = Realm.getDefaultInstance();

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realmAvatar = convert(room, realm);
            }
        });

        realm.close();

        return realmAvatar;
    }
}
