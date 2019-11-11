package net.iGap.model;

import android.util.Base64;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import net.iGap.helper.HelperLog;
import net.iGap.realm.RealmMigration;

import java.io.File;

import io.realm.CompactOnLaunchCallback;
import io.realm.Realm;
import io.realm.RealmConfiguration;

import static net.iGap.Config.REALM_SCHEMA_VERSION;

public class AccountUser {
    //ToDo: should be review and change and remove not use item
    private long id;
    private String dbName;
    private String name;
    private String phoneNumber;
    private int unReadMessageCount;
    private boolean isAssigned; // flag for show add new or not
    private transient RealmConfiguration realmConfiguration;

    public AccountUser(long id) {
        this.id = id;
    }

    public AccountUser(long id, String dbName, String name, String phoneNumber, int unReadMessageCount, boolean isAssigned) {
        this.id = id;
        this.dbName = dbName;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.unReadMessageCount = unReadMessageCount;
        this.isAssigned = isAssigned;
    }

    public AccountUser(boolean isAddNew, String name) {
        this.name = name;
        this.isAssigned = isAddNew;
    }

    public String getDbName() {
        return dbName;
    }

    public String getName() {
        return name;
    }

    public int getUnReadMessageCount() {
        return unReadMessageCount;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUnReadMessageCount(int unReadMessageCount) {
        this.unReadMessageCount = unReadMessageCount;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public boolean isAssigned() {
        return isAssigned;
    }

    public void setAssigned(boolean assigned) {
        isAssigned = assigned;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public RealmConfiguration getRealmConfiguration() {
        return realmConfiguration;
    }

    public void setRealmConfiguration(String key) {
        this.realmConfiguration = getConfiguration(key);
    }

    public void setRealmConfiguration(RealmConfiguration realmConfiguration) {
        this.realmConfiguration = realmConfiguration;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj instanceof AccountUser) {
            return id == ((AccountUser) obj).getId();
        }
        return super.equals(obj);
    }

    @NonNull
    @Override
    public String toString() {
        return "id: " + id + "\n" +
                "dbName: " + dbName + "\n" +
                "name: " + name + "\n" +
                "unReadMessageCount: " + unReadMessageCount + "\n" +
                "isAssigned: " + isAssigned + "\n" +
                "db configuration: " + realmConfiguration.getRealmFileName();
    }

    private RealmConfiguration getConfiguration(String key) {
        byte[] mKey = Base64.decode(key, Base64.DEFAULT);

        RealmConfiguration oldConfig = new RealmConfiguration.Builder().name("iGapLocalDatabase.realm")
                .schemaVersion(REALM_SCHEMA_VERSION)
                .compactOnLaunch()
                .migration(new RealmMigration()).build();
        RealmConfiguration newConfig;
        newConfig = new RealmConfiguration.Builder()
                .name(dbName)
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
}
