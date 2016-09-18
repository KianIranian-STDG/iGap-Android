package com.iGap.realm;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

// note: realm doesn't support enum
// as a workaround, we save its toString() value
// https://github.com/realm/realm-java/issues/776
public class RealmOfflineSeen extends RealmObject {

    @PrimaryKey
    private long id;
    private long offlineSeen;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getOfflineSeen() {
        return offlineSeen;
    }

    public void setOfflineSeen(long offlineSeen) {
        this.offlineSeen = offlineSeen;
    }
}
