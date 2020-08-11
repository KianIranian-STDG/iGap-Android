package net.iGap.repository;

import net.iGap.G;
import net.iGap.R;
import net.iGap.api.StickerApi;
import net.iGap.api.apiService.RetrofitFactory;
import net.iGap.fragments.emoji.apiModels.Ids;
import net.iGap.fragments.emoji.struct.StructIGSticker;
import net.iGap.fragments.emoji.struct.StructIGStickerCategory;
import net.iGap.fragments.emoji.struct.StructIGStickerGroup;
import net.iGap.module.RSACipher;
import net.iGap.module.accountManager.DbManager;
import net.iGap.observers.interfaces.ResponseCallback;
import net.iGap.observers.rx.IGSingleObserver;
import net.iGap.realm.RealmStickerGroup;
import net.iGap.realm.RealmStickerGroupFields;
import net.iGap.realm.RealmStickerItem;
import net.iGap.realm.RealmStickerItemFields;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.SingleSource;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import io.realm.RealmResults;
import io.realm.Sort;

public class StickerRepository {
    private static final int RECENT_STICKER_LIMIT = 13;
    private static final int FAVORITE_STICKER_LIMIT = 15;

    private static StickerRepository stickerRepository;
    private StickerApi stickerApi;
    private RSACipher rsaCipher;
    private String TAG = "abbasiStickerRepository";

    public static StickerRepository getInstance() {
        if (stickerRepository == null)
            stickerRepository = new StickerRepository();
        return stickerRepository;
    }

    private StickerRepository() {
        stickerApi = new RetrofitFactory().getStickerRetrofit();
    }

    public Single<List<StructIGStickerCategory>> getStickerCategory() {
        return stickerApi.getCategories()
                .subscribeOn(Schedulers.newThread())
                .map(dataModel -> {
                    List<StructIGStickerCategory> categories = new ArrayList<>();
                    for (int i = 0; i < dataModel.getData().size(); i++) {
                        StructIGStickerCategory category = new StructIGStickerCategory(dataModel.getData().get(i));
                        categories.add(category);
                    }
                    return categories;
                });
    }

    public Single<List<StructIGStickerGroup>> getCategoryStickerGroups(String categoryId) {
        return stickerApi.getCategoryStickers(categoryId)
                .subscribeOn(Schedulers.newThread())
                .map(dataModel -> {
                    List<StructIGStickerGroup> groups = new ArrayList<>();
                    for (int i = 0; i < dataModel.getData().size(); i++) {
                        StructIGStickerGroup stickerGroup = new StructIGStickerGroup(dataModel.getData().get(i));
                        groups.add(stickerGroup);
                    }
                    return groups;
                });
    }

    public Single<List<StructIGStickerGroup>> getCategoryStickerGroups(String categoryId, int skip, int limit) {
        return stickerApi.getCategoryStickers(categoryId, skip, limit)
                .subscribeOn(Schedulers.newThread())
                .map(dataModel -> {
                    List<StructIGStickerGroup> groups = new ArrayList<>();
                    for (int i = 0; i < dataModel.getData().size(); i++) {
                        StructIGStickerGroup stickerGroup = new StructIGStickerGroup(dataModel.getData().get(i));
                        groups.add(stickerGroup);
                    }
                    return groups;
                });
    }

    public void getUserStickersGroup() {
        CompositeDisposable compositeDisposable = new CompositeDisposable();
        stickerApi.getUserStickersGroup()
                .subscribeOn(Schedulers.newThread())
                .map(dataModel -> {
                    List<StructIGStickerGroup> groups = new ArrayList<>();
                    for (int i = 0; i < dataModel.getData().size(); i++) {
                        StructIGStickerGroup stickerGroup = new StructIGStickerGroup(dataModel.getData().get(i));
                        groups.add(stickerGroup);
                    }
                    return groups;
                })
                .subscribe(new IGSingleObserver<List<StructIGStickerGroup>>(compositeDisposable) {
                    @Override
                    public void onSuccess(List<StructIGStickerGroup> structIGStickerGroups) {
                        updateStickers(structIGStickerGroups);
                        if (!compositeDisposable.isDisposed())
                            compositeDisposable.dispose();
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        if (!compositeDisposable.isDisposed())
                            compositeDisposable.dispose();
                    }
                });
    }

