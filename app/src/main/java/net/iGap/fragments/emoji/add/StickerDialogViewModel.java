package net.iGap.fragments.emoji.add;

import android.view.View;

import androidx.lifecycle.MutableLiveData;

import net.iGap.viewmodel.BaseCPayViewModel;
import net.iGap.viewmodel.BaseViewModel;

import java.util.List;

public class StickerDialogViewModel extends BaseViewModel {
    private StickerRepository repository;
    private MutableLiveData<Integer> progressMutableLiveData;
    private MutableLiveData<Boolean> closeDialogMutableLiveData;

    StickerDialogViewModel() {
        repository = new StickerRepository();
        progressMutableLiveData = new MutableLiveData<>();
        closeDialogMutableLiveData = new MutableLiveData<>();
    }

    public void getSticker(String groupId, OnLoadSticker onLoadSticker) {
        progressMutableLiveData.postValue(View.VISIBLE);
        repository.getStickerListForAddDialog(groupId, new BaseCPayViewModel<List<StructIGSticker>>() {
            @Override
            public void onSuccess(List<StructIGSticker> data) {
                onLoadSticker.onStickerLoaded(data);
                progressMutableLiveData.postValue(View.GONE);
            }

            @Override
            public void onError(String error) {
                progressMutableLiveData.postValue(View.GONE);
            }
        });
    }

    public void addSticker(String groupId) {
        progressMutableLiveData.postValue(View.VISIBLE);
        repository.addSticker(groupId, new BaseCPayViewModel<Boolean>() {
            @Override
            public void onSuccess(Boolean data) {
                closeDialogMutableLiveData.postValue(data);
                progressMutableLiveData.postValue(View.GONE);
            }

            @Override
            public void onError(String error) {
                progressMutableLiveData.postValue(View.GONE);
            }
        });
    }

    public MutableLiveData<Integer> getProgressMutableLiveData() {
        return progressMutableLiveData;
    }

    public MutableLiveData<Boolean> getCloseDialogMutableLiveData() {
        return closeDialogMutableLiveData;
    }

    public interface OnLoadSticker {
        void onStickerLoaded(List<StructIGSticker> structIGStickers);
    }
}
