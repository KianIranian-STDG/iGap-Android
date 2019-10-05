package net.iGap;

import android.os.Looper;

import io.realm.Realm;

public class DbManager {

    private Realm uiRealm;

    private static final DbManager ourInstance = new DbManager();

    public static DbManager getInstance() {
        return ourInstance;
    }

    private DbManager() {
    }

    public Realm getUiRealm() {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            throw new IllegalStateException("You must use this realm instance in ui thread.");
        }

//        if (uiRealm == null || uiRealm.isClosed()) {
//            uiRealm = Realm.getDefaultInstance();
//        }

        return uiRealm;
    }

    public void openUiRealm() {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            throw new IllegalStateException("You must open realm in ui thread.");
        }
        this.uiRealm = Realm.getDefaultInstance();
    }

    public void closeUiRealm() {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            throw new IllegalStateException("You must close realm in ui thread.");
        }
        uiRealm.close();
    }


    public <T> T doRealmTask(RealmTaskWithReturn<T> realmTask) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            return realmTask.doTask(getUiRealm());
        } else {
            try (Realm realm = Realm.getDefaultInstance()) {
                return realmTask.doTask(realm);
            }
        }
    }

    public void doRealmTask(RealmTask realmTask) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            realmTask.doTask(getUiRealm());
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
