package net.iGap.realm;


import com.vanniktech.emoji.sticker.struct.StructItemSticker;

import net.iGap.helper.emoji.HelperDownloadSticker;
import net.iGap.proto.ProtoFileDownload;
import net.iGap.request.RequestFileDownload;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.RealmResults;

public class RealmStickers extends RealmObject {

    private long id;
    private long createdAt;
    private String st_id;
    private long refId;
    private String name;
    private String avatarToken;
    private String uri = "";
    private long avatarSize;
    private String avatarName;
    private long price;
    private boolean isVip;
    private int sort;
    private boolean approved;
    private long createdBy;
    private boolean isFavorite;
    private RealmList<RealmStickersDetails> realmStickersDetails;


    public static RealmStickers put(long createdAt, String st_id, long refId, String name, String avatarToken, long avatarSize, String avatarName, long price, boolean isVip, int sort, boolean approved, long createdBy, List<StructItemSticker> stickers, boolean isFavorite) {

        Realm realm = Realm.getDefaultInstance();
        RealmStickers realmStickers = realm.where(RealmStickers.class).equalTo("avatarToken", avatarToken).findFirst();

        if (realmStickers == null) {
            realmStickers = realm.createObject(RealmStickers.class);
            realmStickers.setCreatedAt(createdAt);
            realmStickers.setSt_id(st_id);
            realmStickers.setRefId(refId);
            realmStickers.setName(name);
            realmStickers.setAvatarToken(avatarToken);
            realmStickers.setAvatarSize(avatarSize);
            realmStickers.setAvatarName(avatarName);
            realmStickers.setPrice(price);
            realmStickers.setVip(isVip);
            realmStickers.setSort(sort);
            realmStickers.setApproved(approved);
            realmStickers.setCreatedBy(createdBy);
            realmStickers.setFavorite(isFavorite);

            HelperDownloadSticker.stickerDownload(avatarToken, avatarName, avatarSize, ProtoFileDownload.FileDownload.Selector.FILE, RequestFileDownload.TypeDownload.STICKER);

            RealmList<RealmStickersDetails> realmStickersDetails = new RealmList<>();
            for (StructItemSticker itemSticker : stickers) {

                realmStickersDetails.add(RealmStickersDetails.put(itemSticker.getId(), itemSticker.getRefId(), itemSticker.getName(), itemSticker.getToken(), "", itemSticker.getAvatarSize(), itemSticker.getAvatarName(), itemSticker.getSort(), itemSticker.getGroupId()));
            }
            realmStickers.setRealmStickersDetails(realmStickersDetails);
        }

        realm.close();

        return realmStickers;
    }

    public static void setAllDataIsDeleted() {
        Realm realm = Realm.getDefaultInstance();
        RealmResults<RealmStickers> realmStickers = realm.where(RealmStickers.class).findAll();
        for (RealmStickers item : realmStickers) {
            item.setFavorite(false);
        }
        realm.close();
    }

//    public static void removeandUpdateRealm() {
//        Realm realm = Realm.getDefaultInstance();
//        RealmResults<RealmStickers> realmStickers = realm.where(RealmStickers.class).equalTo("isDeleted", true).findAll();
//        realmStickers.deleteAllFromRealm();
//        realm.close();
//    }

    public static List<com.vanniktech.emoji.sticker.struct.StructGroupSticker> getAllStickers(boolean isFavorite) {
        List<com.vanniktech.emoji.sticker.struct.StructGroupSticker> stickers = new ArrayList<>();

        Realm realm = Realm.getDefaultInstance();
        RealmResults<RealmStickers> realmStickers = realm.where(RealmStickers.class).equalTo(RealmStickersFields.IS_FAVORITE, isFavorite).findAll();

        for (RealmStickers item : realmStickers) {
            com.vanniktech.emoji.sticker.struct.StructGroupSticker itemSticker = new com.vanniktech.emoji.sticker.struct.StructGroupSticker();

            itemSticker.setCreatedAt(item.getCreatedAt());
            itemSticker.setId(item.st_id);
            itemSticker.setRefId(item.refId);
            itemSticker.setName(item.name);
            itemSticker.setAvatarToken(item.avatarToken);
            itemSticker.setUri(item.getUri());
            itemSticker.setAvatarSize((int) item.getAvatarSize());
            itemSticker.setAvatarName(item.getAvatarName());
            itemSticker.setPrice(item.getPrice());
            itemSticker.setVip(item.isVip);
            itemSticker.setSort(item.sort);
            itemSticker.setCreatedBy(item.createdBy);

            List<com.vanniktech.emoji.sticker.struct.StructItemSticker> stickerDetails = new ArrayList<>();

            for (RealmStickersDetails it : item.getRealmStickersDetails()) {

                com.vanniktech.emoji.sticker.struct.StructItemSticker itemSticker1 = new com.vanniktech.emoji.sticker.struct.StructItemSticker();
                itemSticker1.setId(it.getSt_id());
                itemSticker1.setRefId(it.getRefId());
                itemSticker1.setName(it.getName());
                itemSticker1.setToken(it.getToken());
                itemSticker1.setUri(it.getUri());
                itemSticker1.setAvatarName(it.getFileName());
                itemSticker1.setAvatarSize((int) it.getFileSize());
                itemSticker1.setSort(it.getSort());
                itemSticker1.setGroupId(it.getGroupId());
                stickerDetails.add(itemSticker1);

            }
            itemSticker.setStickers(stickerDetails);
            stickers.add(itemSticker);
        }


        return stickers;
    }

    public static RealmStickers updateUri(String token, String uri) {
        Realm realm = Realm.getDefaultInstance();

        RealmStickers realmStickers = realm.where(RealmStickers.class).equalTo("avatarToken", token).findFirst();
        if (realmStickers != null) {
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    realmStickers.setUri(uri);
                }
            });
        }

        realm.close();

        return realmStickers;
    }

    public static RealmStickers updateFavorite(String token, boolean isFavorite) {
        Realm realm = Realm.getDefaultInstance();
        RealmStickers realmStickers = realm.where(RealmStickers.class).equalTo("avatarToken", token).findFirst();
        if (realmStickers != null) {
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    realmStickers.setFavorite(isFavorite);
                }
            });
        }
        realm.close();

        return realmStickers;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
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

    public String getAvatarToken() {
        return avatarToken;
    }

    public void setAvatarToken(String avatarToken) {
        this.avatarToken = avatarToken;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public long getAvatarSize() {
        return avatarSize;
    }

    public void setAvatarSize(long avatarSize) {
        this.avatarSize = avatarSize;
    }

    public String getAvatarName() {
        return avatarName;
    }

    public void setAvatarName(String avatarName) {
        this.avatarName = avatarName;
    }

    public long getPrice() {
        return price;
    }

    public void setPrice(long price) {
        this.price = price;
    }

    public boolean isVip() {
        return isVip;
    }

    public void setVip(boolean vip) {
        isVip = vip;
    }

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    public boolean isApproved() {
        return approved;
    }

    public void setApproved(boolean approved) {
        this.approved = approved;
    }

    public long getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(long createdBy) {
        this.createdBy = createdBy;
    }

    public RealmList<RealmStickersDetails> getRealmStickersDetails() {
        return realmStickersDetails;
    }

    public void setRealmStickersDetails(RealmList<RealmStickersDetails> realmStickersDetails) {
        this.realmStickersDetails = realmStickersDetails;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }
}
