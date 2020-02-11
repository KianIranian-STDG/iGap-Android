package net.iGap.viewmodel.sticker;

import android.view.View;

import androidx.lifecycle.MutableLiveData;

import net.iGap.R;
import net.iGap.fragments.emoji.struct.StructIGSticker;
import net.iGap.fragments.emoji.struct.StructIGStickerGroup;
import net.iGap.module.SingleLiveEvent;
import net.iGap.repository.sticker.StickerRepository;
import net.iGap.rx.IGSingleObserver;
import net.iGap.rx.ObserverViewModel;

public class StickerDialogViewModel extends ObserverViewModel {
    private static final String TAG = "abbasiSticker ViewModel";

    private static final int LIST = 0;
    private static final int PREVIEW = 1;

    private StickerRepository repository;
    private StructIGStickerGroup stickerGroup;

    private MutableLiveData<Integer> progressMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<Integer> addOrRemoveProgressLiveData = new MutableLiveData<>();
    private MutableLiveData<StructIGStickerGroup> stickersMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<Integer> addOrRemoveStickerLiveData = new MutableLiveData<>();
    private SingleLiveEvent<Boolean> favoriteStickerLiveData = new SingleLiveEvent<>();
    private MutableLiveData<Boolean> closeDialogMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<StructIGSticker> openPreviewViewLiveData = new MutableLiveData<>();
    private MutableLiveData<StructIGSticker> sendMessageLiveData = new MutableLiveData<>();

    private int fragmentMode = LIST;

    public StickerDialogViewModel(StructIGStickerGroup stickerGroup) {
        repository = StickerRepository.getInstance();
        this.stickerGroup = stickerGroup;
    }

    public void onAddOrRemoveStickerClicked() {
        if (fragmentMode == LIST) {
            if (stickerGroup.isInUserList()) {
                removeStickerToMyStickers(stickerGroup);
            } else {
                addStickerToMyStickers(stickerGroup);
            }
        } else if (fragmentMode == PREVIEW) {
            if (openPreviewViewLiveData.getValue() != null)
                sendMessageLiveData.postValue(openPreviewViewLiveData.getValue());
        }
    }

    public void getSticker() {
        progressMutableLiveData.postValue(View.VISIBLE);
        repository.getStickerGroup(stickerGroup.getGroupId())
                .subscribe(new IGSingleObserver<StructIGStickerGroup>(mainThreadDisposable) {
                    @Override
                    public void onSuccess(StructIGStickerGroup stickerGroup) {
                        progressMutableLiveData.postValue(View.GONE);

                        if (StickerDialogViewModel.this.stickerGroup != null && !StickerDialogViewModel.this.stickerGroup.hasData()) {
                            StickerDialogViewModel.this.stickerGroup = stickerGroup;
                        }

                        stickersMutableLiveData.postValue(stickerGroup);

                        onStickerFavoriteChange(stickerGroup.isInUserList());
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        progressMutableLiveData.postValue(View.GONE);
                    }
                });
    }

    private void addStickerToMyStickers(StructIGStickerGroup stickerGroup) {
        repository.addStickerGroupToMyStickers(stickerGroup)
                .subscribe(new IGSingleObserver<StructIGStickerGroup>(backgroundDisposable) {
                    @Override
                    public void onSuccess(StructIGStickerGroup stickerGroup) {
                        addOrRemoveProgressLiveData.postValue(View.GONE);
                        onStickerFavoriteChange(stickerGroup.isInUserList());
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        addOrRemoveProgressLiveData.postValue(View.GONE);
                        closeDialogMutableLiveData.postValue(true);
                    }
                });
    }

    private void removeStickerToMyStickers(StructIGStickerGroup stickerGroup) {
        repository.removeStickerGroupFromMyStickers(stickerGroup)
                .subscribe(new IGSingleObserver<StructIGStickerGroup>(backgroundDisposable) {
                    @Override
                    public void onSuccess(StructIGStickerGroup stickerGroup) {
                        favoriteStickerLiveData.postValue(false);
                        addOrRemoveProgressLiveData.postValue(View.GONE);
                        closeDialogMutableLiveData.postValue(true);
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        addOrRemoveProgressLiveData.postValue(View.GONE);
                        closeDialogMutableLiveData.postValue(false);
                    }
                });
    }

    public void onStickerInListModeClick(StructIGSticker structIGSticker) {
        fragmentMode = PREVIEW;
        openPreviewViewLiveData.postValue(structIGSticker);
    }

    public void onPreviewImageClicked() {
        fragmentMode = LIST;
        openPreviewViewLiveData.postValue(null);
        onStickerFavoriteChange(stickerGroup.isInUserList());
    }

    private void onStickerFavoriteChange(boolean favorite) {
        addOrRemoveStickerLiveData.postValue(favorite ? R.string.remove_sticker_with_size : R.string.add_sticker_with_size);
        favoriteStickerLiveData.postValue(favorite);
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

    public MutableLiveData<StructIGSticker> getOpenPreviewViewLiveData() {
        return openPreviewViewLiveData;
    }

    public MutableLiveData<StructIGSticker> getSendMessageLiveData() {
        return sendMessageLiveData;
    }

    public MutableLiveData<Integer> getAddOrRemoveProgressLiveData() {
        return addOrRemoveProgressLiveData;
    }

    public SingleLiveEvent<Boolean> getFavoriteStickerLiveData() {
        return favoriteStickerLiveData;
    }

    @Override
    public void subscribe() {

    }
}
