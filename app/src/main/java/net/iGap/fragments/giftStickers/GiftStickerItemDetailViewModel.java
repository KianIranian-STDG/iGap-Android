package net.iGap.fragments.giftStickers;

import android.view.View;

import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableInt;
import androidx.lifecycle.MutableLiveData;

import net.iGap.fragments.emoji.apiModels.Issue;
import net.iGap.fragments.emoji.apiModels.IssueDataModel;
import net.iGap.fragments.emoji.struct.StructIGSticker;
import net.iGap.module.SingleLiveEvent;
import net.iGap.repository.sticker.StickerRepository;
import net.iGap.rx.IGSingleObserver;
import net.iGap.rx.ObserverViewModel;

public class GiftStickerItemDetailViewModel extends ObserverViewModel {

    private ObservableBoolean isEnabledButton = new ObservableBoolean(true);
    private ObservableInt isShowLoading = new ObservableInt(View.INVISIBLE);
    private ObservableInt isShowRetry = new ObservableInt(View.INVISIBLE);
    private SingleLiveEvent<Boolean> goBack = new SingleLiveEvent<>();
    private StickerRepository stickerRepository = StickerRepository.getInstance();
    private MutableLiveData<String> getPaymentLiveData = new MutableLiveData<>();

    @Override
    public void subscribe() {

    }

    public void onPaymentButtonClicked(StructIGSticker structIGSticker) {
        isShowLoading.set(View.VISIBLE);
        isEnabledButton.set(false);
        // TODO: 1/20/20 clear hard code data
        stickerRepository.addIssue(structIGSticker.getId(), new Issue("4271241776", "09120423503", 1))
                .subscribe(new IGSingleObserver<IssueDataModel>(mainThreadDisposable) {
                    @Override
                    public void onSuccess(IssueDataModel issueDataModel) {
                        getPaymentLiveData.postValue(issueDataModel.getToken());
                        isShowLoading.set(View.GONE);
                        isEnabledButton.set(true);
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        isShowLoading.set(View.INVISIBLE);
                        isEnabledButton.set(false);
                        isShowRetry.set(View.VISIBLE);
                    }
                });
    }

    public void onRetryIconClick(StructIGSticker structIGSticker) {
        isShowRetry.set(View.INVISIBLE);
        onPaymentButtonClicked(structIGSticker);
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

    public MutableLiveData<String> getGetPaymentLiveData() {
        return getPaymentLiveData;
    }

    public ObservableInt getIsShowRetry() {
        return isShowRetry;
    }

    public SingleLiveEvent<Boolean> getGoBack() {
        return goBack;
    }
}
