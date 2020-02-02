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

import io.reactivex.android.schedulers.AndroidSchedulers;

public class MyGiftStickerBuyViewModel extends ObserverViewModel {

    private MutableLiveData<List<StructIGGiftSticker>> loadStickerList = new MutableLiveData<>();
    private SingleLiveEvent<String> showRequestErrorMessage = new SingleLiveEvent<>();
    private SingleLiveEvent<StructIGGiftSticker> goNext = new SingleLiveEvent<>();
    private ObservableInt showLoading = new ObservableInt(View.VISIBLE);
    private ObservableInt showRetryView = new ObservableInt(View.GONE);
    private ObservableInt showEmptyListMessage = new ObservableInt(View.GONE);

    @Override
    public void subscribe() {
        showLoading.set(View.VISIBLE);
        showEmptyListMessage.set(View.GONE);
        StickerRepository.getInstance().getMyGiftStickerBuy()
                .subscribe(new IGSingleObserver<List<StructIGGiftSticker>>(mainThreadDisposable) {
                    @Override
                    public void onSuccess(List<StructIGGiftSticker> structIGGiftStickers) {
                        loadStickerList.postValue(structIGGiftStickers);
                        showLoading.set(View.GONE);

                        if (structIGGiftStickers.size() == 0) {
                            showEmptyListMessage.set(View.VISIBLE);
                        }

                        showRetryView.set(View.GONE);
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        showLoading.set(View.GONE);
                        showRetryView.set(View.VISIBLE);
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

    public SingleLiveEvent<StructIGGiftSticker> getGoNext() {
        return goNext;
    }

    public SingleLiveEvent<String> getShowRequestErrorMessage() {
        return showRequestErrorMessage;
    }

    public void onItemClicked(StructIGGiftSticker giftSticker, MyStickerListAdapter.ProgressDelegate progressDelegate) {
        progressDelegate.onView(View.VISIBLE);
        StickerRepository.getInstance().getCardStatus(giftSticker.getGiftId())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnEvent((giftSticker1, throwable) -> progressDelegate.onView(View.GONE))
                .subscribe(new IGSingleObserver<StructIGGiftSticker>(mainThreadDisposable) {
                    @Override
                    public void onSuccess(StructIGGiftSticker structIGGiftSticker) {
                        giftSticker.setActive(structIGGiftSticker.isActive());
                        giftSticker.setValid(structIGGiftSticker.isValid());
                        giftSticker.setForward(structIGGiftSticker.isForward());
                        goNext.postValue(giftSticker);
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        goNext.postValue(null);
                    }
                });
    }
}
