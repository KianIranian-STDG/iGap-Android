package net.iGap.fragments.giftStickers;

import android.view.View;

import androidx.lifecycle.MutableLiveData;

import net.iGap.fragments.emoji.struct.StructIGStickerGroup;
import net.iGap.module.SingleLiveEvent;
import net.iGap.repository.StickerRepository;
import net.iGap.observers.rx.IGSingleObserver;
import net.iGap.observers.rx.ObserverViewModel;

import java.util.List;

public class GiftStickerPackageListViewModel extends ObserverViewModel {

    private MutableLiveData<Integer> isShowLoadingLiveData = new MutableLiveData<>();
    private MutableLiveData<Integer> isShowRetryViewLiveData = new MutableLiveData<>();
    private SingleLiveEvent<Boolean> goBack = new SingleLiveEvent<>();
    private SingleLiveEvent<StructIGStickerGroup> goToPackageItemPage = new SingleLiveEvent<>();

    private MutableLiveData<List<StructIGStickerGroup>> loadData = new MutableLiveData<>();

    private StickerRepository stickerRepository = StickerRepository.getInstance();

    @Override
    public void subscribe() {
        isShowLoadingLiveData.setValue(View.VISIBLE);
        stickerRepository.getGiftableStickers().subscribe(new IGSingleObserver<List<StructIGStickerGroup>>(mainThreadDisposable) {
            @Override
            public void onSuccess(List<StructIGStickerGroup> structIGStickerGroups) {
                loadData.postValue(structIGStickerGroups);
                isShowLoadingLiveData.postValue(View.GONE);
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                isShowLoadingLiveData.postValue(View.GONE);
                isShowRetryViewLiveData.postValue(View.VISIBLE);
            }
        });
    }

    public void onCancelButtonClick() {
        goBack.setValue(true);
    }

    public void onRetryButtonClicked() {
        isShowRetryViewLiveData.setValue(View.GONE);
        subscribe();
    }

    public void onGiftStickerPackageClicked(StructIGStickerGroup stickerGroup) {
        goToPackageItemPage.setValue(stickerGroup);
    }

    public MutableLiveData<Integer> getIsShowLoadingLiveData() {
        return isShowLoadingLiveData;
    }

    public MutableLiveData<Integer> getIsShowRetryViewLiveData() {
        return isShowRetryViewLiveData;
    }

    public MutableLiveData<List<StructIGStickerGroup>> getLoadData() {
        return loadData;
    }

    public SingleLiveEvent<Boolean> getGoBack() {
        return goBack;
    }

    public SingleLiveEvent<StructIGStickerGroup> getGoToPackageItemPage() {
        return goToPackageItemPage;
    }
}
