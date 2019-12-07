package net.iGap.fragments.emoji.add;

import android.util.Log;
import android.view.View;

import androidx.lifecycle.MutableLiveData;

import net.iGap.R;
import net.iGap.viewmodel.BaseCPayViewModel;
import net.iGap.viewmodel.BaseViewModel;

public class StickerDialogViewModel extends BaseViewModel {
    private static final String TAG = "abbasiSticker ViewModel";

    private StickerRepository repository;
    private StructIGStickerGroup stickerGroup;

    private MutableLiveData<Integer> progressMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<StructIGStickerGroup> stickersMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<Integer> addOrRemoveStickerLiveData = new MutableLiveData<>();
    private MutableLiveData<Boolean> closeDialogMutableLiveData = new MutableLiveData<>();

    StickerDialogViewModel(StructIGStickerGroup stickerGroup) {
        repository = new StickerRepository(stickerGroup);
        this.stickerGroup = stickerGroup;
        if (stickerGroup.hasData())
            onStickerFavoriteChange(stickerGroup.isFavorite());
    }

    public void onAddOrRemoveStickerClicked() {
        if (stickerGroup.isFavorite()) {
            removeStickerFromFavorite(stickerGroup.getGroupId());
        } else {
            addStickerToFavorite(stickerGroup.getGroupId());
        }
    }

    public void getSticker() {
        progressMutableLiveData.postValue(View.VISIBLE);
        repository.getStickerListForStickerDialog(new BaseCPayViewModel<StructIGStickerGroup>() {
            @Override
            public void onSuccess(StructIGStickerGroup data) {

                progressMutableLiveData.postValue(View.GONE);

                if (stickerGroup != null && !stickerGroup.hasData()) {
                    stickerGroup = data;
                }

                onStickerFavoriteChange(data.isFavorite());

                stickersMutableLiveData.postValue(stickerGroup);

                Log.i(TAG, "on Success getSticker with group id -> " + stickerGroup.getGroupId() + " and size -> " + stickerGroup.getStickers().size());
            }

            @Override
            public void onError(String error) {
                Log.i(TAG, "on Error getSticker with group id -> " + stickerGroup.getGroupId());
            }
        });
    }

    public void addStickerToFavorite(String groupId) {
        progressMutableLiveData.postValue(View.VISIBLE);
        repository.addStickerGroupToFavorite(groupId, new BaseCPayViewModel<Boolean>() {
            @Override
            public void onSuccess(Boolean data) {
                progressMutableLiveData.postValue(View.GONE);
                stickerGroup.setFavorite(data);
                onStickerFavoriteChange(data);
                Log.i(TAG, "on Success addStickerToFavorite with id -> " + stickerGroup.getGroupId());
            }

            @Override
            public void onError(String error) {
                progressMutableLiveData.postValue(View.GONE);
                closeDialogMutableLiveData.postValue(true);
                Log.i(TAG, "on Error addStickerToFavorite with id -> " + stickerGroup.getGroupId() + " with error " + error);
            }
        });
    }

    public void removeStickerFromFavorite(String groupId) {
        progressMutableLiveData.postValue(View.VISIBLE);
        repository.removeStickerGroupFromFavorite(groupId, new BaseCPayViewModel<Boolean>() {
            @Override
            public void onSuccess(Boolean data) {
                progressMutableLiveData.postValue(View.GONE);
                closeDialogMutableLiveData.postValue(true);
                Log.i(TAG, "on Success removeStickerFromFavorite with id -> " + stickerGroup.getGroupId());
            }

            @Override
            public void onError(String error) {
                progressMutableLiveData.postValue(View.GONE);
                closeDialogMutableLiveData.postValue(true);
                Log.i(TAG, "on Error addStickerToFavorite with id -> " + stickerGroup.getGroupId() + " and error -> " + error);
            }
        });
    }

    private void onStickerFavoriteChange(boolean favorite) {
        addOrRemoveStickerLiveData.postValue(favorite ? R.string.remove_sticker_with_size : R.string.add_sticker_with_size);
    }

    public MutableLiveData<Integer> getProgressMutableLiveData() {
        return progressMutableLiveData;
    }

    public MutableLiveData<StructIGStickerGroup> getStickersMutableLiveData() {
        return stickersMutableLiveData;
    }

    public MutableLiveData<Integer> getAddOrRemoveStickerLiveData() {
        return addOrRemoveStickerLiveData;
    }

    public MutableLiveData<Boolean> getCloseDialogMutableLiveData() {
        return closeDialogMutableLiveData;
    }
}
