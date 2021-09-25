package net.iGap.realm;

import io.realm.RealmObject;

public class RealmStoryViewInfo extends RealmObject {
    private long id;
    private long userId;
    private long createdTime;
    private String displayName;
    private String profileColor;

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(long createdTime) {
        this.createdTime = createdTime;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getProfileColor() {
        return profileColor;
    }

    public void setProfileColor(String profileColor) {
        this.profileColor = profileColor;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
}