    private Completable addStickerGroupToMyStickersApiService(String groupId) {
        return stickerApi.addStickerGroupToMyStickers(groupId).subscribeOn(Schedulers.newThread());
    }

    private Completable removeStickerGroupFromMyStickersApiService(String groupId) {
        return stickerApi.removeStickerGroupFromMyStickers(new Ids(groupId)).subscribeOn(Schedulers.newThread());
    }

    private Single<StructIGStickerGroup> getStickerGroupApiService(String groupId) {
        return stickerApi.getStickerGroupStickers(groupId)
                .subscribeOn(Schedulers.newThread())
                .map(StructIGStickerGroup::new);
    }

    public Single<List<StructIGSticker>> getStickerGroupStickers(String groupId) {
        return getStickerGroupApiService(groupId).map(StructIGStickerGroup::getStickers);
    }

    public Completable getRecentSticker() {
        return stickerApi.getRecentSticker()
                .subscribeOn(Schedulers.newThread())
                .flatMapCompletable(stickersDataModel -> CompletableObserver::onComplete);
    }

    public Completable addStickerToRecent(String stickerId) {
        return stickerApi.addStickerToRecent(stickerId)
                .subscribeOn(Schedulers.newThread())
                .flatMapCompletable(o -> CompletableObserver::onComplete);
    }

    private Completable addStickerToFavoriteApiService(String stickerId) {
        return stickerApi.addStickerToFavorite(stickerId).subscribeOn(Schedulers.newThread());
    }

    public Completable getFavoriteSticker() {
        return stickerApi.getFavoriteSticker()
                .subscribeOn(Schedulers.newThread())
                .flatMapCompletable(stickersDataModel -> CompletableObserver::onComplete);
    }

    private void updateStickers(List<StructIGStickerGroup> stickerGroup) {
        DbManager.getInstance().doRealmTask(realm -> {
            realm.executeTransactionAsync(asyncRealm -> {
                HashSet<String> hashedData = new HashSet<>();
                ArrayList<RealmStickerGroup> itemToDelete = new ArrayList<>();
                for (StructIGStickerGroup structGroupSticker : stickerGroup) {
                    hashedData.add(structGroupSticker.getGroupId());
                }

                RealmResults<RealmStickerGroup> allStickers = asyncRealm.where(RealmStickerGroup.class).findAll();
                for (RealmStickerGroup realmStickers : allStickers) {
                    if (!hashedData.contains(realmStickers.getId())) {
                        itemToDelete.add(realmStickers);
                    }
                }

                for (RealmStickerGroup realmStickers : itemToDelete) {
                    realmStickers.removeFromRealm();
                }

                for (StructIGStickerGroup updateStickers : stickerGroup) {
                    RealmStickerGroup.put(asyncRealm, updateStickers);
                }
            });
        });
    }

    public Single<StructIGStickerGroup> getStickerGroup(String groupId) {

        RealmStickerGroup realmStickers = DbManager.getInstance().doRealmTask(realm -> {
            return realm.where(RealmStickerGroup.class).equalTo(RealmStickerGroupFields.ID, groupId).findFirst();
        });

        if (realmStickers != null && realmStickers.isValid()) {
            StructIGStickerGroup stickerGroup = new StructIGStickerGroup(realmStickers);
            return Single.create(emitter -> emitter.onSuccess(stickerGroup));
        } else {
            return getStickerGroupApiService(groupId);
        }
    }

