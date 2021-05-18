package net.iGap.model;

import android.util.Base64;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

import net.iGap.G;
import net.iGap.helper.HelperLog;
import net.iGap.realm.RealmMigration;

import java.io.File;

import io.realm.CompactOnLaunchCallback;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.rx.RealmObservableFactory;

import static net.iGap.Config.REALM_SCHEMA_VERSION;

public class AccountUser {
    @SerializedName("id")
    private long id;

    @SerializedName("dbName")
    private String dbName;

    @SerializedName("name")
    private String name = "";

    @SerializedName("phoneNumber")
    private String phoneNumber = "";

    @SerializedName("unReadMessageCount")
    private int unReadMessageCount;

    @SerializedName("isAssigned")
    private boolean isAssigned; // flag for show add new or not

    @SerializedName("loginTime")
    private long loginTime;

    private transient RealmConfiguration realmConfiguration;

    public AccountUser(String name) {
        this.name = name;
        this.isAssigned = false;
    }

    public long getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(long loginTime) {
        this.loginTime = loginTime;
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

    @Nullable
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
                "phoneNumber: " + phoneNumber + "\n" +
                "unReadMessageCount: " + unReadMessageCount + "\n" +
                "isAssigned: " + isAssigned + "\n" +
                "loginTime: " + loginTime + "\n" +
                "db configuration: " + realmConfiguration.getRealmFileName();
    }

    private RealmConfiguration getConfiguration(String key) {
        byte[] mKey = Base64.decode(key, Base64.DEFAULT);

        Realm.init(G.context);
        RealmConfiguration oldConfig = new RealmConfiguration.Builder().name("iGapLocalDatabase.realm")
                .schemaVersion(37)
                .compactOnLaunch()
                .migration(new RealmMigration()).build();
        RealmConfiguration newConfig;
        newConfig = new RealmConfiguration.Builder()
                .name(dbName)
                .encryptionKey(mKey)
                .rxFactory(new RealmObservableFactory()) //rx java for change listener
                .compactOnLaunch(new CompactOnLaunchCallback() {
                    @Override
                    public boolean shouldCompact(long totalBytes, long usedBytes) {
                        final long thresholdSize = 10 * 1024 * 1024;

                        if (totalBytes > 500 * 1024 * 1024) {
                            HelperLog.getInstance().setErrorLog(new Exception("DatabaseSize=" + totalBytes + " UsedSize=" + usedBytes));
                        }

                        return (totalBytes > thresholdSize) && (((double) usedBytes / (double) totalBytes) < 0.9);
                    }
                })
                .schemaVersion(REALM_SCHEMA_VERSION)
                .migration(new RealmMigration())
                .build();

        File oldRealmFile = new File(oldConfig.getPath());
        File newRealmFile = new File(newConfig.getPath());
        if (oldRealmFile.exists()) {
            Realm realm = null;
            try {
                realm = Realm.getInstance(oldConfig);
                realm.writeEncryptedCopyTo(newRealmFile, mKey);
            } catch (OutOfMemoryError ignored) {
            } catch (Exception ignored) {
            }

            try {
                if (realm != null)
                    realm.close();
            } catch (Exception ignored) {
            }

            Realm.deleteRealm(oldConfig);
        }

        return newConfig;
    }

    public void clearData() {
        id = 0;
        name = "";
        phoneNumber = "";
        unReadMessageCount = 0;
        isAssigned = false;
        loginTime = 0;
    }
}
