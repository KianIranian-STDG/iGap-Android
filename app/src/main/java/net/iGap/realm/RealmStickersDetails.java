package net.iGap.realm;

import android.util.Log;

import net.iGap.G;
import net.iGap.helper.emoji.HelperDownloadSticker;
import net.iGap.interfaces.OnDownload;
import net.iGap.interfaces.OnStickerDownloaded;
import net.iGap.module.AndroidUtils;
import net.iGap.proto.ProtoFileDownload;
import net.iGap.proto.ProtoGlobal;
import net.iGap.request.RequestFileDownload;

import java.io.IOException;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class RealmStickersDetails extends RealmObject {

    private long id;
    private String st_id;
    private long refId;
    private String name;
    private String token;
    private String uri = "";
    private String fileName;
    private long fileSize;
    private int sort;
    private String groupId;


    public static RealmStickersDetails put(String st_id, long refId, String name, String avatarToken, String uri, long avatarSize, String avatarName, int sort, String groupId) {
        Realm realm = Realm.getDefaultInstance();

        RealmStickersDetails realmStickersDetails = realm.where(RealmStickersDetails.class).equalTo("token", avatarToken).findFirst();
        if (realmStickersDetails == null) {
            realmStickersDetails = realm.createObject(RealmStickersDetails.class);

            realmStickersDetails.setSt_id(st_id);
            realmStickersDetails.setRefId(refId);
            realmStickersDetails.setName(name);
            realmStickersDetails.setToken(avatarToken);
            realmStickersDetails.setUri(HelperDownloadSticker.createPathFile(avatarToken , avatarName ));
            realmStickersDetails.setFileName(avatarName);
            realmStickersDetails.setFileSize(avatarSize);
            realmStickersDetails.setSort(sort);
            realmStickersDetails.setGroupId(groupId);

            HelperDownloadSticker.stickerDownload(avatarToken, avatarName, avatarSize, ProtoFileDownload.FileDownload.Selector.FILE, RequestFileDownload.TypeDownload.STICKER_DETAIL);
        }

        realm.close();
        return realmStickersDetails;
    }

    public static RealmStickersDetails updateUriStickersDetails(String avatarToken, String uri) {
        Realm realm = Realm.getDefaultInstance();

        RealmStickersDetails realmStickersDetails = realm.where(RealmStickersDetails.class).equalTo("token", avatarToken).findFirst();
        if (realmStickersDetails != null) {
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    realmStickersDetails.setUri(uri);
                }
            });

        }

        realm.close();

        return realmStickersDetails;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getSt_id() {
        return st_id;
    }

    public void setSt_id(String st_id) {
        this.st_id = st_id;
    }

    public long getRefId() {
        return refId;
    }

    public void setRefId(long refId) {
        this.refId = refId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }
}
