package net.iGap.fragments.giftStickers;

import android.view.View;

import androidx.databinding.ObservableInt;
import androidx.lifecycle.MutableLiveData;

import net.iGap.fragments.emoji.apiModels.CardDetailDataModel;
import net.iGap.fragments.emoji.struct.StructIGGiftSticker;
import net.iGap.module.SingleLiveEvent;
import net.iGap.observers.rx.ObserverViewModel;
import net.iGap.repository.StickerRepository;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.processors.PublishProcessor;

public class MyGiftStickerReceivedViewModel extends ObserverViewModel {
    private MutableLiveData<List<StructIGGiftSticker>> loadStickerList = new MutableLiveData<>();
    private MutableLiveData<Integer> loadMoreProgressLiveData = new MutableLiveData<>();

    private PublishProcessor<Integer> pagination = PublishProcessor.create();
    private int pages = 0;


    private SingleLiveEvent<String> showRequestErrorMessage = new SingleLiveEvent<>();
    private ObservableInt showLoading = new ObservableInt(View.VISIBLE);
    private ObservableInt showRetryView = new ObservableInt(View.GONE);
    private ObservableInt showEmptyListMessage = new ObservableInt(View.GONE);
    private SingleLiveEvent<CardDetailDataModel> cardDetailLiveEvent = new SingleLiveEvent<>();


    @Override
    public void subscribe() {
        Disposable disposable = pagination
                .onBackpressureDrop()
                .doOnNext(integer -> loadMoreProgressLiveData.postValue(View.VISIBLE))
                .concatMapSingle(page -> StickerRepository.getInstance().getMyActivatedGiftSticker(pages * 10, 10))
                .doOnError(throwable -> {
                    showRetryView.set(View.VISIBLE);
                    showLoading.set(View.GONE);
                    loadMoreProgressLiveData.postValue(View.GONE);
                })
                .onErrorReturn(throwable -> new ArrayList<>())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(structIGStickerGroups -> {
                    loadStickerList.postValue(structIGStickerGroups);
                    loadMoreProgressLiveData.postValue(View.GONE);
                });

        backgroundDisposable.add(disposable);
        pagination.onNext(pages);
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


    public MutableLiveData<Integer> getLoadMoreProgressLiveData() {
        return loadMoreProgressLiveData;
    }

    public void onPageEnded() {
        pages++;
        pagination.onNext(pages);
    }


}
