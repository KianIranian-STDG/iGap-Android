package net.iGap.fragments.giftStickers;

import android.view.View;

import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableInt;

import net.iGap.api.apiService.BaseAPIViewModel;
import net.iGap.module.SingleLiveEvent;

public class GiftStickerItemDetailViewModel extends BaseAPIViewModel {

    private ObservableBoolean isEnabledButton = new ObservableBoolean(true);
    private ObservableInt isShowLoading = new ObservableInt(View.GONE);
    private SingleLiveEvent<Boolean> goBack = new SingleLiveEvent<>();


    public void onPaymentButtonClicked() {
        isShowLoading.set(View.VISIBLE);
        isEnabledButton.set(false);
    }

    public void onCancelButtonClicked() {
        goBack.setValue(true);
    }

    public ObservableBoolean getIsEnabledButton() {
        return isEnabledButton;
    }

    public ObservableInt getIsShowLoading() {
        return isShowLoading;
    }

    public SingleLiveEvent<Boolean> getGoBack() {
        return goBack;
    }
}
