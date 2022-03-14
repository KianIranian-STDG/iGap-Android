package net.iGap.story.viewPager;

import android.graphics.Bitmap;

import net.iGap.proto.ProtoGlobal;
import net.iGap.realm.RealmAttachment;
import net.iGap.realm.RealmStoryViewInfo;
import net.iGap.story.StoryViewInfoObject;
import net.iGap.structs.AttachmentObject;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

import io.realm.RealmList;

public class Story implements Serializable {

    private String url;
    private Bitmap bitmap;
    private String txt;
    private long storyData;
    private long userId;
    private long roomId;
    private long storyId;
    private String displayName;
    private AttachmentObject attachment;
    private ProtoGlobal.File file;
    private int viewCount;
    private boolean isRoom;
    private boolean isVerified;
    private List<StoryViewInfoObject> userIdList;

    public Story(String url, Bitmap bitmap, String txt, long storyData, long userId, long roomId, String displayName, long storyId, AttachmentObject attachment, ProtoGlobal.File file, int viewCount, boolean isRoom, boolean isVerified, List<StoryViewInfoObject> userIdList) {
        this.url = url;
        this.bitmap = bitmap;
        this.txt = txt;
        this.storyData = storyData;
        this.userId = userId;
        this.roomId = roomId;
        this.storyId = storyId;
        this.attachment = attachment;
        this.file = file;
        this.viewCount = viewCount;
        this.userIdList = userIdList;
        this.displayName = displayName;
        this.isRoom = isRoom;
        this.isVerified = isVerified;
    }

    public Story() {
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
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

    public String getTxt() {
        return txt;
    }

    public void setTxt(String txt) {
        this.txt = txt;
    }

    public void setStoryData(long storyData) {
        this.storyData = storyData;
    }

    public long getStoryData() {
        return storyData;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public ProtoGlobal.File getFile() {
        return file;
    }

    public void setFile(ProtoGlobal.File file) {
        this.file = file;
    }

    public long getStoryId() {
        return storyId;
    }

    public void setStoryId(long storyId) {
        this.storyId = storyId;
    }

    public boolean isVerified() {
        return isVerified;
    }

    public void setVerified(boolean verified) {
        isVerified = verified;
    }

    public int getViewCount() {
        return viewCount;
    }

    public void setViewCount(int viewCount) {
        this.viewCount = viewCount;
    }

    public AttachmentObject getAttachment() {
        return attachment;
    }

    public void setAttachment(AttachmentObject attachment) {
        this.attachment = attachment;
    }

    public List<StoryViewInfoObject> getUserIdList() {
        return userIdList;
    }

    public void setUserIdList(List<StoryViewInfoObject> userIdList) {
        this.userIdList = userIdList;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public boolean isRoom() {
        return isRoom;
    }

    public void setRoom(boolean room) {
        isRoom = room;
    }
}
