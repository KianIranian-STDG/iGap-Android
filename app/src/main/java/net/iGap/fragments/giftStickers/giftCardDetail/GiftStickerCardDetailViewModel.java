package net.iGap.fragments.giftStickers.giftCardDetail;

import android.view.View;

import androidx.databinding.ObservableInt;

import net.iGap.AccountManager;
import net.iGap.G;
import net.iGap.fragments.emoji.apiModels.CardDetailDataModel;
import net.iGap.fragments.emoji.struct.StructIGSticker;
import net.iGap.module.SingleLiveEvent;
import net.iGap.repository.sticker.StickerRepository;
import net.iGap.rx.IGSingleObserver;
import net.iGap.rx.ObserverViewModel;

public class GiftStickerCardDetailViewModel extends ObserverViewModel {

    private ObservableInt showMainView = new ObservableInt(View.INVISIBLE);
    private ObservableInt showLoadingView = new ObservableInt(View.VISIBLE);
    private ObservableInt showRetryView = new ObservableInt(View.GONE);
    private SingleLiveEvent<String> copyValue = new SingleLiveEvent<>();
    private SingleLiveEvent<CardDetailDataModel> cardDetailLiveData = new SingleLiveEvent<>();

    private StructIGSticker structIGSticker;
    private int mode;

    GiftStickerCardDetailViewModel(StructIGSticker structIGSticker, int mode) {
        this.structIGSticker = structIGSticker;
        this.mode = mode;
    }

    @Override
    public void subscribe() {
        showLoadingView.set(View.VISIBLE);
        String phoneNumber = AccountManager.getInstance().getCurrentUser().getPhoneNumber();

        if (phoneNumber.length() > 2 && phoneNumber.substring(0, 2).equals("98")) {
            phoneNumber = "0" + phoneNumber.substring(2);
            if (mode == 0) {
                StickerRepository.getInstance().getCardInfo(structIGSticker.getGiftId(), G.getNationalCode(), phoneNumber)
                        .subscribe(new IGSingleObserver<CardDetailDataModel>(mainThreadDisposable) {
                            @Override
                            public void onSuccess(CardDetailDataModel cardDetailDataModel) {
                                if (cardDetailDataModel != null)
                                    cardDetailLiveData.postValue(cardDetailDataModel);
                                showMainView.set(View.VISIBLE);
                                showLoadingView.set(View.GONE);
                            }

                            @Override
                            public void onError(Throwable e) {
                                super.onError(e);
                                showLoadingView.set(View.GONE);
                                showRetryView.set(View.VISIBLE);
                            }
                        });
            } else if (mode == 1) {
                StickerRepository.getInstance().getGiftCardInfo(phoneNumber, G.getNationalCode(), structIGSticker.getGiftId())
                        .subscribe(new IGSingleObserver<CardDetailDataModel>(mainThreadDisposable) {
                            @Override
                            public void onSuccess(CardDetailDataModel cardDetailDataModel) {
                                if (cardDetailDataModel != null) {
                                    cardDetailLiveData.postValue(cardDetailDataModel);
                                }
                                showMainView.set(View.VISIBLE);
                                showLoadingView.set(View.GONE);
                            }

                            @Override
                            public void onError(Throwable e) {
                                super.onError(e);
                                showLoadingView.set(View.GONE);
                                showRetryView.set(View.VISIBLE);
                            }
                        });
            }
        }
    }

    public void onRetryViewClicked() {
        showRetryView.set(View.GONE);
        subscribe();
    }

    public void copyValue(String value) {
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

    public SingleLiveEvent<CardDetailDataModel> getCardDetailLiveData() {
        return cardDetailLiveData;
    }
}
