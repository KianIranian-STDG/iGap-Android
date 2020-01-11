package net.iGap.viewmodel;

import android.view.View;

import androidx.lifecycle.MutableLiveData;

import net.iGap.api.apiService.ResponseCallback;
import net.iGap.fragments.emoji.struct.StructIGStickerCategory;
import net.iGap.fragments.emoji.struct.StructIGStickerGroup;
import net.iGap.repository.sticker.StickerRepository;
import net.iGap.rx.ObserverViewModel;

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

    private MutableLiveData<Integer> loadMoreProgressLiveData = new MutableLiveData<>();

    private PublishProcessor<Integer> pagination = PublishProcessor.create();
    private int page = 0;
    private int limit = 20;

    public AddStickerViewModel(StructIGStickerCategory category) {
        this.category = category;
        repository = StickerRepository.getInstance();
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

        compositeDisposable.add(disposable);

        pagination.onNext(page);
    }

    public void onItemCellClicked(StructIGStickerGroup stickerGroup) {
        openStickerDetailLiveData.postValue(stickerGroup);
    }

    public void onItemButtonClicked(StructIGStickerGroup stickerGroup, OnClickResult clickResult) {
        if (stickerGroup.isFavorite()) {
            repository.removeStickerGroupFromFavorite(stickerGroup.getGroupId(), new ResponseCallback<Boolean>() {
                @Override
                public void onSuccess(Boolean data) {
                    stickerGroup.setFavorite(false);
                    clickResult.onResult(stickerGroup);
                }

                @Override
                public void onError(String error) {
                    clickResult.onResult(stickerGroup);
                }

                @Override
                public void onFailed() {

                }
            });
        } else {
            repository.addStickerGroupToFavorite(stickerGroup.getGroupId(), new ResponseCallback<Boolean>() {
                @Override
                public void onSuccess(Boolean data) {
                    stickerGroup.setFavorite(true);
                    clickResult.onResult(stickerGroup);
                }

                @Override
                public void onError(String error) {
                    clickResult.onResult(stickerGroup);
                }

                @Override
                public void onFailed() {

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

    public void onPageEnded() {
        page++;
        pagination.onNext(page);
    }

    public interface OnClickResult {
        void onResult(StructIGStickerGroup sticker);
    }
}
