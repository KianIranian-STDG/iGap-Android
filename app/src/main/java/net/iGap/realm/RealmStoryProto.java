package net.iGap.realm;

import net.iGap.module.enums.AttachmentFor;
import net.iGap.proto.ProtoGlobal;
import net.iGap.proto.ProtoStoryGetOwnStoryViews;

import java.util.HashMap;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmObject;

public class RealmStoryProto extends RealmObject {

    private String caption;
    private String fileToken;
    private String imagePath;
    private RealmAttachment file;
    private long createdAt;
    private long userId;
    private long storyId;
    private long id;
    private boolean isSeen;
    private int viewCount;
    private int status;
    private int index;
    private boolean isForReply;
    private String displayName;
    private RealmList<RealmStoryViewInfo> realmStoryViewInfos;

    public static RealmStoryProto putOrUpdate(Realm realm, ProtoGlobal.RoomMessage roomMessage) {
        RealmStoryProto realmStory = realm.where(RealmStoryProto.class).equalTo("storyId", roomMessage.getStory().getStory().getId()).findFirst();
        ProtoGlobal.Story story = roomMessage.getStory().getStory();
        if (realmStory == null) {
            realmStory = realm.createObject(RealmStoryProto.class);
        }

        realmStory.setCaption(story.getCaption());
        realmStory.setCreatedAt(story.getCreatedAt() * 1000L);
        realmStory.setFile(RealmAttachment.build(realm, story.getFileDetails(), AttachmentFor.MESSAGE_ATTACHMENT, ProtoGlobal.RoomMessageType.STORY));
        realmStory.setSeen(story.getSeen());
        realmStory.setFileToken(story.getFileToken());
        realmStory.setStoryId(story.getId());
        realmStory.setUserId(story.getUserId());
        realmStory.setForReply(true);
        return realmStory;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public RealmAttachment getFile() {
        return file;
    }

    public void setFile(RealmAttachment file) {
        this.file = file;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public String getFileToken() {
        return fileToken;
    }

    public void setFileToken(String fileToken) {
        this.fileToken = fileToken;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getStoryId() {
        return storyId;
    }

    public void setStoryId(long storyId) {
        this.storyId = storyId;
    }

    public boolean isSeen() {
        return isSeen;
    }

    public void setSeen(boolean seen) {
        isSeen = seen;
    }

    public int getViewCount() {
        return viewCount;
    }

    public void setViewCount(int viewCount) {
        this.viewCount = viewCount;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public boolean isForReply() {
        return isForReply;
    }

    public void setForReply(boolean forReply) {
        isForReply = forReply;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public RealmList<RealmStoryViewInfo> getRealmStoryViewInfos() {
        return realmStoryViewInfos;
    }

    public void setRealmStoryViewInfos(Realm realm, ProtoStoryGetOwnStoryViews.GroupedViews groupedViews) {
        boolean isExist = false;
        for (int j = 0; j < groupedViews.getStoryViewsList().size(); j++) {
            RealmStoryViewInfo realmStoryViewInfo;
            realmStoryViewInfo = realm.where(RealmStoryViewInfo.class).equalTo("userId", groupedViews.getStoryViewsList().get(j).getUserId()).findFirst();
            if (realmStoryViewInfo == null) {
                realmStoryViewInfo = realm.createObject(RealmStoryViewInfo.class);
            } else {
                isExist = true;
            }
            realmStoryViewInfo.setId(groupedViews.getStoryId());
            realmStoryViewInfo.setUserId(groupedViews.getStoryViewsList().get(j).getUserId());
            realmStoryViewInfo.setCreatedTime(groupedViews.getStoryViewsList().get(j).getViewedAt());
            if (isExist) {
                realmStoryViewInfos.remove(realmStoryViewInfo);
            }
            realmStoryViewInfos.add(realmStoryViewInfo);
            isExist = false;
        }


    }

    public void setRealmStoryViewInfos(Realm realm, RealmStoryViewInfo storyViewInfo) {
        boolean isExist = false;
        RealmStoryViewInfo realmStoryViewInfo;
        realmStoryViewInfo = realm.where(RealmStoryViewInfo.class).equalTo("userId", storyViewInfo.getUserId()).findFirst();
        if (realmStoryViewInfo == null) {
            realmStoryViewInfo = realm.createObject(RealmStoryViewInfo.class);
        } else {
            isExist = true;
        }
        realmStoryViewInfo.setId(storyViewInfo.getId());
        realmStoryViewInfo.setUserId(storyViewInfo.getUserId());
        realmStoryViewInfo.setCreatedTime(storyViewInfo.getCreatedTime());
        if (isExist) {
            realmStoryViewInfos.remove(realmStoryViewInfo);
        }
        realmStoryViewInfos.add(realmStoryViewInfo);
        isExist = false;
    }
}
