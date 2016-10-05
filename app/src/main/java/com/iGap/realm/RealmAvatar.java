package com.iGap.realm;

import com.iGap.proto.ProtoGlobal;

import io.realm.Realm;
import io.realm.RealmObject;


public class RealmAvatar extends RealmObject {

    private long id;
    private RealmFileAvatar file;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public RealmFileAvatar getFile() {
        return file;
    }

    public void setFile(RealmFileAvatar file) {
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
        realmThumbnailSmall.setSize(smallThumbnail.getSize());
        realmThumbnailSmall.setWidth(smallThumbnail.getWidth());
        realmThumbnailSmall.setHeight(smallThumbnail.getHeight());
        realmThumbnailSmall.setCacheId(smallThumbnail.getCacheId());

        //large thumbnail
        ProtoGlobal.Thumbnail largeThumbnail = file.getLargeThumbnail();
        RealmThumbnail realmThumbnailLarge = realm.createObject(RealmThumbnail.class);
        realmThumbnailLarge.setSize(largeThumbnail.getSize());
        realmThumbnailLarge.setWidth(largeThumbnail.getWidth());
        realmThumbnailLarge.setHeight(largeThumbnail.getHeight());
        realmThumbnailLarge.setCacheId(largeThumbnail.getCacheId());

        //File info for avatar
        RealmFileAvatar realmFileAvatar = realm.createObject(RealmFileAvatar.class);
        realmFileAvatar.setToken(file.getToken());
        realmFileAvatar.setName(file.getName());
        realmFileAvatar.setSize(file.getSize());
        realmFileAvatar.setLargeThumbnail(realmThumbnailLarge);
        realmFileAvatar.setSmallThumbnail(realmThumbnailSmall);
        realmFileAvatar.setWidth(file.getWidth());
        realmFileAvatar.setHeight(file.getHeight());
        realmFileAvatar.setDuration(file.getDuration());
        realmFileAvatar.setCatchId(file.getCacheId());

        //set info in avatar
        realmAvatar = realm.createObject(RealmAvatar.class);
        realmAvatar.setId(room.getId());
        realmAvatar.setFile(realmFileAvatar);

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