    public Flowable<List<StructIGStickerGroup>> getStickerGroupWithRecentTabForEmojiView() {
        return DbManager.getInstance().doRealmTask(realm -> {
            return realm.where(RealmStickerGroup.class).findAllAsync()
                    .asFlowable().filter(RealmResults::isLoaded)
                    .map(realmStickers -> {
                        List<StructIGStickerGroup> structIGStickerGroups = new ArrayList<>();
                        for (int i = 0; i < realmStickers.size(); i++) {
                            StructIGStickerGroup stickerGroup = new StructIGStickerGroup(realmStickers.get(i));
                            structIGStickerGroups.add(stickerGroup);
                        }
                        return structIGStickerGroups;
                    }).map(structIGStickerGroups -> {
                        StructIGStickerGroup stickerGroup = new StructIGStickerGroup(StructIGStickerGroup.RECENT_GROUP);
                        stickerGroup.setName(G.context.getResources().getString(R.string.recently));
                        RealmResults<RealmStickerItem> realmStickersDetails = realm.where(RealmStickerItem.class)
                                .limit(RECENT_STICKER_LIMIT)
                                .notEqualTo(RealmStickerItemFields.RECENT_TIME, 0)
                                .sort(RealmStickerItemFields.RECENT_TIME, Sort.DESCENDING)
                                .findAll();
                        List<StructIGSticker> stickers = new ArrayList<>();
                        for (int i = 0; i < realmStickersDetails.size(); i++) {
                            RealmStickerItem stickerItem = realmStickersDetails.get(i);
                            if (stickerItem != null) {
                                stickers.add(new StructIGSticker(stickerItem));
                            }
                        }
                        stickerGroup.setStickers(stickers);
                        if (stickers.size() > 0) {
                            structIGStickerGroups.add(0, stickerGroup);
                        }
                        return structIGStickerGroups;
                    }).map(structIGStickerGroups -> {
                        StructIGStickerGroup favoriteStickerGroup = new StructIGStickerGroup(StructIGStickerGroup.FAVORITE_GROUP);
                        favoriteStickerGroup.setName(G.context.getResources().getString(R.string.beeptunesـfavorite_song));
                        RealmResults<RealmStickerItem> stickerItems = realm.where(RealmStickerItem.class)
                                .limit(FAVORITE_STICKER_LIMIT)
                                .equalTo(RealmStickerItemFields.IS_FAVORITE, true)
                                .sort(RealmStickerItemFields.RECENT_TIME, Sort.DESCENDING)
                                .findAll();

                        if (stickerItems != null) {
                            List<StructIGSticker> stickers = new ArrayList<>();
                            for (int i = 0; i < stickerItems.size(); i++) {
                                RealmStickerItem stickerItem = stickerItems.get(i);
                                if (stickerItem != null) {
                                    stickers.add(new StructIGSticker(stickerItem));
                                }
                            }

                            favoriteStickerGroup.setStickers(stickers);

//                            boolean hasRecent = structIGStickerGroups.get(0).getGroupId().equals(StructIGStickerGroup.RECENT_GROUP);

//                            if (stickers.size() > 0) {
//                                structIGStickerGroups.add(hasRecent ? 1 : 0, favoriteStickerGroup);
//                            }
                        }

                        return structIGStickerGroups;
                    });
        });
    }

    public Flowable<List<StructIGSticker>> getStickerByEmoji(String unicode) {
        return DbManager.getInstance().doRealmTask(realm -> {
            return realm.where(RealmStickerItem.class)
                    .equalTo(RealmStickerItemFields.NAME, unicode)
                    .sort(RealmStickerItemFields.RECENT_TIME, Sort.DESCENDING)
                    .findAll()
                    .asFlowable()
                    .filter(RealmResults::isLoaded)
                    .map(realmStickers -> {
                        List<StructIGSticker> stickers = new ArrayList<>();
                        for (int i = 0; i < realmStickers.size(); i++) {
                            RealmStickerItem stickerItem = realmStickers.get(i);
                            if (stickerItem != null) {
                                StructIGSticker sticker = new StructIGSticker(stickerItem);
                                stickers.add(sticker);
                            }
                        }
                        return stickers;
                    });
        });
    }

