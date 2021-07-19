package net.iGap.story.viewPager;

import android.graphics.Bitmap;

import net.iGap.proto.ProtoGlobal;
import net.iGap.realm.RealmAttachment;

import java.io.Serializable;

public class Story implements Serializable {

    private String url;
    private Bitmap bitmap;
    private String txt;
    private long storyData;
    private long userId;
    private long storyId;
    private RealmAttachment attachment;
    private ProtoGlobal.File file;


    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public RealmAttachment getAttachment() {
        return attachment;
    }

    public void setAttachment(RealmAttachment attachment) {
        this.attachment = attachment;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
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
}
