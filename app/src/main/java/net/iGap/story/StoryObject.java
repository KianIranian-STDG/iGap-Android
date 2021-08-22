package net.iGap.story;

import net.iGap.proto.ProtoGlobal;
import net.iGap.proto.ProtoStoryGetStories;
import net.iGap.proto.ProtoStoryUserAddNew;
import net.iGap.realm.RealmAttachment;
import net.iGap.realm.RealmStoryProto;
import net.iGap.structs.AttachmentObject;
import net.iGap.structs.MessageObject;

public class StoryObject {

    public String caption;
    public String fileToken;
    public ProtoGlobal.File file;
    public AttachmentObject attachmentObject;
    public long createdAt;
    public long userId;
    public long storyId;
    public boolean isSeen;
    public String imagePath;
    public RealmAttachment realmAttachment;
    public int status = MessageObject.STATUS_LISTENED;
    public long id;


    public static StoryObject create(ProtoGlobal.Story igapStory) {
        StoryObject storyObject = new StoryObject();

        storyObject.caption = igapStory.getCaption();
        storyObject.fileToken = igapStory.getFileToken();
        storyObject.file = igapStory.getFileDetails();
        if (storyObject.file != null && storyObject.fileToken != null) {
            storyObject.status = MessageObject.STATUS_SENT;
        }
        storyObject.createdAt = igapStory.getCreatedAt();
        storyObject.userId = igapStory.getUserId();
        storyObject.storyId = igapStory.getId();
        storyObject.isSeen = igapStory.getSeen();

        return storyObject;
    }
    public static StoryObject create(RealmStoryProto igapStory) {
        StoryObject storyObject = new StoryObject();

        storyObject.caption = igapStory.getCaption();
        storyObject.fileToken = igapStory.getFileToken();
        ProtoGlobal.File.Builder builder = ProtoGlobal.File.newBuilder();
        builder.setToken(igapStory.getFileToken());
        builder.setName(igapStory.getFile().name);
        builder.setSize(igapStory.getFile().size);
        storyObject.attachmentObject = AttachmentObject.create(igapStory.getFile());
        if (storyObject.attachmentObject != null || storyObject.file != null) {
            storyObject.status = MessageObject.STATUS_SENT;
        }
        storyObject.createdAt = igapStory.getCreatedAt();
        storyObject.userId = igapStory.getUserId();
        storyObject.storyId = igapStory.getId();
        storyObject.isSeen = igapStory.isSeen();

        return storyObject;
    }

}
