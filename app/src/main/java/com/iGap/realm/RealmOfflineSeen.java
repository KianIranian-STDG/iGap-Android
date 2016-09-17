package com.iGap.realm;

import io.realm.RealmObject;

// note: realm doesn't support enum
// as a workaround, we save its toString() value
// https://github.com/realm/realm-java/issues/776
public class RealmOfflineSeen extends RealmObject {

    private long offlineSeen;

    public long getOfflineSeen() {
        return offlineSeen;
    }

    public void setOfflineSeen(long offlineSeen) {
        this.offlineSeen = offlineSeen;
    }
}
