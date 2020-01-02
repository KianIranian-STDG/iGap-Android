package net.iGap.repository.sticker;

import android.util.Log;

import com.vanniktech.emoji.sticker.struct.StructGroupSticker;
import com.vanniktech.emoji.sticker.struct.StructSticker;

import net.iGap.DbManager;
import net.iGap.G;
import net.iGap.api.apiService.ResponseCallback;
import net.iGap.fragments.FragmentChat;
import net.iGap.fragments.emoji.api.APIEmojiService;
import net.iGap.fragments.emoji.api.ApiEmojiUtils;
import net.iGap.fragments.emoji.struct.StructEachSticker;
import net.iGap.fragments.emoji.struct.StructIGSticker;
import net.iGap.fragments.emoji.struct.StructIGStickerGroup;
import net.iGap.fragments.emoji.struct.StructStickerResult;
import net.iGap.helper.rx.AaSingleObserver;
import net.iGap.interfaces.ObserverView;
import net.iGap.realm.RealmStickers;
import net.iGap.realm.RealmStickersDetails;
import net.iGap.realm.RealmStickersDetailsFields;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import io.realm.RealmResults;
import io.realm.Sort;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StickerRepository implements ObserverView {

    private APIEmojiService apiService;
    private String TAG = "abbasiSticker Repository";
    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    public StickerRepository() {
        apiService = ApiEmojiUtils.getAPIService();
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
                                RealmStickers.put(realm, structGroupSticker.getCreatedAt(), structGroupSticker.getId(), structGroupSticker.getRefId(), structGroupSticker.getName(), structGroupSticker.getAvatarToken(), structGroupSticker.getAvatarSize(), structGroupSticker.getAvatarName(), structGroupSticker.getPrice(), structGroupSticker.getIsVip(), structGroupSticker.getSort(), structGroupSticker.getIsVip(), structGroupSticker.getCreatedBy(), structGroupSticker.getStickers(), false);
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
                .subscribe(new AaSingleObserver<StructSticker>(compositeDisposable) {
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
                        stickerGroup.setName("Recent");
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
        if (compositeDisposable != null)
            compositeDisposable.clear();
    }

    public CompositeDisposable getCompositeDisposable() {
        return compositeDisposable;
    }
}
