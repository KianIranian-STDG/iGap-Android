package com.iGap.realm;

import io.realm.RealmObject;


public class RealmAvatar extends RealmObject {

    private long id;
    private RealmFiles file;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public RealmFiles getFile() {
        return file;
    }

    public void setFile(RealmFiles file) {
        this.file = file;
    }
}
