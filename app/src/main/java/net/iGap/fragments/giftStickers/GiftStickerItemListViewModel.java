package net.iGap.fragments.giftStickers;

import android.view.View;

import androidx.databinding.ObservableInt;
import androidx.lifecycle.MutableLiveData;

import net.iGap.api.apiService.BaseAPIViewModel;
import net.iGap.fragments.emoji.struct.StructIGSticker;
import net.iGap.module.SingleLiveEvent;

import java.util.List;

public class GiftStickerItemListViewModel extends BaseAPIViewModel {

    private ObservableInt isShowLoading = new ObservableInt(View.GONE);
    private ObservableInt isShowRetryView = new ObservableInt(View.GONE);

    private MutableLiveData<List<String>> loadData = new MutableLiveData<>();
    private SingleLiveEvent<Boolean> goBack = new SingleLiveEvent<>();
    /*private SingleLiveEvent<String> goToBuyItemPage = new SingleLiveEvent<>();*/
    private SingleLiveEvent<StructIGSticker> goToShowDetailPage = new SingleLiveEvent<>();

    public void onCancelButtonClick() {
        goBack.setValue(true);
    }

    public void onGiftStickerItemClicked(StructIGSticker sticker) {
        goToShowDetailPage.setValue(sticker);
    }

    public void onRetryButtonClicked() {
        isShowRetryView.set(View.GONE);
        isShowLoading.set(View.GONE);
    }

    public ObservableInt getIsShowLoading() {
        return isShowLoading;
    }

    public ObservableInt getIsShowRetryView() {
        return isShowRetryView;
    }

    public MutableLiveData<List<String>> getLoadData() {
        return loadData;
    }

    public SingleLiveEvent<Boolean> getGoBack() {
        return goBack;
    }

    /*public SingleLiveEvent<String> getGoToBuyItemPage() {
        return goToBuyItemPage;
    }*/

    public SingleLiveEvent<StructIGSticker> getGoToShowDetailPage() {
        return goToShowDetailPage;
    }
}
