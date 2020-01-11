package net.iGap.fragments.giftStickers;

import android.view.View;

import androidx.databinding.ObservableInt;
import androidx.lifecycle.MutableLiveData;

import net.iGap.api.apiService.BaseAPIViewModel;
import net.iGap.module.SingleLiveEvent;

import java.util.ArrayList;
import java.util.List;

public class GiftStickerItemListViewModel extends BaseAPIViewModel {

    private ObservableInt isShowLoading = new ObservableInt(View.VISIBLE);
    private ObservableInt isShowRetryView = new ObservableInt(View.GONE);

    private MutableLiveData<List<String>> loadData = new MutableLiveData<>();
    private SingleLiveEvent<Boolean> goBack = new SingleLiveEvent<>();
    private SingleLiveEvent<String> goToBuyItemPage = new SingleLiveEvent<>();
    private SingleLiveEvent<String> goToShowDetailPage = new SingleLiveEvent<>();

    private List<String> giftStickerList;

    public GiftStickerItemListViewModel(){
        isShowLoading.set(View.VISIBLE);
        giftStickerList = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            giftStickerList.add(String.valueOf(i));
        }
        isShowLoading.set(View.GONE);
        loadData.setValue(giftStickerList);
    }

    public void onCancelButtonClick(){
        goBack.setValue(true);
    }

    public void onGiftStickerItemClicked(int position){
        goToShowDetailPage.setValue(giftStickerList.get(position));
    }

    public void onRetryButtonClicked(){
        isShowRetryView.set(View.GONE);
        isShowLoading.set(View.VISIBLE);
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

    public SingleLiveEvent<String> getGoToBuyItemPage() {
        return goToBuyItemPage;
    }

    public SingleLiveEvent<String> getGoToShowDetailPage() {
        return goToShowDetailPage;
    }
}
