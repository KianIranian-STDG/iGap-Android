package net.iGap;

import android.os.Looper;
import android.util.Log;

import io.realm.Realm;

public class DbManager {

    private Realm uiRealm;

    private static final DbManager ourInstance = new DbManager();

    public static DbManager getInstance() {
        return ourInstance;
    }

    private DbManager() {

    }

    private Realm getUiRealm() {
        return uiRealm;
    }

    public void openUiRealm() {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            throw new IllegalStateException("You must open realm in ui thread.");
        }
        Log.wtf(this.getClass().getName(), "openUiRealm: " + AccountManager.getInstance().getCurrentUser().getRealmConfiguration().getRealmFileName());
        this.uiRealm = Realm.getInstance(AccountManager.getInstance().getCurrentUser().getRealmConfiguration());
    }

    public void closeUiRealm() {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            throw new IllegalStateException("You must close realm in ui thread.");
        }
        uiRealm.removeAllChangeListeners();
        uiRealm.close();
    }

    public void changeRealmConfiguration() {
        this.uiRealm = null;
        Log.wtf(this.getClass().getName(), "current user: " + AccountManager.getInstance().getCurrentUser().getName());
        this.uiRealm = Realm.getInstance(AccountManager.getInstance().getCurrentUser().getRealmConfiguration());
        Log.wtf(this.getClass().getName(), "changeRealmConfiguration: " + this.uiRealm.getConfiguration().getRealmFileName());
    }

    public <T> T doRealmTask(RealmTaskWithReturn<T> realmTask) {
        if (Looper.myLooper() == Looper.getMainLooper() && getUiRealm() != null && !getUiRealm().isClosed()) {
            return realmTask.doTask(getUiRealm());
        } else {
            try (Realm realm = Realm.getInstance(AccountManager.getInstance().getCurrentUser().getRealmConfiguration())) {
                return realmTask.doTask(realm);
            }
        }
    }

    public void doRealmTask(RealmTask realmTask) {
        if (Looper.myLooper() == Looper.getMainLooper() && getUiRealm() != null && !getUiRealm().isClosed()) {
            realmTask.doTask(getUiRealm());
        } else {
            try (Realm realm = Realm.getInstance(AccountManager.getInstance().getCurrentUser().getRealmConfiguration())) {
                realmTask.doTask(realm);
            }
        }
    }

    public void doRealmTask(RealmTask realmTask, long userId) {
        try (Realm realm = Realm.getInstance(AccountManager.getInstance().getUser(userId).getRealmConfiguration())) {
            realmTask.doTask(realm);
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
