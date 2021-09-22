package net.iGap.realm;

import io.realm.RealmObject;

public class RealmStoryViewInfo  extends RealmObject {
    private long id;
    private long userId;
    private long createdTime;

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
}
