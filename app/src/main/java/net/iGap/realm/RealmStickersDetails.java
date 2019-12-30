package net.iGap.realm;


import net.iGap.fragments.emoji.HelperDownloadSticker;

import io.realm.Realm;
import io.realm.RealmObject;

public class RealmStickersDetails extends RealmObject {

    private String st_id;
    private long refId;
    private String name;
    private String token;
    private String uri = "";
    private String fileName;
    private long fileSize;
    private int sort;
    private String groupId;


    public static RealmStickersDetails put(Realm realm, String st_id, long refId, String name, String avatarToken, String uri, long avatarSize, String avatarName, int sort, String groupId) {

        RealmStickersDetails realmStickersDetails = realm.where(RealmStickersDetails.class).equalTo("token", avatarToken).findFirst();
        if (realmStickersDetails == null) {
            realmStickersDetails = realm.createObject(RealmStickersDetails.class);

            realmStickersDetails.setSt_id(st_id);
            realmStickersDetails.setRefId(refId);
            realmStickersDetails.setName(name);
            realmStickersDetails.setToken(avatarToken);
            realmStickersDetails.setUri(HelperDownloadSticker.downloadStickerPath(avatarToken, avatarName));
            realmStickersDetails.setFileName(avatarName);
            realmStickersDetails.setFileSize(avatarSize);
            realmStickersDetails.setSort(sort);
            realmStickersDetails.setGroupId(groupId);
        }

        return realmStickersDetails;
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
