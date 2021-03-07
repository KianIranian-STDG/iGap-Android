package net.iGap.viewmodel.sticker;

import android.view.View;

import androidx.lifecycle.MutableLiveData;

import net.iGap.G;
import net.iGap.R;
import net.iGap.fragments.emoji.struct.StructIGSticker;
import net.iGap.fragments.emoji.struct.StructIGStickerGroup;
import net.iGap.module.accountManager.AccountManager;
import net.iGap.observers.eventbus.EventManager;
import net.iGap.observers.rx.IGSingleObserver;
import net.iGap.observers.rx.ObserverViewModel;
import net.iGap.repository.StickerRepository;

public class StickerDialogViewModel extends ObserverViewModel {
    private static final String TAG = "abbasiSticker ViewModel";

    private static final int LIST = 0;
    private static final int PREVIEW = 1;

    private StickerRepository repository;
    private StructIGStickerGroup stickerGroup;

    private MutableLiveData<Integer> progressMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<Integer> addOrRemoveProgressLiveData = new MutableLiveData<>();
    private MutableLiveData<Integer> retryViewLiveData = new MutableLiveData<>();
    private MutableLiveData<StructIGStickerGroup> stickersMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<Integer> addOrRemoveStickerLiveData = new MutableLiveData<>();
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

                        retryViewLiveData.postValue(View.GONE);

                        onStickerFavoriteChange(stickerGroup.isInUserList());
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        progressMutableLiveData.postValue(View.GONE);
                        retryViewLiveData.postValue(View.VISIBLE);
                    }
                });
    }

    public void onRetryViewClicked() {
        retryViewLiveData.setValue(View.GONE);
        getSticker();
    }

    private void addStickerToMyStickers(StructIGStickerGroup stickerGroup) {
        addOrRemoveProgressLiveData.postValue(View.VISIBLE);
        repository.addStickerGroupToMyStickers(stickerGroup)
                .subscribe(new IGSingleObserver<StructIGStickerGroup>(backgroundDisposable) {
                    @Override
                    public void onSuccess(StructIGStickerGroup stickerGroup) {
                        addOrRemoveProgressLiveData.postValue(View.GONE);
                        onStickerFavoriteChange(stickerGroup.isInUserList());
                        G.runOnUiThread(() -> EventManager.getInstance(AccountManager.selectedAccount).postEvent(EventManager.STICKER_CHANGED, stickerGroup.getGroupId(), stickerGroup.isInUserList()));
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
        addOrRemoveProgressLiveData.postValue(View.VISIBLE);
        repository.removeStickerGroupFromMyStickers(stickerGroup)
                .subscribe(new IGSingleObserver<StructIGStickerGroup>(backgroundDisposable) {
                    @Override
                    public void onSuccess(StructIGStickerGroup stickerGroup) {
                        G.runOnUiThread(() -> EventManager.getInstance(AccountManager.selectedAccount).postEvent(EventManager.STICKER_CHANGED, stickerGroup.getGroupId(), false));
                        onStickerFavoriteChange(false);
                        addOrRemoveProgressLiveData.postValue(View.GONE);
                        closeDialogMutableLiveData.postValue(true);
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        addOrRemoveProgressLiveData.postValue(View.GONE);
                        closeDialogMutableLiveData.postValue(true);
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

    public MutableLiveData<Integer> getRetryViewLiveData() {
        return retryViewLiveData;
    }

    @Override
    public void subscribe() {

    }
}