    public Flowable<List<StructIGStickerGroup>> getMySticker() {
        return DbManager.getInstance().doRealmTask(realm -> {
            return realm.where(RealmStickerGroup.class)
                    .findAll().asFlowable()
                    .filter(RealmResults::isLoaded)
                    .map(realmStickers -> {
                        List<StructIGStickerGroup> stickerGroups = new ArrayList<>();
                        for (int i = 0; i < realmStickers.size(); i++) {
                            stickerGroups.add(new StructIGStickerGroup(realmStickers.get(i)));
                        }
                        return stickerGroups;
                    });
        });
    }

    public void clearRecentSticker(ResponseCallback<Boolean> callback) {
        DbManager.getInstance().doRealmTask(realm -> {
            realm.executeTransactionAsync(realm1 -> {
                RealmResults<RealmStickerItem> realmStickersDetails = realm1.where(RealmStickerItem.class)
                        .notEqualTo(RealmStickerItemFields.RECENT_TIME, 0)
                        .findAll();

                realmStickersDetails.setLong(RealmStickerItemFields.RECENT_TIME, 0);

            }, () -> callback.onSuccess(true), error -> callback.onError(error.getMessage()));
        });
    }

    public void clearStickerInternalStorage() {
        File file;
        try {
            file = new File(G.downloadDirectoryPath);
            clearFile(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void clearFile(@NotNull File fileTmp) {
        for (File file : fileTmp.listFiles()) {
            if (!file.isDirectory()) file.delete();
        }
    }


    public String getStickerPath(String token, String extension) {
        if (token == null || extension == null)
            return null;

        String mimeType = ".png";
        int index = extension.lastIndexOf(".");
        if (index >= 0) {
            mimeType = extension.substring(index);
        }
        return G.downloadDirectoryPath + "/" + token + mimeType;
    }

    public Completable addStickerToFavorite(String stickerId) {
        return addStickerToFavoriteApiService(stickerId)
                .doOnComplete(() -> DbManager.getInstance().doRealmTask(realm -> {
                    RealmStickerItem stickerItem = realm.where(RealmStickerItem.class)
                            .equalTo(RealmStickerItemFields.ID, stickerId)
                            .equalTo(RealmStickerItemFields.IS_FAVORITE, false)
                            .findFirst();

                    if (stickerItem != null)
                        realm.executeTransactionAsync(realm1 -> stickerItem.setFavorite(true));

                }));
    }

    public Single<StructIGStickerGroup> addStickerGroupToMyStickers(StructIGStickerGroup stickerGroup) {
        return addStickerGroupToMyStickersApiService(stickerGroup.getGroupId())
                .doOnComplete(() -> DbManager.getInstance().doRealmTask(realm -> {
                    RealmStickerGroup realmStickerGroup = realm
                            .where(RealmStickerGroup.class)
                            .equalTo(RealmStickerGroupFields.ID, stickerGroup.getGroupId())
                            .findFirst();

                    if (realmStickerGroup == null) {
                        realm.executeTransactionAsync(asyncRealm -> RealmStickerGroup.put(asyncRealm, stickerGroup));
                        stickerGroup.setInUserList(true);
                    }

                })).andThen((SingleSource<StructIGStickerGroup>) observer -> observer.onSuccess(stickerGroup));
    }

    public Single<StructIGStickerGroup> removeStickerGroupFromMyStickers(StructIGStickerGroup stickerGroup) {
        return removeStickerGroupFromMyStickersApiService(stickerGroup.getGroupId())
                .doOnComplete(() -> DbManager.getInstance().doRealmTask(realm -> {
                    RealmStickerGroup realmStickerGroup = realm
                            .where(RealmStickerGroup.class)
                            .equalTo(RealmStickerGroupFields.ID, stickerGroup.getGroupId())
                            .findFirst();

                    if (realmStickerGroup != null) {
                        try {
                            DbManager.getInstance().doRealmTransaction(asyncRealm -> realmStickerGroup.removeFromRealm());
                            stickerGroup.setInUserList(false);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                })).andThen((SingleSource<StructIGStickerGroup>) observer -> observer.onSuccess(stickerGroup));
    }
}
