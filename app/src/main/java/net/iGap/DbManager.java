package net.iGap;

import android.os.Looper;

import io.realm.Realm;

public class DbManager {

    private Realm realm;

    private static final DbManager ourInstance = new DbManager();

    public static DbManager getInstance() {
        return ourInstance;
    }

    private DbManager() {
    }

    public Realm getRealm() {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            throw new IllegalStateException("You must use this realm instance in ui thread.");
        }

//        if (realm == null || realm.isClosed()) {
//            realm = Realm.getDefaultInstance();
//        }

        return realm;
    }

    public void setRealm(Realm realm) {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            throw new IllegalStateException("You must set this realm instance in ui thread.");
        }
        this.realm = realm;
    }

    public void closeRealm() {
        realm.close();
    }
}
