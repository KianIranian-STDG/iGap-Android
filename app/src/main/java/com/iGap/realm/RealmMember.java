package com.iGap.realm;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class RealmMember extends RealmObject {

    @PrimaryKey private long id;

    private long peerId;
    private String role;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getPeerId() {
        return peerId;
    }

    public void setPeerId(long peerId) {
        this.peerId = peerId;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
