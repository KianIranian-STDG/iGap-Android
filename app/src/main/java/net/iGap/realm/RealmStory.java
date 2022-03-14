package net.iGap.realm;

import net.iGap.module.SUID;
import net.iGap.module.accountManager.AccountManager;
import net.iGap.module.enums.AttachmentFor;
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
    @Index
    private long userId; // userId for users and roomId for rooms
    @Index
    private long roomId;
    private boolean isSeenAll;
    private boolean isSentAll;
    private boolean isUploadedAll;
    private boolean isVerified;
    private long lastCreatedAt;
    private int indexOfSeen;
    private int orginatorValue;
    private long sessionId;
    private String displayName;
    private String profileColor;
    private RealmList<RealmStoryProto> realmStoryProtos;


    public RealmStory() {
    }

    public RealmStory(long id) {
        this.id = id;
    }

    public static RealmStory putOrUpdate(Realm realm, boolean isSeenAll, final long userId, final List<StoryObject> stories) {
        RealmStory realmStory = realm.where(RealmStory.class).equalTo("userId", userId).findFirst();
        if (realmStory == null) {
            realmStory = realm.createObject(RealmStory.class, SUID.id().get());
            realmStory.setSeenAll(false);
        }
        realmStory.setSessionId(AccountManager.getInstance().getCurrentUser().getId());
        realmStory.setUserId(userId);
        realmStory.setSeenAll(isSeenAll);
        realmStory.setDisplayName(stories.get(0).displayName);
        realmStory.setRealmStoryProtos(realm, stories);
        return realmStory;
    }

    public static RealmStory putOrUpdate(Realm realm, final long userId, boolean isSeenAll) {
        RealmStory realmStory = realm.where(RealmStory.class).equalTo("userId", userId).findFirst();
        if (realmStory == null) {
            realmStory = realm.createObject(RealmStory.class, SUID.id().get());
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

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getRoomId() {
        return roomId;
    }

    public void setRoomId(long roomId) {
        this.roomId = roomId;
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

    public int getOrginatorValue() {
        return orginatorValue;
    }

    public void setOrginatorValue(int orginatorValue) {
        this.orginatorValue = orginatorValue;
    }

    public boolean isSentAll() {
        return isSentAll;
    }

    public void setSentAll(boolean sentAll) {
        isSentAll = sentAll;
    }

    public long getSessionId() {
        return sessionId;
    }

    public void setSessionId(long sessionId) {
        this.sessionId = sessionId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public long getLastCreatedAt() {
        return lastCreatedAt;
    }

    public void setLastCreatedAt(long lastCreatedAt) {
        this.lastCreatedAt = lastCreatedAt;
    }

    public boolean isUploadedAll() {
        return isUploadedAll;
    }

    public void setUploadedAll(boolean uploadedAll) {
        isUploadedAll = uploadedAll;
    }

    public String getProfileColor() {
        return profileColor;
    }

    public boolean isVerified() {
        return isVerified;
    }

    public void setVerified(boolean verified) {
        isVerified = verified;
    }

    public void setProfileColor(String profileColor) {
        this.profileColor = profileColor;
    }

    public void setRealmStoryProtos(Realm realm, List<StoryObject> stories) {
        boolean isExist = false;
        for (StoryObject igapStory : stories) {
            RealmAttachment realmAttachment;
            RealmStoryProto storyProto;
            if (igapStory.fileToken != null) {
                storyProto = realm.where(RealmStoryProto.class).equalTo("sessionId", AccountManager.getInstance().getCurrentUser().getId()).equalTo("isForReply", false).equalTo("fileToken", igapStory.fileToken).findFirst();
            } else {
                storyProto = realm.where(RealmStoryProto.class).equalTo("sessionId", AccountManager.getInstance().getCurrentUser().getId()).equalTo("isForReply", false).equalTo("isForReply", false).equalTo("id", igapStory.id).findFirst();
            }
            if (storyProto == null) {
                storyProto = realm.createObject(RealmStoryProto.class);
                setSentAll(false);
                setUploadedAll(false);
            } else {
                isExist = true;
            }
            if (igapStory.realmAttachment != null) {
                storyProto.setFile(igapStory.realmAttachment);
            } else {
                if (!isExist) {
                    realmAttachment = RealmAttachment.build(realm, igapStory.file, AttachmentFor.AVATAR, null);
                    storyProto.setFile(realmAttachment);
                } else if (isExist && igapStory.file != null) {
                    realmAttachment = RealmAttachment.putOrUpdate(realm, SUID.id().get(), storyProto.getFile(), igapStory.file);
                    storyProto.setFile(realmAttachment);
                } else {
                    realmAttachment = RealmAttachment.build(realm, igapStory.file, AttachmentFor.AVATAR, null);
                    storyProto.setFile(realmAttachment);
                }
            }

            storyProto.setCaption(igapStory.caption);
            if (igapStory.storyId == 0) {
                storyProto.setCreatedAt(igapStory.createdAt);
            } else {
                storyProto.setCreatedAt(igapStory.createdAt * 1000L);
            }
            storyProto.setVerified(igapStory.isVerified);
            storyProto.setFileToken(igapStory.fileToken);
            storyProto.setUserId(igapStory.userId);
            storyProto.setStoryId(igapStory.storyId);
            storyProto.setSeen(igapStory.isSeen);
            storyProto.setStatus(igapStory.status);
            storyProto.setId(igapStory.id);
            storyProto.setForReply(false);
            storyProto.setDisplayName(igapStory.displayName);
            storyProto.setProfileColor(igapStory.profileColor);
            storyProto.setRoomId(igapStory.roomId);
            storyProto.setForRoom(igapStory.isForRoom);
            storyProto.setViewCount(igapStory.viewCount);
            storyProto.setSessionId(AccountManager.getInstance().getCurrentUser().getId());
            if (isExist) {
                storyProto.setIndex(storyProto.getIndex());
            } else {
                storyProto.setIndex(igapStory.index);
            }


            if (igapStory.isSeen) {
                setIndexOfSeen(stories.indexOf(igapStory));
            }

            if (getIndexOfSeen() > stories.size()) {
                setIndexOfSeen(0);
            }

            if (isExist) {
                realmStoryProtos.remove(storyProto);
            }

            realmStoryProtos.add(storyProto);

            isExist = false;
        }
        if (realm.where(RealmStoryProto.class).equalTo("userId", getUserId()).equalTo("isForReply", false).equalTo("status", MessageObject.STATUS_SENDING).findAll().size() > 0 ||
                realm.where(RealmStoryProto.class).equalTo("userId", getUserId()).equalTo("isForReply", false).equalTo("status", MessageObject.STATUS_FAILED).findAll().size() > 0) {
            setSentAll(false);
        } else {
            setSentAll(true);
        }


    }

}
