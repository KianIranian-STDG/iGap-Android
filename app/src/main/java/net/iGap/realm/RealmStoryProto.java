package net.iGap.realm;

import net.iGap.module.enums.AttachmentFor;
import net.iGap.proto.ProtoGlobal;

import io.realm.Realm;
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

    public static RealmStoryProto putOrUpdate(Realm realm, ProtoGlobal.RoomMessage roomMessage) {
        RealmStoryProto realmStory = realm.where(RealmStoryProto.class).equalTo("storyId", roomMessage.getStory().getStory().getId()).findFirst();
        ProtoGlobal.Story story = roomMessage.getStory().getStory();
        if (realmStory == null) {
            realmStory = realm.createObject(RealmStoryProto.class);
        }

        realmStory.setCaption(story.getCaption());
        realmStory.setCreatedAt(story.getCreatedAt() * 1000);
        realmStory.setFile(RealmAttachment.build(realm,story.getFileDetails(), AttachmentFor.MESSAGE_ATTACHMENT, ProtoGlobal.RoomMessageType.STORY));
        realmStory.setSeen(story.getSeen());
        realmStory.setFileToken(story.getFileToken());
        realmStory.setStoryId(story.getId());
        realmStory.setUserId(story.getUserId());
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
}
