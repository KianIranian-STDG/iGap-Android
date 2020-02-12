package net.iGap.viewmodel;

import android.view.View;

import androidx.lifecycle.MutableLiveData;

import net.iGap.fragments.emoji.struct.StructIGStickerCategory;
import net.iGap.fragments.emoji.struct.StructIGStickerGroup;
import net.iGap.module.SingleLiveEvent;
import net.iGap.repository.StickerRepository;
import net.iGap.observers.rx.IGSingleObserver;
import net.iGap.observers.rx.ObserverViewModel;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.processors.PublishProcessor;

public class AddStickerViewModel extends ObserverViewModel {
    private StickerRepository repository;
    private StructIGStickerCategory category;

    private MutableLiveData<StructIGStickerGroup> openStickerDetailLiveData = new MutableLiveData<>();
    private MutableLiveData<List<StructIGStickerGroup>> stickerGroupLiveData = new MutableLiveData<>();
    private SingleLiveEvent<Boolean> buttonStatusChangedLiveData = new SingleLiveEvent<>();

    private MutableLiveData<Integer> loadMoreProgressLiveData = new MutableLiveData<>();

    private PublishProcessor<Integer> pagination = PublishProcessor.create();
    private int page = 0;
    private int limit = 20;
    private String TAG = "abbasiNewSticker";

    public AddStickerViewModel() {
        repository = StickerRepository.getInstance();
    }

    public void setCategory(StructIGStickerCategory category) {
        this.category = category;
    }

    @Override
    public void subscribe() {
        Disposable disposable = pagination
                .onBackpressureDrop()
                .doOnNext(integer -> loadMoreProgressLiveData.postValue(View.VISIBLE))
                .concatMapSingle(page -> repository.getCategoryStickerGroups(category.getId(), page * limit, limit))
                .doOnError(throwable -> loadMoreProgressLiveData.postValue(View.GONE))
                .onErrorReturn(throwable -> new ArrayList<>())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(structIGStickerGroups -> {
                    stickerGroupLiveData.postValue(structIGStickerGroups);
                    loadMoreProgressLiveData.postValue(View.GONE);
                });

        backgroundDisposable.add(disposable);

        pagination.onNext(page);
    }

    public void onItemCellClicked(StructIGStickerGroup stickerGroup) {
        openStickerDetailLiveData.postValue(stickerGroup);
    }

    public void onItemButtonClicked(StructIGStickerGroup stickerGroup, OnClickResult clickResult) {
        if (stickerGroup.isInUserList()) {
            repository.removeStickerGroupFromMyStickers(stickerGroup)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new IGSingleObserver<StructIGStickerGroup>(backgroundDisposable) {
                        @Override
                        public void onSuccess(StructIGStickerGroup stickerGroup) {
                            clickResult.onResult(stickerGroup);
                        }

                        @Override
                        public void onError(Throwable e) {
                            super.onError(e);
                            clickResult.onResult(stickerGroup);
                        }
                    });
        } else {
            repository.addStickerGroupToMyStickers(stickerGroup)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new IGSingleObserver<StructIGStickerGroup>(backgroundDisposable) {
                        @Override
                        public void onSuccess(StructIGStickerGroup stickerGroup) {
                            clickResult.onResult(stickerGroup);
                        }

                        @Override
                        public void onError(Throwable e) {
                            super.onError(e);
                            clickResult.onResult(stickerGroup);
                        }
                    });
        }
    }

    public MutableLiveData<StructIGStickerGroup> getOpenStickerDetailLiveData() {
        return openStickerDetailLiveData;
    }

    public MutableLiveData<Integer> getLoadMoreProgressLiveData() {
        return loadMoreProgressLiveData;
    }

    public MutableLiveData<List<StructIGStickerGroup>> getStickerGroupLiveData() {
        return stickerGroupLiveData;
    }

    public SingleLiveEvent<Boolean> getButtonStatusChangedLiveData() {
        return buttonStatusChangedLiveData;
    }

    public void onPageEnded() {
        page++;
        pagination.onNext(page);
    }

    public void onButtonStatusChanged(boolean addedInUserList) {
        buttonStatusChangedLiveData.postValue(addedInUserList);
    }

    public interface OnClickResult {
        void onResult(StructIGStickerGroup sticker);
    }
}
