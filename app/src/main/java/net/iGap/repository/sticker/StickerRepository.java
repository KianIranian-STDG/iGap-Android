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
import net.iGap.fragments.emoji.struct.StructIGStickerGroup;
import net.iGap.fragments.emoji.struct.StructStickerResult;
import net.iGap.helper.rx.AaSingleObserver;
import net.iGap.realm.RealmStickers;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import io.realm.OrderedCollectionChangeSet;
import io.realm.OrderedRealmCollectionChangeListener;
import io.realm.RealmResults;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StickerRepository {

    private APIEmojiService apiService;
    private String TAG = "abbasiSticker Repository";

    public interface Listener {
        void onAddSticker(StructIGStickerGroup stickerGroup, int pos);

        void onDeletedSticker(StructIGStickerGroup stickerGroup, int pos);

        void onUpdatedSticker(StructIGStickerGroup stickerGroup, int pos);

        void dataChange();
    }

    public StickerRepository() {
        apiService = ApiEmojiUtils.getAPIService();
    }

    private RealmResults<RealmStickers> liveRealmStickers;
    private OrderedRealmCollectionChangeListener<RealmResults<RealmStickers>> stickerChangeListener;

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

    public void addChangeListener(Listener listener) {
        removeStickerChangeListener();

        DbManager.getInstance().doRealmTask(realm -> {
            liveRealmStickers = realm.where(RealmStickers.class).findAll();

            stickerChangeListener = (realmStickers, changeSet) -> {
                if (changeSet.getState() == OrderedCollectionChangeSet.State.INITIAL) {
                    listener.dataChange();
                    return;
                }

                OrderedCollectionChangeSet.Range[] deletions = changeSet.getDeletionRanges();
                for (int i = deletions.length - 1; i >= 0; i--) {
                    OrderedCollectionChangeSet.Range range = deletions[i];
                    listener.onDeletedSticker(null, 0);
                }

                OrderedCollectionChangeSet.Range[] insertions = changeSet.getInsertionRanges();
                for (OrderedCollectionChangeSet.Range range : insertions) {
                    listener.onAddSticker(null, 0);
                }


                OrderedCollectionChangeSet.Range[] modifications = changeSet.getChangeRanges();
                for (OrderedCollectionChangeSet.Range range : modifications) {
                    listener.onUpdatedSticker(null, 0);
                }

            };

            liveRealmStickers.addChangeListener(stickerChangeListener);
        });
    }

    private void removeStickerChangeListener() {
        if (liveRealmStickers != null && stickerChangeListener != null) {
            liveRealmStickers.removeChangeListener(stickerChangeListener);
            liveRealmStickers = null;
            stickerChangeListener = null;
        }
    }

    public void onDestroy() {
        removeStickerChangeListener();
    }

    public void putOrUpdateMyStickerPackToDb() {
        apiService.getMyStickerPack()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new AaSingleObserver<StructSticker>() {
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
}
