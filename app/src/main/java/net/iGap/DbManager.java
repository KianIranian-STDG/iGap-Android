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


    public <T> T doRealmTask(RealmTaskWithReturn<T> realmTask) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            return realmTask.doTask(getRealm());
        } else {
            try (Realm realm = Realm.getDefaultInstance()) {
                return realmTask.doTask(realm);
            }
        }
    }

    public void doRealmTask(RealmTask realmTask) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            realmTask.doTask(getRealm());
        } else {
            try (Realm realm = Realm.getDefaultInstance()) {
                realmTask.doTask(realm);
            }
        }
    }

    @FunctionalInterface
    public interface RealmTaskWithReturn<T> {
        T doTask(Realm realm);
    }

    @FunctionalInterface
    public interface RealmTask {
        void doTask(Realm realm);
    }

}
