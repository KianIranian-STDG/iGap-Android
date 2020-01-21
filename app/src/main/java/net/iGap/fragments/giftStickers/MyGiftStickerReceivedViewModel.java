package net.iGap.fragments.giftStickers;

import android.view.View;

import androidx.databinding.ObservableInt;
import androidx.lifecycle.MutableLiveData;

import net.iGap.fragments.emoji.struct.StructIGGiftSticker;
import net.iGap.module.SingleLiveEvent;
import net.iGap.repository.sticker.StickerRepository;
import net.iGap.rx.IGSingleObserver;
import net.iGap.rx.ObserverViewModel;

import java.util.List;

public class MyGiftStickerReceivedViewModel extends ObserverViewModel {

    private MutableLiveData<List<StructIGGiftSticker>> loadStickerList = new MutableLiveData<>();
    private SingleLiveEvent<String> showRequestErrorMessage = new SingleLiveEvent<>();
    private ObservableInt showLoading = new ObservableInt(View.VISIBLE);
    private ObservableInt showRetryView = new ObservableInt(View.GONE);
    private ObservableInt showEmptyListMessage = new ObservableInt(View.GONE);

    @Override
    public void subscribe() {
        showLoading.set(View.VISIBLE);
        showEmptyListMessage.set(View.GONE);
        StickerRepository.getInstance().getMyActivatedGiftSticker()
                .subscribe(new IGSingleObserver<List<StructIGGiftSticker>>(mainThreadDisposable) {
                    @Override
                    public void onSuccess(List<StructIGGiftSticker> structIGGiftStickers) {
                        loadStickerList.postValue(structIGGiftStickers);

                        showLoading.set(View.GONE);

                        if (structIGGiftStickers.size() == 0)
                            showEmptyListMessage.set(View.VISIBLE);

                        showRetryView.set(View.GONE);
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);

                        showRetryView.set(View.VISIBLE);
                        showLoading.set(View.GONE);
                    }
                });
    }

    public void onRetryButtonClick() {
        showRetryView.set(View.GONE);
        subscribe();
    }

    public ObservableInt getShowLoading() {
        return showLoading;
    }

    public ObservableInt getShowRetryView() {
        return showRetryView;
    }

    public ObservableInt getShowEmptyListMessage() {
        return showEmptyListMessage;
    }

    public MutableLiveData<List<StructIGGiftSticker>> getLoadStickerList() {
        return loadStickerList;
    }

    public SingleLiveEvent<String> getShowRequestErrorMessage() {
        return showRequestErrorMessage;
    }
}
