package net.iGap.realm;

import net.iGap.module.enums.AttachmentFor;
import net.iGap.proto.ProtoStoryGetStories;
import net.iGap.story.StoryObject;
import net.iGap.structs.MessageObject;

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
    private boolean isSeenAll;
    private boolean isSentAll;
    private boolean isUploadedAll;
    private int indexOfSeen;
    private RealmList<RealmStoryProto> realmStoryProtos;


    public RealmStory() {
    }

    public RealmStory(long id) {
        this.id = id;
    }

    public static RealmStory putOrUpdate(Realm realm, boolean isSeenAll, final long userId, final List<StoryObject> stories) {
        RealmStory realmStory = realm.where(RealmStory.class).equalTo("id", userId).findFirst();
        if (realmStory == null) {
            realmStory = realm.createObject(RealmStory.class, userId);
            realmStory.setSeenAll(false);
        }
        realmStory.setUserId(userId);
        realmStory.setSeenAll(isSeenAll);
        realmStory.setRealmStoryProtos(realm, stories);
        return realmStory;
    }

    public static RealmStory putOrUpdate(Realm realm, final long userId, boolean isSeenAll) {
        RealmStory realmStory = realm.where(RealmStory.class).equalTo("id", userId).findFirst();
        if (realmStory == null) {
            realmStory = realm.createObject(RealmStory.class, userId);
        }
        realmStory.setSeenAll(isSeenAll);
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

    public boolean isSeenAll() {
        return isSeenAll;
    }

    public void setSeenAll(boolean seenAll) {
        isSeenAll = seenAll;
    }

    public int getIndexOfSeen() {
        return indexOfSeen;
    }

    public void setIndexOfSeen(int indexOfSeen) {
        this.indexOfSeen = indexOfSeen;
    }

    public boolean isSentAll() {
        return isSentAll;
    }

    public void setSentAll(boolean sentAll) {
        isSentAll = sentAll;
    }

    public boolean isUploadedAll() {
        return isUploadedAll;
    }

    public void setUploadedAll(boolean uploadedAll) {
        isUploadedAll = uploadedAll;
    }

    public void setRealmStoryProtos(Realm realm, List<StoryObject> stories) {
        boolean isExist = false;
        for (StoryObject igapStory : stories) {
            RealmAttachment realmAttachment;
            RealmStoryProto storyProto;
            if (igapStory.fileToken != null) {
                storyProto = realm.where(RealmStoryProto.class).equalTo("fileToken", igapStory.fileToken).findFirst();
            } else {
                storyProto = realm.where(RealmStoryProto.class).equalTo("id", igapStory.id).findFirst();
            }
            if (storyProto == null) {
                storyProto = realm.createObject(RealmStoryProto.class);
                setSeenAll(false);
                setSentAll(false);
                setUploadedAll(false);
            } else {
                isExist = true;
            }
            if (igapStory.file != null && igapStory.fileToken != null) {
                realmAttachment = RealmAttachment.build(realm, igapStory.file, AttachmentFor.AVATAR, null);
                storyProto.setFile(realmAttachment);
            } else {
                storyProto.setFile(igapStory.realmAttachment);
            }

            storyProto.setCaption(igapStory.caption);
            storyProto.setCreatedAt(igapStory.createdAt * 1000L);
            storyProto.setFileToken(igapStory.fileToken);
            storyProto.setUserId(igapStory.userId);
            storyProto.setStoryId(igapStory.storyId);
            storyProto.setSeen(igapStory.isSeen);
//            storyProto.setImagePath(igapStory.imagePath);
            storyProto.setStatus(igapStory.status);
            storyProto.setId(igapStory.id);


            if (igapStory.isSeen) {
                setIndexOfSeen(stories.indexOf(igapStory));
            }

            if (isExist) {
                realmStoryProtos.remove(storyProto);
            }
            realmStoryProtos.add(storyProto);
            isExist = false;
        }
        if (realm.where(RealmStoryProto.class).equalTo("userId", getUserId()).equalTo("status", MessageObject.STATUS_SENDING).findAll().size() > 0 ||
                realm.where(RealmStoryProto.class).equalTo("userId", getUserId()).equalTo("status", MessageObject.STATUS_FAILED).findAll().size() > 0) {
            setSentAll(false);
        } else {
            setSentAll(true);
        }


    }

}
