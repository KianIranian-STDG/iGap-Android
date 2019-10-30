package net.iGap.helper;

import net.iGap.DbManager;
import net.iGap.proto.ProtoGlobal;
import net.iGap.realm.RealmDataUsage;
import net.iGap.realm.RealmDataUsageFields;

import io.realm.Realm;
import io.realm.RealmResults;

public class HelperDataUsage {

    public static void increaseDownloadFiles(ProtoGlobal.RoomMessageType type) {
        new Thread(() -> {
            ProtoGlobal.RoomMessageType finalType = getDataUsageType(type);
            DbManager.getInstance().doRealmTask(realm -> {
                realm.executeTransaction(realm1 -> {
                    RealmDataUsage realmDataUsage = realm1.where(RealmDataUsage.class)
                            .equalTo(RealmDataUsageFields.CONNECTIVITY_TYPE, HelperCheckInternetConnection.isConnectivityWifi())
                            .equalTo(RealmDataUsageFields.TYPE, finalType.toString())
                            .findFirst();
                    if (realmDataUsage != null) {
                        realmDataUsage.setNumDownloadedFile(realmDataUsage.getNumDownloadedFile() + 1);
                    }
                });
            });
        }).run();

    }

    public static void increaseUploadFiles(ProtoGlobal.RoomMessageType type) {
        new Thread(() -> {
            ProtoGlobal.RoomMessageType finalType = getDataUsageType(type);
            DbManager.getInstance().doRealmTask(realm -> {
                realm.executeTransaction(realm1 -> {
                    RealmDataUsage realmDataUsage = realm1.where(RealmDataUsage.class)
                            .equalTo(RealmDataUsageFields.CONNECTIVITY_TYPE, HelperCheckInternetConnection.isConnectivityWifi())
                            .equalTo(RealmDataUsageFields.TYPE, finalType.toString())
                            .findFirst();
                    if (realmDataUsage != null) {
                        realmDataUsage.setNumUploadedFiles(realmDataUsage.getNumUploadedFiles() + 1);
                    }
                });
            });
        }).run();

    }

    public static void progressDownload(long downloadByte, ProtoGlobal.RoomMessageType type) {
        new Thread(() -> {
            ProtoGlobal.RoomMessageType finalType = getDataUsageType(type);
            DbManager.getInstance().doRealmTask(realm -> {
                realm.executeTransaction(realm1 -> {
                    RealmDataUsage realmDataUsage = realm1.where(RealmDataUsage.class)
                            .equalTo(RealmDataUsageFields.CONNECTIVITY_TYPE, HelperCheckInternetConnection.isConnectivityWifi())
                            .equalTo(RealmDataUsageFields.TYPE, finalType.toString())
                            .findFirst();
                    if (realmDataUsage != null) {
                        realmDataUsage.setDownloadSize(realmDataUsage.getDownloadSize() + downloadByte);
                    }
                });
            });
        }).run();

    }


    public static void progressUpload(long uploadByte, ProtoGlobal.RoomMessageType type) {
        new Thread(() -> {
            ProtoGlobal.RoomMessageType finalType = getDataUsageType(type);
            DbManager.getInstance().doRealmTask(realm -> {
                realm.executeTransaction(realm1 -> {
                    RealmDataUsage realmDataUsage = realm1.where(RealmDataUsage.class)
                            .equalTo(RealmDataUsageFields.CONNECTIVITY_TYPE, HelperCheckInternetConnection.isConnectivityWifi())
                            .equalTo(RealmDataUsageFields.TYPE, finalType.toString())
                            .findFirst();
                    if (realmDataUsage != null) {
                        realmDataUsage.setUploadSize(realmDataUsage.getUploadSize() + uploadByte);
                    }
                });
            });
        }).run();

    }

    public static void initializeRealmDataUsage() {
        new Thread(() -> {
            String[] typeList = {"IMAGE", "VIDEO", "AUDIO", "FILE", "UNRECOGNIZED"};
            DbManager.getInstance().doRealmTask(realm -> {
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
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
                    }
                });
            });
        }).run();
    }

    public static void clearUsageRealm(boolean connectivityType) {
        DbManager.getInstance().doRealmTask(realm -> {
            RealmResults<RealmDataUsage> wifiRealmList = realm.where(RealmDataUsage.class).equalTo(RealmDataUsageFields.CONNECTIVITY_TYPE, connectivityType).findAll();
            for (RealmDataUsage usage : wifiRealmList) {
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        usage.setDownloadSize(0);
                        usage.setUploadSize(0);
                        usage.setNumDownloadedFile(0);
                        usage.setNumUploadedFiles(0);
                    }
                });
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
