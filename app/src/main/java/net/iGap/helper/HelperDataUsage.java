package net.iGap.helper;

import net.iGap.module.accountManager.DbManager;
import net.iGap.proto.ProtoGlobal;
import net.iGap.realm.RealmDataUsage;

import io.realm.RealmResults;

public class HelperDataUsage {

    public static void increaseDownloadFiles(ProtoGlobal.RoomMessageType type) {
        ProtoGlobal.RoomMessageType finalType = getDataUsageType(type);
        DbManager.getInstance().doRealmTransactionLowPriorityAsync(realm -> {
            RealmDataUsage realmDataUsage = realm.where(RealmDataUsage.class)
                    .equalTo("connectivityType", HelperCheckInternetConnection.isConnectivityWifi())
                    .equalTo("type", finalType.toString())
                    .findFirst();
            if (realmDataUsage != null) {
                realmDataUsage.setNumDownloadedFile(realmDataUsage.getNumDownloadedFile() + 1);
            }
        });
    }

    public static void increaseUploadFiles(ProtoGlobal.RoomMessageType type) {
        ProtoGlobal.RoomMessageType finalType = getDataUsageType(type);
        DbManager.getInstance().doRealmTransactionLowPriorityAsync(realm -> {
            RealmDataUsage realmDataUsage = realm.where(RealmDataUsage.class)
                    .equalTo("connectivityType", HelperCheckInternetConnection.isConnectivityWifi())
                    .equalTo("type", finalType.toString())
                    .findFirst();
            if (realmDataUsage != null) {
                realmDataUsage.setNumUploadedFiles(realmDataUsage.getNumUploadedFiles() + 1);
            }
        });
    }

    public static void progressDownload(long downloadByte, ProtoGlobal.RoomMessageType type) {
        ProtoGlobal.RoomMessageType finalType = getDataUsageType(type);
        DbManager.getInstance().doRealmTransactionLowPriorityAsync(realm -> {
            RealmDataUsage realmDataUsage = realm.where(RealmDataUsage.class)
                    .equalTo("connectivityType", HelperCheckInternetConnection.isConnectivityWifi())
                    .equalTo("type", finalType.toString())
                    .findFirst();
            if (realmDataUsage != null) {
                realmDataUsage.setDownloadSize(realmDataUsage.getDownloadSize() + downloadByte);
            }
        });
    }


    public static void progressUpload(long uploadByte, ProtoGlobal.RoomMessageType type) {
        ProtoGlobal.RoomMessageType finalType = getDataUsageType(type);
        DbManager.getInstance().doRealmTransactionLowPriorityAsync(realm -> {
            RealmDataUsage realmDataUsage = realm.where(RealmDataUsage.class)
                    .equalTo("connectivityType", HelperCheckInternetConnection.isConnectivityWifi())
                    .equalTo("type", finalType.toString())
                    .findFirst();
            if (realmDataUsage != null) {
                realmDataUsage.setUploadSize(realmDataUsage.getUploadSize() + uploadByte);
            }
        });
    }

    public static void initializeRealmDataUsage() {
        String[] typeList = {"IMAGE", "VIDEO", "AUDIO", "FILE", "UNRECOGNIZED"};
        DbManager.getInstance().doRealmTransactionLowPriorityAsync(realm -> {
            for (String s : typeList) {
                RealmDataUsage realmDataUsage = realm.createObject(RealmDataUsage.class);
                realmDataUsage.setNumUploadedFiles(0);
                realmDataUsage.setNumDownloadedFile(0);
                realmDataUsage.setConnectivityType(false);
                realmDataUsage.setUploadSize(0);
                realmDataUsage.setDownloadSize(0);
                realmDataUsage.setType(s);

                RealmDataUsage realmDataUsage2 = realm.createObject(RealmDataUsage.class);
                realmDataUsage2.setNumUploadedFiles(0);
                realmDataUsage2.setNumDownloadedFile(0);
                realmDataUsage2.setConnectivityType(true);
                realmDataUsage2.setUploadSize(0);
                realmDataUsage2.setDownloadSize(0);
                realmDataUsage2.setType(s);
            }
        });
    }

    public static void clearUsageRealm(boolean connectivityType) {
        DbManager.getInstance().doRealmTransaction(realm -> {
            RealmResults<RealmDataUsage> wifiRealmList = realm.where(RealmDataUsage.class).equalTo("connectivityType", connectivityType).findAll();
            for (RealmDataUsage usage : wifiRealmList) {
                usage.setDownloadSize(0);
                usage.setUploadSize(0);
                usage.setNumDownloadedFile(0);
                usage.setNumUploadedFiles(0);
            }
        });
    }

    private static ProtoGlobal.RoomMessageType getDataUsageType(ProtoGlobal.RoomMessageType type) {
        switch (type) {
            case VIDEO_TEXT:
            case VIDEO:
                return ProtoGlobal.RoomMessageType.VIDEO;
            case IMAGE_TEXT:
            case IMAGE:
                return ProtoGlobal.RoomMessageType.IMAGE;
            case FILE_TEXT:
            case FILE:
            case STICKER:
                return ProtoGlobal.RoomMessageType.FILE;
            case AUDIO_TEXT:
            case VOICE:
            case AUDIO:
                return ProtoGlobal.RoomMessageType.AUDIO;
            default:
                return ProtoGlobal.RoomMessageType.UNRECOGNIZED;
        }
    }

}
