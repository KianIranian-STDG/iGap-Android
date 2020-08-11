package net.iGap.module.accountManager;

import android.os.Looper;
import android.util.Log;

import net.iGap.BuildConfig;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import io.realm.Realm;

public class DbManager {

    private Realm uiRealm;

    private ThreadPoolExecutor realmLowPriorityPoolExecutor;

    private static final DbManager ourInstance = new DbManager();

    public static DbManager getInstance() {
        return ourInstance;
    }

    private DbManager() {
        realmLowPriorityPoolExecutor = new ThreadPoolExecutor(
                1,
                1,
                3,
                TimeUnit.SECONDS,
                new LinkedBlockingDeque<>());
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
        Log.wtf("bagi", "LocalOpenRealmBeforeClose=" + Realm.getLocalInstanceCount(uiRealm.getConfiguration()) + "");

        if (uiRealm != null) {
            uiRealm.removeAllChangeListeners();
            uiRealm.close();
        }
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

    /***
     Very important Not:
     All transaction using this method have low priority and may not execute in order of transaction but order of all low transaction is guaranteed.
     so all transaction that use this function will be called in order of calling.
     **/
    public void doRealmTransactionLowPriorityAsync(RealmTransaction realmTransaction) {
        realmLowPriorityPoolExecutor.execute(new Thread(() -> DbManager.getInstance().doRealmTask(realm -> {
            realm.executeTransaction(realmTransaction::doTransaction);
        })));
    }

    public void doRealmTransaction(RealmTransaction realmTransaction) {
        if (BuildConfig.DEBUG && Looper.myLooper() == Looper.getMainLooper()) {
            throw new IllegalStateException("running transaction in ui thread !!!!!!");
        }

        DbManager.getInstance().doRealmTask(realm -> {
            realm.executeTransaction(realmTransaction::doTransaction);
        });
    }

    public void doRealmTask(RealmTask realmTask, long userId) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            throw new IllegalStateException("You must call this function in non ui thread.");
        }

        try (Realm realm = Realm.getInstance(AccountManager.getInstance().getUser(userId).getRealmConfiguration())) {
            realmTask.doTask(realm);
        }
    }

    public <T> T doRealmTask(RealmTaskWithReturn<T> realmTask, long userId) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            throw new IllegalStateException("You must call this function in non ui thread.");
        }

        try (Realm realm = Realm.getInstance(AccountManager.getInstance().getUser(userId).getRealmConfiguration())) {
            return realmTask.doTask(realm);
        }
    }

    /***
     Very important Not:
     All transaction using this method have low priority and may not execute in order of transaction but order of all low transaction is guaranteed.
     so all transaction that use this function will be called in order of calling.
     **/
    public void doRealmTransactionLowPriorityAsync(RealmTransaction realmTransaction, long userId) {
        realmLowPriorityPoolExecutor.execute(new Thread(() -> DbManager.getInstance().doRealmTask(realm -> {
            realm.executeTransaction(realmTransaction::doTransaction);
        }, userId)));
    }

    public void doRealmTransaction(RealmTransaction realmTransaction, long userId) {
        if (BuildConfig.DEBUG && Looper.myLooper() == Looper.getMainLooper()) {
            throw new IllegalStateException("running transaction in ui thread !!!!!!");
        }

        DbManager.getInstance().doRealmTask(realm -> {
            realm.executeTransaction(realmTransaction::doTransaction);
        }, userId);
    }

    @FunctionalInterface
    public interface RealmTaskWithReturn<T> {
        T doTask(Realm realm);
    }

    @FunctionalInterface
    public interface RealmTask {
        void doTask(Realm realm);
    }

    @FunctionalInterface
    public interface RealmTransaction {
        void doTransaction(Realm realm);
    }

}
