package net.iGap.repository.sticker;

import android.util.Log;

import com.vanniktech.emoji.sticker.struct.StructGroupSticker;
import com.vanniktech.emoji.sticker.struct.StructSticker;

import net.iGap.AccountManager;
import net.iGap.DbManager;
import net.iGap.G;
import net.iGap.R;
import net.iGap.api.StickerApi;
import net.iGap.api.apiService.ResponseCallback;
import net.iGap.api.apiService.RetrofitFactory;
import net.iGap.fragments.FragmentChat;
import net.iGap.fragments.emoji.api.APIEmojiService;
import net.iGap.fragments.emoji.api.ApiEmojiUtils;
import net.iGap.fragments.emoji.struct.StructEachSticker;
import net.iGap.fragments.emoji.struct.StructIGSticker;
import net.iGap.fragments.emoji.struct.StructIGStickerCategory;
import net.iGap.fragments.emoji.struct.StructIGStickerGroup;
import net.iGap.fragments.emoji.struct.StructStickerResult;
import net.iGap.interfaces.ObserverView;
import net.iGap.realm.RealmStickers;
import net.iGap.realm.RealmStickersDetails;
import net.iGap.realm.RealmStickersDetailsFields;
import net.iGap.rx.IGSingleObserver;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StickerRepository implements ObserverView {
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

    public Single<List<StructIGStickerGroup>> getUserStickersGroup() {
        return stickerApi.getUserStickersGroup()
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

    public Completable addStickerGroupToMyStickers(StructIGSticker structIGSticker) {
        return stickerApi.addStickerGroupToMyStickers(structIGSticker.getGroupId())
                .subscribeOn(Schedulers.newThread())
                .flatMapCompletable(response -> {
                    Realm realm = Realm.getInstance(AccountManager.getInstance().getCurrentUser().getRealmConfiguration());
                    realm.executeTransaction(realm1 -> {

                    });
                    return CompletableObserver::onComplete;
                });
    }

    public Single<StructIGStickerGroup> getStickerGroup(String groupId) {
        return stickerApi.getStickerGroupStickers(groupId)
                .subscribeOn(Schedulers.newThread())
                .map(StructIGStickerGroup::new);
    }

    public Single<List<StructIGSticker>> getStickerGroupStickers(String groupId) {
        return getStickerGroup(groupId).map(StructIGStickerGroup::getStickers);
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

    public Completable addStickerToFavorite(String stickerId) {
        return stickerApi.addStickerToFavorite(stickerId)
                .subscribeOn(Schedulers.newThread())
                .flatMapCompletable(o -> CompletableObserver::onComplete);
    }

    public Completable getFavoriteSticker() {
        return stickerApi.getFavoriteSticker()
                .subscribeOn(Schedulers.newThread())
                .flatMapCompletable(stickersDataModel -> CompletableObserver::onComplete);
    }

    public void getStickerListForStickerDialog(String groupId, ResponseCallback<StructIGStickerGroup> callback) {

        StructIGStickerGroup stickerGroup = new StructIGStickerGroup(groupId);

        DbManager.getInstance().doRealmTask(realm -> {
            RealmStickers realmStickers = RealmStickers.checkStickerExist(groupId, realm);

            if (realmStickers != null && realmStickers.isValid())
                stickerGroup.setValueWithRealmStickers(realmStickers);
        });

        if (stickerGroup.hasData()) {
            callback.onSuccess(stickerGroup);
        } else {
            getStickerFromServer(groupId, callback);
        }
    }

    private void getStickerFromServer(String groupId, ResponseCallback<StructIGStickerGroup> callback) {
        if (apiService != null)
            apiService.getSticker(groupId).enqueue(new Callback<StructEachSticker>() {
                @Override
                public void onResponse(@NotNull Call<StructEachSticker> call, @NotNull Response<StructEachSticker> response) {
                    if (response.body() != null) {
                        if (response.body().getOk() && response.body().getData() != null) {

                            StructGroupSticker structGroupSticker = response.body().getData();
                            StructIGStickerGroup stickerGroup = new StructIGStickerGroup(groupId);

                            stickerGroup.setValueWithOldStruct(structGroupSticker);

                            G.handler.postDelayed(() -> callback.onSuccess(stickerGroup), 300);
                        }
                    }
                }

                @Override
                public void onFailure(@NotNull Call<StructEachSticker> call, @NotNull Throwable t) {
                    Log.i(TAG, "get sticker from API SERVICE  with group id" + groupId + " with error " + t.getMessage());
                    callback.onError(t.getMessage());
                }
            });
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

                            if (FragmentChat.onUpdateSticker != null)
                                FragmentChat.onUpdateSticker.update();

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
                            }, () -> FragmentChat.onUpdateSticker.update());
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

    public List<StructIGStickerGroup> getMyStickers() {
        return RealmStickers.getMyStickers();
    }

    public void putOrUpdateMyStickerPackToDb() {
        apiService.getMyStickerPack()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new IGSingleObserver<StructSticker>(compositeDisposable) {
                    @Override
                    public void onSuccess(StructSticker structSticker) {

                        RealmStickers.updateStickers(structSticker.getData(), () -> {
                            if (FragmentChat.onUpdateSticker != null)
                                FragmentChat.onUpdateSticker.update();
                        });

                        Log.i(TAG, "onSuccess: " + structSticker.getData().size());
                    }
                });
    }

    public Flowable<List<StructIGStickerGroup>> getStickerGroupWithRecentTabForEmojiView() {
        return DbManager.getInstance().doRealmTask(realm -> {
            return realm.where(RealmStickers.class).findAllAsync()
                    .asFlowable()
                    .filter(RealmResults::isLoaded)
                    .map(realmStickers -> {
                        List<StructIGStickerGroup> structIGStickerGroups = new ArrayList<>();
                        for (int i = 0; i < realmStickers.size(); i++) {
                            StructIGStickerGroup stickerGroup = new StructIGStickerGroup().setValueWithRealmStickers(realmStickers.get(i));
                            structIGStickerGroups.add(stickerGroup);
                        }
                        return structIGStickerGroups;
                    }).map(structIGStickerGroups -> {
                        StructIGStickerGroup stickerGroup = new StructIGStickerGroup(StructIGStickerGroup.RECENT_GROUP);
                        stickerGroup.setName(G.context.getResources().getString(R.string.recently));
                        RealmResults<RealmStickersDetails> realmStickersDetails = realm.where(RealmStickersDetails.class)
                                .limit(13)
                                .notEqualTo(RealmStickersDetailsFields.RECENT_TIME, 0)
                                .sort(RealmStickersDetailsFields.RECENT_TIME, Sort.DESCENDING)
                                .findAll();
                        List<StructIGSticker> stickers = new ArrayList<>();
                        for (int i = 0; i < realmStickersDetails.size(); i++) {
                            stickers.add(new StructIGSticker().setValueWithRealm(realmStickersDetails.get(i)));
                        }
                        stickerGroup.setStickers(stickers);
                        if (stickers.size() > 0)
                            structIGStickerGroups.add(0, stickerGroup);
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
            return realm.where(RealmStickers.class).findAll().asFlowable()
                    .filter(RealmResults::isLoaded)
                    .map(realmStickers -> {
                        List<StructIGStickerGroup> stickerGroups = new ArrayList<>();
                        for (int i = 0; i < realmStickers.size(); i++) {
                            StructIGStickerGroup group = new StructIGStickerGroup().setValueWithRealmStickers(realmStickers.get(i));
                            stickerGroups.add(group);
                        }
                        return stickerGroups;
                    });
        });
    }

    public void clearRecentSticker(ResponseCallback<Boolean> callback) {
        DbManager.getInstance().doRealmTask(realm -> {
            realm.executeTransactionAsync(realm1 -> {
                RealmResults<RealmStickersDetails> realmStickersDetails = realm1.where(RealmStickersDetails.class)
                        .limit(13)
                        .notEqualTo(RealmStickersDetailsFields.RECENT_TIME, 0)
                        .findAll();

                realmStickersDetails.setLong(RealmStickersDetailsFields.RECENT_TIME, 0);

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
}
