package net.iGap.module.accountManager;

import android.os.Looper;
import android.os.Process;

import net.iGap.helper.FileLog;

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

    private int lastProcessId;

    private DbManager() {
        realmLowPriorityPoolExecutor = new ThreadPoolExecutor(
                1,
                1,
                3,
                TimeUnit.SECONDS,
                new LinkedBlockingDeque<>());

        lastProcessId = Process.myPid();

        FileLog.e("DbManager PId -> " + lastProcessId);
    }

    private Realm getUiRealm() {
        return uiRealm;
    }

    public void openUiRealm() {
        printProcessId();
        if (Looper.myLooper() != Looper.getMainLooper()) {
            throw new IllegalStateException("You must open realm in ui thread.");
        }
        FileLog.i("DbManager " + "openUiRealm: databaseName -> " + AccountManager.getInstance().getCurrentUser().getRealmConfiguration().getRealmFileName() + " userId -> " + AccountManager.getInstance().getCurrentUser().getId());
        this.uiRealm = Realm.getInstance(AccountManager.getInstance().getCurrentUser().getRealmConfiguration());
    }

    public void closeUiRealm() {
        printProcessId();
        if (Looper.myLooper() != Looper.getMainLooper()) {
            throw new IllegalStateException("You must close realm in ui thread.");
        }
        FileLog.i("DbManager " + "closeUiRealm: databaseName -> " + AccountManager.getInstance().getCurrentUser().getRealmConfiguration().getRealmFileName() + " userId -> " + AccountManager.getInstance().getCurrentUser().getId());

        if (uiRealm != null) {
            uiRealm.removeAllChangeListeners();
            uiRealm.close();
        }
    }

    public <T> T doRealmTask(RealmTaskWithReturn<T> realmTask) {
        printProcessId();
        if (Looper.myLooper() == Looper.getMainLooper() && getUiRealm() != null && !getUiRealm().isClosed()) {
            return realmTask.doTask(getUiRealm());
        } else {
            try (Realm realm = Realm.getInstance(AccountManager.getInstance().getCurrentUser().getRealmConfiguration())) {
                return realmTask.doTask(realm);
            }
        }
    }

    public void doRealmTask(RealmTask realmTask) {
        printProcessId();
        if (Looper.myLooper() == Looper.getMainLooper() && getUiRealm() != null && !getUiRealm().isClosed()) {
            realmTask.doTask(getUiRealm());
        } else {
            try (Realm realm = Realm.getInstance(AccountManager.getInstance().getCurrentUser().getRealmConfiguration())) {
                realmTask.doTask(realm);
            }
        }
    }

    public Realm getRealm() {
        return Realm.getInstance(AccountManager.getInstance().getCurrentUser().getRealmConfiguration());
    }

    /***
     Very important Not:
     All transaction using this method have low priority and may not execute in order of transaction but order of all low transaction is guaranteed.
     so all transaction that use this function will be called in order of calling.
     **/
    public void doRealmTransactionLowPriorityAsync(RealmTransaction realmTransaction) {
        realmLowPriorityPoolExecutor.execute(new Thread(() -> DbManager.getInstance().doRealmTask(realm -> {
            printProcessId();
            realm.executeTransaction(realmTransaction::doTransaction);
        })));
    }

    public void doRealmTransaction(RealmTransaction realmTransaction) {
        DbManager.getInstance().doRealmTask(realm -> {
            printProcessId();
            realm.executeTransaction(realmTransaction::doTransaction);
        });
    }

    public void doRealmTask(RealmTask realmTask, long userId) {
        try (Realm realm = Realm.getInstance(AccountManager.getInstance().getUser(userId).getRealmConfiguration())) {
            printProcessId();
            realmTask.doTask(realm);
        }
    }

    public <T> T doRealmTask(RealmTaskWithReturn<T> realmTask, long userId) {
        try (Realm realm = Realm.getInstance(AccountManager.getInstance().getUser(userId).getRealmConfiguration())) {
            printProcessId();
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
        DbManager.getInstance().doRealmTask(realm -> {
            printProcessId();
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

    private void printProcessId() {
        int temp = Process.myPid();

        if (lastProcessId != temp) {
            lastProcessId = temp;
            FileLog.i("DbManager ", "PId -> " + lastProcessId);
        }
    }
}
