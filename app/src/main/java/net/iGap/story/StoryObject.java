package net.iGap.story;

import net.iGap.module.accountManager.AccountManager;
import net.iGap.proto.ProtoGlobal;
import net.iGap.realm.RealmAttachment;
import net.iGap.realm.RealmStoryProto;
import net.iGap.structs.AttachmentObject;
import net.iGap.structs.MessageObject;

import java.util.ArrayList;
import java.util.List;

public class StoryObject {

    public String caption;
    public String fileToken;
    public ProtoGlobal.File file;
    public AttachmentObject attachmentObject;
    public long createdAt;
    public long userId;
    public long roomId;
    public long storyId;
    public long sessionId;
    public boolean isSeen;
    public boolean isVerified;
    public boolean isForRoom = false;
    public String imagePath;
    public RealmAttachment realmAttachment;
    public int status = MessageObject.STATUS_LISTENED;
    public long id;
    public String displayName;
    public int index;
    public int viewCount;
    public String profileColor;
    public List<StoryViewInfoObject> storyViewInfoObjects = new ArrayList<>();


    public static StoryObject create(ProtoGlobal.Story igapStory, int index, String displayName, String profileColor, boolean isForRoom, boolean isVerified) {
        StoryObject storyObject = new StoryObject();

        storyObject.caption = igapStory.getCaption();
        storyObject.fileToken = igapStory.getFileToken();
        storyObject.file = igapStory.getFileDetails();
        if (storyObject.file != null && storyObject.fileToken != null) {
            storyObject.status = MessageObject.STATUS_SENT;
        }
        storyObject.isForRoom = isForRoom;
        storyObject.createdAt = igapStory.getCreatedAt();
        storyObject.userId = igapStory.getUserId();
        storyObject.roomId = igapStory.getRoomId();
        storyObject.storyId = igapStory.getId();
        storyObject.isSeen = igapStory.getSeen();
        storyObject.viewCount = igapStory.getViews();
        storyObject.isVerified = isVerified;
        storyObject.index = index;
        storyObject.profileColor = profileColor;
        if (isForRoom) {
            storyObject.displayName = displayName;
        } else {
            storyObject.displayName = storyObject.userId == AccountManager.getInstance().getCurrentUser().getId() ? AccountManager.getInstance().getCurrentUser().getName() : displayName;
        }

        return storyObject;
    }

    public static StoryObject create(RealmStoryProto igapStory) {
        StoryObject storyObject = new StoryObject();

        storyObject.caption = igapStory.getCaption();
        storyObject.fileToken = igapStory.getFileToken();
        ProtoGlobal.File.Builder builder = ProtoGlobal.File.newBuilder();
        builder.setToken(igapStory.getFileToken() != null ? igapStory.getFileToken() : "");
        builder.setName(igapStory.getFile().name);
        builder.setSize(igapStory.getFile().size);
        storyObject.attachmentObject = AttachmentObject.create(igapStory.getFile());
        storyObject.status = igapStory.getStatus();
        storyObject.createdAt = igapStory.getCreatedAt();
        storyObject.userId = igapStory.getUserId();
        storyObject.storyId = igapStory.getStoryId();
        storyObject.isSeen = igapStory.isSeen();
        storyObject.roomId = igapStory.getRoomId();
        storyObject.isForRoom = igapStory.isForRoom();
        storyObject.isVerified = igapStory.isVerified();
        storyObject.id = igapStory.getId();
        storyObject.index = igapStory.getIndex();
        storyObject.viewCount = igapStory.getViewCount();
        if (storyObject.isForRoom) {
            storyObject.displayName = igapStory.getDisplayName();
        } else {
            storyObject.displayName = storyObject.userId == AccountManager.getInstance().getCurrentUser().getId() ? AccountManager.getInstance().getCurrentUser().getName() : igapStory.getDisplayName();
        }


        for (int i = 0; i < igapStory.getRealmStoryViewInfos().size(); i++) {
            storyObject.storyViewInfoObjects.add(StoryViewInfoObject.create(igapStory.getRealmStoryViewInfos().get(i)));
        }
        return storyObject;
    }

}
