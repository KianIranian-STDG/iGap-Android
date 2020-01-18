package net.iGap.repository.sticker;

import android.util.Log;

import com.vanniktech.emoji.sticker.struct.StructGroupSticker;
import com.vanniktech.emoji.sticker.struct.StructSticker;

import net.iGap.DbManager;
import net.iGap.G;
import net.iGap.R;
import net.iGap.api.StickerApi;
import net.iGap.api.apiService.ResponseCallback;
import net.iGap.api.apiService.RetrofitFactory;
import net.iGap.fragments.emoji.api.APIEmojiService;
import net.iGap.fragments.emoji.api.ApiEmojiUtils;
import net.iGap.fragments.emoji.struct.StructEachSticker;
import net.iGap.fragments.emoji.struct.StructIGSticker;
import net.iGap.fragments.emoji.struct.StructIGStickerCategory;
import net.iGap.fragments.emoji.struct.StructIGStickerGroup;
import net.iGap.fragments.emoji.struct.StructStickerResult;
import net.iGap.interfaces.ObserverView;
import net.iGap.realm.RealmStickerGroup;
import net.iGap.realm.RealmStickerGroupFields;
import net.iGap.realm.RealmStickerItem;
import net.iGap.realm.RealmStickerItemFields;
import net.iGap.realm.RealmStickers;
import net.iGap.realm.RealmStickersDetails;
import net.iGap.realm.RealmStickersDetailsFields;
import net.iGap.rx.IGSingleObserver;

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
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import io.realm.RealmResults;
import io.realm.Sort;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StickerRepository implements ObserverView {
    private static final int RECENT_STICKER_LIMIT = 13;
    private static final int FAVORITE_STICKER_LIMIT = 15;

    private static StickerRepository stickerRepository;
    private StickerApi stickerApi;

    private APIEmojiService apiService;
    private String TAG = "abbasiSticker Repository";
    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    public static StickerRepository getInstance() {
        if (stickerRepository == null)
            stickerRepository = new StickerRepository();
        return stickerRepository;
    }

    private StickerRepository() {
        stickerApi = new RetrofitFactory().getStickerRetrofit();
        apiService = ApiEmojiUtils.getAPIService();

        DbManager.getInstance().doRealmTask(realm -> {
            Log.i("abbasiNewSticker", "New Stickers: " + realm.where(RealmStickerItem.class).equalTo(RealmStickerItemFields.IS_FAVORITE, true).findAll().size());
        });

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

    public Single<List<StructIGStickerGroup>> getUserStickersGroup() {
        return stickerApi.getUserStickersGroup()
                .subscribeOn(Schedulers.newThread())
                .retry((integer, throwable) -> integer > 2)
                .doOnError(Throwable::printStackTrace)
                .map(dataModel -> {
                    List<StructIGStickerGroup> groups = new ArrayList<>();
                    for (int i = 0; i < dataModel.getData().size(); i++) {
                        StructIGStickerGroup stickerGroup = new StructIGStickerGroup(dataModel.getData().get(i));
                        groups.add(stickerGroup);
                    }
                    return groups;
                }).doOnSuccess(this::updateStickers);
    }

    private Completable addStickerGroupToMyStickersApiService(String groupId) {
        return stickerApi.addStickerGroupToMyStickers(groupId).subscribeOn(Schedulers.newThread());
    }

    private Completable removeStickerGroupFromMyStickersApiService(String groupId) {
        return stickerApi.removeStickerGroupFromMyStickers(groupId).subscribeOn(Schedulers.newThread());
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
                Log.i("abbasiNewSticker", "START UPDATE STICKER");
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
                Log.i("abbasiNewSticker", "FINISH STICKER UPDATE");
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

    private void getStickerFromServerAndInsetToDb(String groupId) {
        if (apiService != null)
            apiService.getSticker(groupId).enqueue(new Callback<StructEachSticker>() {
                @Override
                public void onResponse(@NotNull Call<StructEachSticker> call, @NotNull Response<StructEachSticker> response) {
                    if (response.body() != null) {
                        if (response.body().getOk() && response.body().getData() != null) {

                            StructGroupSticker structGroupSticker = response.body().getData();

                            DbManager.getInstance().doRealmTransaction(realm -> {
                                RealmStickers.put(realm, structGroupSticker.getId(), structGroupSticker.getRefId(), structGroupSticker.getName(), structGroupSticker.getAvatarToken(), structGroupSticker.getAvatarSize(), structGroupSticker.getAvatarName(), structGroupSticker.getPrice(), structGroupSticker.getIsVip(), structGroupSticker.getSort(), structGroupSticker.getIsVip(), structGroupSticker.getStickers(), false);
                            });
                        }
                    }
                }

                @Override
                public void onFailure(@NotNull Call<StructEachSticker> call, @NotNull Throwable t) {
                    Log.i(TAG, "get sticker from API SERVICE  with group id" + groupId + " with error " + t.getMessage());
                }
            });
    }

    public void addStickerGroupToFavorite(String groupId, ResponseCallback<Boolean> callback) {
        if (apiService != null)
            apiService.addSticker(groupId).enqueue(new Callback<StructStickerResult>() {
                @Override
                public void onResponse(@NotNull Call<StructStickerResult> call, @NotNull Response<StructStickerResult> response) {
                    if (response.body() != null && response.body().isSuccess()) {

                        DbManager.getInstance().doRealmTask(realm -> {

                            getStickerFromServerAndInsetToDb(groupId);

                        });

                        callback.onSuccess(true);
                    }
                }

                @Override
                public void onFailure(@NotNull Call<StructStickerResult> call, @NotNull Throwable t) {
                    callback.onError(t.getMessage());
                    Log.i(TAG, "add sticker to category API SERVICE  with group id" + groupId + " with error " + t.getMessage());
                }
            });
    }

    public void removeStickerGroupFromFavorite(String groupId, ResponseCallback<Boolean> callback) {
        if (apiService != null) {
            apiService.removeSticker(groupId).enqueue(new Callback<StructStickerResult>() {
                @Override
                public void onResponse(@NotNull Call<StructStickerResult> call, @NotNull Response<StructStickerResult> response) {
                    if (response.body() != null && response.body().isSuccess()) {
                        DbManager.getInstance().doRealmTask(realm -> {
                            realm.executeTransactionAsync(realm1 -> {
                                RealmStickers realmStickers = RealmStickers.checkStickerExist(groupId, realm1);
                                if (realmStickers != null)
                                    realmStickers.removeFromRealm();
                            });
                        });

                        callback.onSuccess(false);
                    }
                }

                @Override
                public void onFailure(@NotNull Call<StructStickerResult> call, @NotNull Throwable t) {
                    callback.onError(t.getMessage());
                    Log.i(TAG, "remove sticker to category API SERVICE  with group id" + groupId + " with error " + t.getMessage());
                }
            });
        }
    }

    public void putOrUpdateMyStickerPackToDb() {
        apiService.getMyStickerPack()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new IGSingleObserver<StructSticker>(compositeDisposable) {
                    @Override
                    public void onSuccess(StructSticker structSticker) {

                        RealmStickers.updateStickers(structSticker.getData(), () -> {

                        });

                        Log.i(TAG, "onSuccess: " + structSticker.getData().size());
                    }
                });
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
                                .notEqualTo(RealmStickersDetailsFields.RECENT_TIME, 0)
                                .sort(RealmStickersDetailsFields.RECENT_TIME, Sort.DESCENDING)
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
                                .sort(RealmStickersDetailsFields.RECENT_TIME, Sort.DESCENDING)
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
            return realm.where(RealmStickersDetails.class)
                    .equalTo(RealmStickersDetailsFields.NAME, unicode)
                    .sort(RealmStickersDetailsFields.RECENT_TIME, Sort.DESCENDING)
                    .findAll()
                    .asFlowable()
                    .filter(RealmResults::isLoaded)
                    .map(realmStickers -> {
                        List<StructIGSticker> stickers = new ArrayList<>();
                        for (int i = 0; i < realmStickers.size(); i++) {
                            StructIGSticker sticker = new StructIGSticker().setValueWithRealm(realmStickers.get(i));
                            stickers.add(sticker);
                        }
                        Log.i(TAG, "getStickerByEmoji: " + stickers.size());
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

    @Override
    public void unsubscribe() {
        Log.i(TAG, "repo unsubscribe: ");
        if (compositeDisposable != null && !compositeDisposable.isDisposed())
            compositeDisposable.clear();
    }

    public CompositeDisposable getCompositeDisposable() {
        return compositeDisposable;
    }


    public String getStickerPath(String token, String extension) {

        String mimeType = ".png";
        int index = extension.lastIndexOf(".");
        if (index >= 0) {
            mimeType = extension.substring(index);
        }

        Log.i(TAG, "createPathFile: " + G.downloadDirectoryPath + "/" + token + mimeType);

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
                            DbManager.getInstance().doRealmTransaction(asyncRealm -> realmStickerGroup.deleteFromRealm());
                            stickerGroup.setInUserList(false);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                })).andThen((SingleSource<StructIGStickerGroup>) observer -> observer.onSuccess(stickerGroup));
    }
}
