package net.iGap.fragments.giftStickers;

import android.view.View;

import androidx.lifecycle.MutableLiveData;

import net.iGap.G;
import net.iGap.fragments.emoji.struct.StructIGGiftSticker;
import net.iGap.module.SingleLiveEvent;
import net.iGap.observers.eventbus.EventManager;
import net.iGap.observers.rx.IGSingleObserver;
import net.iGap.observers.rx.ObserverViewModel;
import net.iGap.repository.StickerRepository;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.processors.PublishProcessor;

public class MyGiftStickerBuyViewModel extends ObserverViewModel {

    private MutableLiveData<List<StructIGGiftSticker>> loadStickerList = new MutableLiveData<>();
    private SingleLiveEvent<String> showRequestErrorMessage = new SingleLiveEvent<>();
    private SingleLiveEvent<StructIGGiftSticker> goNext = new SingleLiveEvent<>();
    private MutableLiveData<Integer> showLoading = new MutableLiveData<>();
    private MutableLiveData<Integer> showRetryView = new MutableLiveData<>();
    private MutableLiveData<Integer> showEmptyListMessage = new MutableLiveData<>();

    private PublishProcessor<Integer> pagination = PublishProcessor.create();

    private String mode;
    private int pages = 0;

    @Override
    public void subscribe() {
        EventManager.getInstance().addEventListener(EventManager.STICKER_CHANGED, this);

        showLoading.postValue(View.VISIBLE);
        showEmptyListMessage.postValue(View.GONE);

        Disposable disposable = pagination
                .onBackpressureDrop()
                .doOnNext(integer -> showLoading.postValue(View.VISIBLE))
                .concatMapSingle(page -> StickerRepository.getInstance().getMyGiftStickerBuy(mode, pages * 10, 10))
                .doOnError(throwable -> {
                    showRetryView.postValue(View.VISIBLE);
                    showLoading.postValue(View.GONE);
                    showLoading.postValue(View.GONE);
                })
                .onErrorReturn(throwable -> new ArrayList<>())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(structIGStickerGroups -> {
                    loadStickerList.postValue(structIGStickerGroups);
                    showLoading.postValue(View.GONE);
                });

        backgroundDisposable.add(disposable);
        pagination.onNext(pages);
    }

//    private void getData() {
//        showLoading.postValue(View.VISIBLE);
//        showEmptyListMessage.postValue(View.GONE);
//        StickerRepository.getInstance().getMyGiftStickerBuy(mode)
//                .subscribe(new IGSingleObserver<List<StructIGGiftSticker>>(mainThreadDisposable) {
//                    @Override
//                    public void onSuccess(List<StructIGGiftSticker> structIGGiftStickers) {
//                        loadStickerList.postValue(structIGGiftStickers);
//                        showLoading.postValue(View.GONE);
//
//                        if (structIGGiftStickers.size() == 0) {
//                            showEmptyListMessage.postValue(View.VISIBLE);
//                        }
//
//                        showRetryView.postValue(View.GONE);
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        super.onError(e);
//                        showLoading.postValue(View.GONE);
//                        showRetryView.postValue(View.VISIBLE);
//                    }
//                });
//    }

    public void onRetryButtonClick() {
        showRetryView.postValue(View.GONE);
        subscribe();
    }

    public MutableLiveData<Integer> getShowLoading() {
        return showLoading;
    }

    public MutableLiveData<Integer> getShowRetryView() {
        return showRetryView;
    }

    public MutableLiveData<Integer> getShowEmptyListMessage() {
        return showEmptyListMessage;
    }

    public MutableLiveData<List<StructIGGiftSticker>> getLoadStickerList() {
        return loadStickerList;
    }

    public void setMode(int mode) {
        if (mode == GiftStickerPurchasedByMeFragment.NEW) {
            this.mode = "new";
        } else if (mode == GiftStickerPurchasedByMeFragment.ACTIVE) {
            this.mode = "active";
        } else {
            this.mode = "forwarded";
        }
        pagination.onNext(pages);
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventManager.getInstance().removeEventListener(EventManager.STICKER_CHANGED, this);
    }

    @Override
    public void receivedMessage(int id, Object... message) {
        super.receivedMessage(id, message);
//        G.handler.post(this::getData);
        G.handler.post(() -> pagination.onNext(pages));
    }

    public void onPageEnded() {
        pages++;
        pagination.onNext(pages);
    }
}
