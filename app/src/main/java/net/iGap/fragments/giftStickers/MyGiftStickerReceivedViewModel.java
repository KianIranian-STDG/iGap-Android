package net.iGap.fragments.giftStickers;

import android.view.View;

import androidx.databinding.ObservableInt;
import androidx.lifecycle.MutableLiveData;

import net.iGap.fragments.emoji.apiModels.UserGiftStickersDataModel;
import net.iGap.module.SingleLiveEvent;
import net.iGap.repository.sticker.StickerRepository;
import net.iGap.rx.IGSingleObserver;
import net.iGap.rx.ObserverViewModel;

import java.util.List;

public class MyGiftStickerReceivedViewModel extends ObserverViewModel {

    private MutableLiveData<List<String>> loadStickerList = new MutableLiveData<>();
    private SingleLiveEvent<String> showRequestErrorMessage = new SingleLiveEvent<>();
    private ObservableInt showLoading = new ObservableInt(View.VISIBLE);
    private ObservableInt showRetryView = new ObservableInt(View.GONE);
    private ObservableInt showEmptyListMessage = new ObservableInt(View.GONE);

    private StickerRepository repository = StickerRepository.getInstance();

    @Override
    public void subscribe() {
        repository.getUserGiftSticker()
                .subscribe(new IGSingleObserver<UserGiftStickersDataModel>(mainThreadDisposable) {
                    @Override
                    public void onSuccess(UserGiftStickersDataModel userGiftStickersDataModel) {
                        // TODO: 1/20/20 post
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);

                    }
                });
    }

    public void onRetryButtonClick() {
        showRetryView.set(View.GONE);
        showLoading.set(View.VISIBLE);
    }

    public ObservableInt getShowLoading() {
        return showLoading;
    }

    public ObservableInt getShowRetryView() {
        return showRetryView;
    }

    public ObservableInt getShowEmptyListMessage() {
        return showEmptyListMessage;
    }

    public MutableLiveData<List<String>> getLoadStickerList() {
        return loadStickerList;
    }

    public SingleLiveEvent<String> getShowRequestErrorMessage() {
        return showRequestErrorMessage;
    }
}
