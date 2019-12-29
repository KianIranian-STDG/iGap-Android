package net.iGap.viewmodel.sticker;

import androidx.lifecycle.MutableLiveData;

import net.iGap.api.apiService.ResponseCallback;
import net.iGap.api.errorhandler.ErrorModel;
import net.iGap.fragments.emoji.struct.StructIGStickerGroup;
import net.iGap.repository.sticker.StickerRepository;
import net.iGap.viewmodel.BaseViewModel;

import java.util.List;

public class RemoveStickerViewModel extends BaseViewModel {
    private StickerRepository repository;

    private MutableLiveData<Integer> removeStickerLiveData = new MutableLiveData<>();

    public RemoveStickerViewModel() {
        repository = new StickerRepository();
    }

    public List<StructIGStickerGroup> getFavoriteStickers() {
        return repository.getMyStickers();
    }

    public void removeStickerFromFavorite(String groupId, int adapterPosition) {
        repository.removeStickerGroupFromFavorite(groupId, new ResponseCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean data) {
                removeStickerLiveData.postValue(adapterPosition);
            }

            @Override
            public void onError(ErrorModel error) {

            }
        });
    }

    public MutableLiveData<Integer> getRemoveStickerLiveData() {
        return removeStickerLiveData;
    }

}
