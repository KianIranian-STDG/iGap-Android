package com.iGap.realm;

import io.realm.RealmObject;

// note: realm doesn't support enum
// as a workaround, we save its toString() value
// https://github.com/realm/realm-java/issues/776
public class RealmOfflineDelete extends RealmObject {

    private long offlineDelete;

    public long getOfflineDelete() {
        return offlineDelete;
    }

    public void setOfflineDelete(long offlineDelete) {
        this.offlineDelete = offlineDelete;
    }
}
