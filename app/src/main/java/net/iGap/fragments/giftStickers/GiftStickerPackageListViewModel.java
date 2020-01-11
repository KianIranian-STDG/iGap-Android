package net.iGap.fragments.giftStickers;

import android.os.Handler;
import android.view.View;

import androidx.databinding.ObservableInt;
import androidx.lifecycle.MutableLiveData;

import net.iGap.api.apiService.BaseAPIViewModel;
import net.iGap.module.SingleLiveEvent;

import java.util.ArrayList;
import java.util.List;

public class GiftStickerPackageListViewModel extends BaseAPIViewModel {

    private ObservableInt isShowLoading = new ObservableInt(View.VISIBLE);
    private ObservableInt isShowRetryView = new ObservableInt(View.GONE);

    private MutableLiveData<List<String>> loadData = new MutableLiveData<>();
    private SingleLiveEvent<Boolean> goBack = new SingleLiveEvent<>();
    private SingleLiveEvent<String> goToPackageItemPage = new SingleLiveEvent<>();

    private List<String> giftStickerList;

    public GiftStickerPackageListViewModel() {
        giftStickerList = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            giftStickerList.add(String.valueOf(i));
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                isShowLoading.set(View.GONE);
                loadData.setValue(giftStickerList);
            }
        }, 2000);
    }

    public void onCancelButtonClick() {
        goBack.setValue(true);
    }

    public void onRetryButtonClicked() {
        isShowRetryView.set(View.GONE);
        isShowLoading.set(View.VISIBLE);
        if (giftStickerList != null) {
            giftStickerList.clear();
        } else {
            giftStickerList = new ArrayList<>();
        }
        for (int i = 0; i < 10; i++) {
            giftStickerList.add(String.valueOf(i));
        }
        loadData.setValue(giftStickerList);
    }

    public void onGiftStickerPackageClicked(int position) {
        goToPackageItemPage.setValue(giftStickerList.get(position));
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

    public SingleLiveEvent<String> getGoToPackageItemPage() {
        return goToPackageItemPage;
    }
}
