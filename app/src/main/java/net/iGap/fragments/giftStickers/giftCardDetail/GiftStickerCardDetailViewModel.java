package net.iGap.fragments.giftStickers.giftCardDetail;

import android.view.View;

import androidx.databinding.ObservableInt;
import androidx.lifecycle.ViewModel;

import net.iGap.module.SingleLiveEvent;

public class GiftStickerCardDetailViewModel extends ViewModel {

    //Must be invisible for not change view size
    private ObservableInt showMainView = new ObservableInt(View.INVISIBLE);
    private ObservableInt showLoadingView = new ObservableInt(View.VISIBLE);
    private ObservableInt showRetryView = new ObservableInt(View.GONE);
    private SingleLiveEvent<String> copyValue = new SingleLiveEvent<>();

    private String cardId;

    GiftStickerCardDetailViewModel(String cardId){
        this.cardId = cardId;
    }

    public void onRetryViewClicked(){

    }

    public void copyValue(String value){
        copyValue.setValue(value);
    }


    public ObservableInt getShowMainView() {
        return showMainView;
    }

    public ObservableInt getShowLoadingView() {
        return showLoadingView;
    }

    public ObservableInt getShowRetryView() {
        return showRetryView;
    }

    public SingleLiveEvent<String> getCopyValue() {
        return copyValue;
    }
}
