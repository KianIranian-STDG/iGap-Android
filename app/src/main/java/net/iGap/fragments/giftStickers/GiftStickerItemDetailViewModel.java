package net.iGap.fragments.giftStickers;

import android.util.Log;
import android.view.View;

import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableInt;

import net.iGap.G;
import net.iGap.fragments.emoji.apiModels.IssueDataModel;
import net.iGap.fragments.emoji.struct.StructIGSticker;
import net.iGap.module.SingleLiveEvent;
import net.iGap.module.accountManager.AccountManager;
import net.iGap.observers.rx.IGSingleObserver;
import net.iGap.observers.rx.ObserverViewModel;
import net.iGap.repository.StickerRepository;

public class GiftStickerItemDetailViewModel extends ObserverViewModel {

    private ObservableBoolean isEnabledButton = new ObservableBoolean(true);
    private ObservableInt isShowLoading = new ObservableInt(View.INVISIBLE);
    private ObservableInt isShowRetry = new ObservableInt(View.INVISIBLE);
    private SingleLiveEvent<Boolean> goBack = new SingleLiveEvent<>();
    private StickerRepository stickerRepository = StickerRepository.getInstance();
    private SingleLiveEvent<IssueDataModel> getPaymentLiveData = new SingleLiveEvent<>();

    @Override
    public void subscribe() {

    }

    public void onPaymentButtonClicked(StructIGSticker structIGSticker) {
        isShowLoading.set(View.VISIBLE);
        isEnabledButton.set(false);

        String phoneNumber = AccountManager.getInstance().getCurrentUser().getPhoneNumber();
        if (phoneNumber != null && phoneNumber.length() > 2 && phoneNumber.substring(0, 2).equals("98")) {
            phoneNumber = "0" + phoneNumber.substring(2);
            stickerRepository.addIssue(structIGSticker.getId(), phoneNumber, G.getNationalCode())
                    .subscribe(new IGSingleObserver<IssueDataModel>(mainThreadDisposable) {
                        @Override
                        public void onSuccess(IssueDataModel issueDataModel) {
                            Log.e("jdhfjhfjsdhf", "onSuccess: "+issueDataModel.getToken() );
                            getPaymentLiveData.postValue(issueDataModel);
                            isShowLoading.set(View.GONE);
                            isEnabledButton.set(true);
                        }

                        @Override
                        public void onError(Throwable e) {
                            super.onError(e);
                            Log.e("jdhfjhfjsdhf", "onError: "+e );
                            isShowLoading.set(View.INVISIBLE);
                            isEnabledButton.set(false);
                            isShowRetry.set(View.VISIBLE);
                        }
                    });
        }
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

    public SingleLiveEvent<IssueDataModel> getGetPaymentLiveData() {
        return getPaymentLiveData;
    }

    public ObservableInt getIsShowRetry() {
        return isShowRetry;
    }

    public SingleLiveEvent<Boolean> getGoBack() {
        return goBack;
    }
}
