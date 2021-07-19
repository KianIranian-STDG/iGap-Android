package net.iGap.realm;

import net.iGap.module.enums.AttachmentFor;
import net.iGap.proto.ProtoStoryGetStories;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.Index;
import io.realm.annotations.PrimaryKey;

public class RealmStory extends RealmObject {
    @PrimaryKey
    private long id;
    private long uid; // id for sorting avatars
    @Index
    private long userId; // userId for users and roomId for rooms
    private RealmList<RealmStoryProto> realmStoryProtos;

    public RealmStory() {
    }

    public RealmStory(long id) {
        this.id = id;
    }

    public static RealmStory putOrUpdate(Realm realm, final long userId, final List<ProtoStoryGetStories.IgapStory> stories) {
        RealmStory realmStory = realm.where(RealmStory.class).equalTo("id", userId).findFirst();
        if (realmStory == null) {
            realmStory = realm.createObject(RealmStory.class, userId);
        }
        realmStory.setUserId(userId);
        realmStory.setRealmStoryProtos(realm, stories);
        return realmStory;
    }


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }


    public RealmList<RealmStoryProto> getRealmStoryProtos() {
        return realmStoryProtos;
    }

    public void setRealmStoryProtos(Realm realm, List<ProtoStoryGetStories.IgapStory> stories) {
        boolean isExist = false;
        for (ProtoStoryGetStories.IgapStory igapStory : stories) {
            RealmAttachment realmAttachment;
            RealmStoryProto storyProto = realm.where(RealmStoryProto.class).equalTo("fileToken", igapStory.getFileToken()).findFirst();
            if (storyProto == null) {
                storyProto = realm.createObject(RealmStoryProto.class);
            } else {
                isExist = true;
            }
            realmAttachment = RealmAttachment.build(realm, igapStory.getFileDetails(), AttachmentFor.AVATAR, null);
            storyProto.setFile(realmAttachment);
            storyProto.setCaption(igapStory.getCaption());
            storyProto.setCreatedAt(igapStory.getCreatedAt() * 1000L);
            storyProto.setFileToken(igapStory.getFileToken());
            storyProto.setUserId(igapStory.getUserId());
            storyProto.setStoryId(igapStory.getId());
            if (!isExist) {
                realmStoryProtos.add(storyProto);
            } else {
                realmStoryProtos.remove(storyProto);
                realmStoryProtos.add(storyProto);
            }
            isExist = false;
        }
    }

}
