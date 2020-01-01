package net.iGap.realm;


import com.vanniktech.emoji.sticker.struct.StructGroupSticker;
import com.vanniktech.emoji.sticker.struct.StructItemSticker;

import net.iGap.DbManager;
import net.iGap.fragments.emoji.HelperDownloadSticker;
import net.iGap.fragments.emoji.struct.StructIGSticker;
import net.iGap.fragments.emoji.struct.StructIGStickerGroup;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.RealmResults;

public class RealmStickers extends RealmObject {

    private String st_id;
    private long createdAt;
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

    public static RealmStickers put(Realm realm, long createdAt, String st_id, long refId, String name, String avatarToken, long avatarSize, String avatarName, long price, boolean isVip, int sort, boolean approved, long createdBy, List<StructItemSticker> stickers, boolean isFavorite) {

        RealmStickers realmStickers = realm.where(RealmStickers.class).equalTo(RealmStickersFields.ST_ID, st_id).findFirst();

        if (realmStickers == null) {
            realmStickers = realm.createObject(RealmStickers.class);
        }

        realmStickers.setCreatedAt(createdAt);
        realmStickers.setSt_id(st_id);
        realmStickers.setRefId(refId);
        realmStickers.setName(name);
        realmStickers.setAvatarToken(avatarToken);
        realmStickers.setUri(HelperDownloadSticker.downloadStickerPath(avatarToken, avatarName));
        realmStickers.setAvatarSize(avatarSize);
        realmStickers.setAvatarName(avatarName);
        realmStickers.setPrice(price);
        realmStickers.setVip(isVip);
        realmStickers.setSort(sort);
        realmStickers.setApproved(approved);
        realmStickers.setCreatedBy(createdBy);
        realmStickers.setFavorite(isFavorite);

        RealmList<RealmStickersDetails> realmStickersDetails = new RealmList<>();
        for (StructItemSticker itemSticker : stickers) {
            realmStickersDetails.add(RealmStickersDetails.put(realm, itemSticker.getId(), itemSticker.getRefId(), itemSticker.getName(), itemSticker.getToken(), "", itemSticker.getAvatarSize(), itemSticker.getAvatarName(), itemSticker.getSort(), itemSticker.getGroupId()));
        }
        realmStickers.setRealmStickersDetails(realmStickersDetails);

        return realmStickers;
    }

    public List<StructIGSticker> getIGGroupStickers() {
        return DbManager.getInstance().doRealmTask(realm -> {

            if (getRealmStickersDetails() == null || getRealmStickersDetails().size() <= 0 || !getRealmStickersDetails().isValid())
                return new ArrayList<>();

            List<StructIGSticker> stickerDetails = new ArrayList<>();

            for (RealmStickersDetails stickersDetails : getRealmStickersDetails()) {
                StructIGSticker structIGSticker = new StructIGSticker();
                structIGSticker.setId(stickersDetails.getSt_id());
                structIGSticker.setName(stickersDetails.getName());
                structIGSticker.setPath(stickersDetails.getUri());
                structIGSticker.setGroupId(stickersDetails.getGroupId());
                structIGSticker.setToken(stickersDetails.getToken());
                structIGSticker.setFileName(stickersDetails.getFileName());
                structIGSticker.setFileSize((int) stickersDetails.getFileSize());
                stickerDetails.add(structIGSticker);
            }

            return stickerDetails;
        });
    }

    public static List<StructIGStickerGroup> getMyStickers() {
        return DbManager.getInstance().doRealmTask(realm -> {
            List<StructIGStickerGroup> stickers = new ArrayList<>();
            RealmResults<RealmStickers> realmStickers = realm.where(RealmStickers.class).findAll();

            for (RealmStickers item : realmStickers) {
                StructIGStickerGroup stickerGroup = new StructIGStickerGroup(item.getSt_id());

                stickerGroup.setCreatedAt(item.getCreatedAt());
                stickerGroup.setRefId(item.refId);
                stickerGroup.setName(item.name);
                stickerGroup.setAvatarToken(item.avatarToken);
                stickerGroup.setAvatarPath(HelperDownloadSticker.downloadStickerPath(item.avatarToken, item.avatarName));
                stickerGroup.setAvatarSize((int) item.getAvatarSize());
                stickerGroup.setAvatarName(item.getAvatarName());
                stickerGroup.setPrice(item.getPrice());
                stickerGroup.setVip(item.isVip);
                stickerGroup.setSort(item.sort);
                stickerGroup.setCreatedBy(item.createdBy);
                stickerGroup.setInMySticker(true);

                List<StructIGSticker> stickerDetails = new ArrayList<>();

                for (RealmStickersDetails it : item.getRealmStickersDetails()) {

                    StructIGSticker structIGSticker = new StructIGSticker();
                    structIGSticker.setId(it.getSt_id());
                    structIGSticker.setId(it.getSt_id());
                    structIGSticker.setName(it.getName());
                    structIGSticker.setToken(it.getToken());
                    structIGSticker.setPath(it.getUri());
                    structIGSticker.setName(it.getFileName());
                    structIGSticker.setFileSize((int) it.getFileSize());
                    structIGSticker.setGroupId(it.getGroupId());
                    stickerDetails.add(structIGSticker);

                }
                stickerGroup.setStickers(stickerDetails);
                stickers.add(stickerGroup);
            }
            return stickers;
        });
    }

    public static RealmStickers checkStickerExist(String groupId, Realm realm) {
        return realm.where(RealmStickers.class).equalTo(RealmStickersFields.ST_ID, groupId).findFirst();
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

    public void removeFromRealm() {
        if (realmStickersDetails != null) {
            for (Iterator<RealmStickersDetails> iterator = realmStickersDetails.iterator(); iterator.hasNext(); ) {
                RealmStickersDetails stickersDetails = iterator.next();
                if (stickersDetails != null) {
                    iterator.remove();
                    stickersDetails.deleteFromRealm();
                }
            }
        }

        deleteFromRealm();
    }

    public static void updateStickers(List<StructGroupSticker> mData, Realm.Transaction.OnSuccess onSuccess) {
        DbManager.getInstance().doRealmTask(realm -> {
            realm.executeTransactionAsync(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    HashSet<String> hashedData = new HashSet<>();
                    ArrayList<RealmStickers> itemToDelete = new ArrayList<>();
                    for (StructGroupSticker structGroupSticker : mData) {
                        hashedData.add(structGroupSticker.getId());
                    }

                    RealmResults<RealmStickers> allStickers = realm.where(RealmStickers.class).equalTo(RealmStickersFields.IS_FAVORITE, true).findAll();
                    for (RealmStickers realmStickers : allStickers) {
                        if (!hashedData.contains(realmStickers.st_id)) {
                            itemToDelete.add(realmStickers);
                        }
                    }

                    for (RealmStickers realmStickers : itemToDelete) {
                        realmStickers.removeFromRealm();
                    }

                    for (StructGroupSticker item : mData) {
                        RealmStickers.put(realm, item.getCreatedAt(), item.getId(), item.getRefId(), item.getName(), item.getAvatarToken(), item.getAvatarSize(), item.getAvatarName(), item.getPrice(), item.getIsVip(), item.getSort(), item.getIsVip(), item.getCreatedBy(), item.getStickers(), false);
                    }
                }
            }, onSuccess);
        });
    }
}
