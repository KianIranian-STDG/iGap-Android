package net.iGap.viewmodel.sticker;

import android.view.View;

import androidx.lifecycle.MutableLiveData;

import net.iGap.api.apiService.ResponseCallback;
import net.iGap.api.errorhandler.ErrorModel;
import net.iGap.fragments.emoji.struct.StructIGStickerGroup;
import net.iGap.repository.sticker.StickerRepository;
import net.iGap.viewmodel.BaseViewModel;

import java.util.List;

public class RemoveStickerViewModel extends BaseViewModel {
    private StickerRepository repository;

    private MutableLiveData<Integer> removeProgressLiveData = new MutableLiveData<>();
    private MutableLiveData<Integer> removeStickerLiveData = new MutableLiveData<>();
    private MutableLiveData<Integer> removeDialogLiveData = new MutableLiveData<>();

    public RemoveStickerViewModel() {
        repository = new StickerRepository();
    }

    public List<StructIGStickerGroup> getFavoriteStickers() {
        removeProgressLiveData.postValue(View.GONE);
        return repository.getFavoriteStickers();
    }

    public void onRemoveStickerClicked(int pos) {
        removeDialogLiveData.postValue(pos);
    }

    public void removeStickerFromFavorite(String groupId, int adapterPosition) {
        removeProgressLiveData.postValue(View.VISIBLE);
        repository.removeStickerGroupFromFavorite(groupId, new ResponseCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean data) {
                removeStickerLiveData.postValue(adapterPosition);
                removeProgressLiveData.postValue(View.GONE);
            }

            @Override
            public void onError(ErrorModel error) {
                removeProgressLiveData.postValue(View.GONE);
            }
        });
    }

    public MutableLiveData<Integer> getRemoveProgressLiveData() {
        return removeProgressLiveData;
    }

    public MutableLiveData<Integer> getRemoveStickerLiveData() {
        return removeStickerLiveData;
    }

    public MutableLiveData<Integer> getRemoveDialogLiveData() {
        return removeDialogLiveData;
    }
}
