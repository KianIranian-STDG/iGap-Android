package net.iGap.viewmodel;

import androidx.lifecycle.MutableLiveData;

import com.vanniktech.emoji.sticker.struct.StructGroupSticker;

import net.iGap.api.apiService.ResponseCallback;
import net.iGap.api.errorhandler.ErrorModel;
import net.iGap.repository.sticker.StickerRepository;

public class AddStickerViewModel extends BaseViewModel {
    private StickerRepository repository;
    private MutableLiveData<StructGroupSticker> openStickerDetailLiveData = new MutableLiveData<>();

    public AddStickerViewModel() {
        repository = new StickerRepository();
    }

    public void onItemCellClicked(StructGroupSticker stickerGroup) {
        openStickerDetailLiveData.postValue(stickerGroup);
    }

    public void onItemButtonClicked(StructGroupSticker stickerGroup, OnClickResult clickResult) {
        if (stickerGroup.getIsFavorite()) {
            repository.removeStickerGroupFromFavorite(stickerGroup.getId(), new ResponseCallback<Boolean>() {
                @Override
                public void onSuccess(Boolean data) {
                    stickerGroup.setIsFavorite(false);
                    clickResult.onResult(stickerGroup);
                }

                @Override
                public void onError(String error) {
                    clickResult.onResult(stickerGroup);
                }

                @Override
                public void onFailed() {

                }
            });
        } else {
            repository.addStickerGroupToFavorite(stickerGroup.getId(), new ResponseCallback<Boolean>() {
                @Override
                public void onSuccess(Boolean data) {
                    stickerGroup.setIsFavorite(true);
                    clickResult.onResult(stickerGroup);
                }

                @Override
                public void onError(String error) {
                    clickResult.onResult(stickerGroup);
                }

                @Override
                public void onFailed() {

                }
            });
        }
    }

    public MutableLiveData<StructGroupSticker> getOpenStickerDetailLiveData() {
        return openStickerDetailLiveData;
    }

    public interface OnClickResult {
        void onResult(StructGroupSticker sticker);
    }
}
