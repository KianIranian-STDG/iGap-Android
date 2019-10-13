package net.iGap;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Looper;
import android.util.Base64;

import net.iGap.helper.HelperLog;
import net.iGap.realm.RealmMigration;

import java.io.File;
import java.security.SecureRandom;

import io.realm.CompactOnLaunchCallback;
import io.realm.Realm;
import io.realm.RealmConfiguration;

import static net.iGap.Config.REALM_SCHEMA_VERSION;

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
        this.uiRealm = Realm.getInstance(getConfiguration());
    }

    public void closeUiRealm() {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            throw new IllegalStateException("You must close realm in ui thread.");
        }
        uiRealm.removeAllChangeListeners();
        uiRealm.close();
    }

    public RealmConfiguration getConfiguration() {
        SharedPreferences sharedPreferences = G.context.getSharedPreferences("AES-256", Context.MODE_PRIVATE);
        String stringArray = sharedPreferences.getString("myByteArray", null);
        if (stringArray == null) {
            byte[] key = new byte[64];
            new SecureRandom().nextBytes(key);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            String saveThis = Base64.encodeToString(key, Base64.DEFAULT);
            editor.putString("myByteArray", saveThis);
            editor.apply();
        }

        byte[] mKey = Base64.decode(sharedPreferences.getString("myByteArray", null), Base64.DEFAULT);

        RealmConfiguration oldConfig = new RealmConfiguration.Builder().name(AccountManager.getInstance().getCurrentUser().getDbName())
                .schemaVersion(REALM_SCHEMA_VERSION)
                .compactOnLaunch()
                .migration(new RealmMigration()).build();
        RealmConfiguration newConfig;
        newConfig = new RealmConfiguration.Builder()
                .name(net.iGap.AccountManager.defaultDBName)
                .encryptionKey(mKey)
                .compactOnLaunch(new CompactOnLaunchCallback() {
                    @Override
                    public boolean shouldCompact(long totalBytes, long usedBytes) {
                        final long thresholdSize = 10 * 1024 * 1024;

                        if (totalBytes > 500 * 1024 * 1024) {
                            HelperLog.setErrorLog(new Exception("DatabaseSize=" + totalBytes + " UsedSize=" + usedBytes));
                        }

                        return (totalBytes > thresholdSize) && (((double) usedBytes / (double) totalBytes) < 0.9);
                    }
                })
                .schemaVersion(REALM_SCHEMA_VERSION)
                .migration(new RealmMigration())
                .build();

        File oldRealmFile = new File(oldConfig.getPath());
        File newRealmFile = new File(newConfig.getPath());
        if (!oldRealmFile.exists()) {
            return newConfig;
        } else {
            Realm realm = null;
            /*try {*/
                realm = Realm.getInstance(oldConfig);
                realm.writeEncryptedCopyTo(newRealmFile, mKey);
                realm.close();
                Realm.deleteRealm(oldConfig);
                return newConfig;
            /*} catch (OutOfMemoryError oom) {
                //TODO : what is that, exception in catch, realm may be null and close it
                realm.close();
                return null;
            } catch (Exception e) {
                //TODO : what is that, exception in catch, realm may be null and close it
                e.printStackTrace();
                realm.close();
                return null;
            }*/
        }
    }

    public <T> T doRealmTask(RealmTaskWithReturn<T> realmTask) {
        if (Looper.myLooper() == Looper.getMainLooper() && getUiRealm() != null && !getUiRealm().isClosed()) {
            return realmTask.doTask(getUiRealm());
        } else {
            try (Realm realm = Realm.getInstance(getConfiguration())) {
                return realmTask.doTask(realm);
            }
        }
    }

    public void doRealmTask(RealmTask realmTask) {
        if (Looper.myLooper() == Looper.getMainLooper() && getUiRealm() != null && !getUiRealm().isClosed()) {
            realmTask.doTask(getUiRealm());
        } else {
            try (Realm realm = Realm.getInstance(getConfiguration())) {
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
