package net.iGap.fragments.emoji.add;

import android.util.Log;

import com.vanniktech.emoji.sticker.struct.StructGroupSticker;

import net.iGap.DbManager;
import net.iGap.api.apiService.ResponseCallback;
import net.iGap.fragments.FragmentChat;
import net.iGap.fragments.emoji.api.APIEmojiService;
import net.iGap.fragments.emoji.api.ApiEmojiUtils;
import net.iGap.fragments.emoji.struct.StructEachSticker;
import net.iGap.fragments.emoji.struct.StructStickerResult;
import net.iGap.realm.RealmStickers;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StickerRepository {

    private APIEmojiService apiService;
    private String TAG = "abbasiSticker";

    public StickerRepository() {
        apiService = ApiEmojiUtils.getAPIService();
    }

    public void getStickerListForAddDialog(String groupId, ResponseCallback<List<StructIGSticker>> callback) {
        DbManager.getInstance().doRealmTask(realm -> {
            RealmStickers realmStickers = RealmStickers.checkStickerExist(groupId, realm);
            if (realmStickers != null) {
                List<StructIGSticker> structIGStickers = RealmStickers.getGroupStickers(groupId);
                callback.onSuccess(structIGStickers);
                Log.i(TAG, "load sticker from DB with group id --> " + groupId);
            } else {
                Log.i(TAG, "get sticker from API SERVICE with group id --> " + groupId);
                getStickerFromServer(groupId, callback);
            }
        });
    }

    private void getStickerFromServer(String groupId, ResponseCallback<List<StructIGSticker>> callback) {
        if (apiService != null)
            apiService.getSticker(groupId).enqueue(new Callback<StructEachSticker>() {
                @Override
                public void onResponse(@NotNull Call<StructEachSticker> call, @NotNull Response<StructEachSticker> response) {
                    if (response.body() != null) {
                        if (response.body().getOk() && response.body().getData() != null) {
                            StructGroupSticker item = response.body().getData();
                            DbManager.getInstance().doRealmTask(realm -> {
                                realm.executeTransaction(realm1 -> RealmStickers.put(realm1, item.getCreatedAt(), item.getId(), item.getRefId(), item.getName(), item.getAvatarToken(), item.getAvatarSize(), item.getAvatarName(), item.getPrice(), item.getIsVip(), item.getSort(), item.getIsVip(), item.getCreatedBy(), item.getStickers(), false));
                            });

                            Log.i(TAG, "get sticker from API SERVICE with group id" + groupId + " successfully ");
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

    public void addSticker(String groupId, ResponseCallback<Boolean> callback) {
        if (apiService != null)
            apiService.addSticker(groupId).enqueue(new Callback<StructStickerResult>() {
                @Override
                public void onResponse(@NotNull Call<StructStickerResult> call, @NotNull Response<StructStickerResult> response) {
                    if (response.body() != null && response.body().isSuccess()) {
                        DbManager.getInstance().doRealmTask(realm -> {
                            realm.executeTransactionAsync(realm1 -> {
                                RealmStickers.updateFavorite(realm1, groupId, true);
                            }, () -> {
                                if (FragmentChat.onUpdateSticker != null) {
                                    FragmentChat.onUpdateSticker.update();
                                }
                            });
                        });
                        callback.onSuccess(true);
                        Log.i(TAG, "add sticker to category successfully with group id --> " + groupId);
                    }
                }

                @Override
                public void onFailure(@NotNull Call<StructStickerResult> call, @NotNull Throwable t) {
                    callback.onError(t.getMessage());
                    Log.i(TAG, "add sticker to category API SERVICE  with group id" + groupId + " with error " + t.getMessage());
                }
            });
    }

}
