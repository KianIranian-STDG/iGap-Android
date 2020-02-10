package net.iGap.fragments.giftStickers;

import android.util.Log;
import android.view.View;

import androidx.lifecycle.MutableLiveData;

import net.iGap.fragments.emoji.apiModels.SliderDataModel;
import net.iGap.repository.sticker.StickerRepository;
import net.iGap.rx.IGSingleObserver;
import net.iGap.rx.ObserverViewModel;

public class GiftStickerHomeViewModel extends ObserverViewModel {
    private StickerRepository stickerRepository = StickerRepository.getInstance();

    private MutableLiveData<SliderDataModel> sliderMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<Integer> sliderVisibilityLiveData = new MutableLiveData<>();

    @Override
    public void subscribe() {

    }

    @Override
    public void onFragmentViewCreated() {
        super.onFragmentViewCreated();

        stickerRepository.getGiftStickerHomePageImageUrl()
                .subscribe(new IGSingleObserver<SliderDataModel>(mainThreadDisposable) {
                    @Override
                    public void onSuccess(SliderDataModel dataModel) {
                        Log.i("abbasiPro", "onSuccess getGiftStickerHomePageImageUrl START");
                        sliderMutableLiveData.postValue(dataModel);
                        Log.i("abbasiPro", "onSuccess getGiftStickerHomePageImageUrl END");
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        Log.i("abbasiPro", "onError getGiftStickerHomePageImageUrl START");
                        sliderVisibilityLiveData.postValue(View.GONE);
                        Log.i("abbasiPro", "onError getGiftStickerHomePageImageUrl END");
                    }
                });

    }

    public MutableLiveData<SliderDataModel> getSliderMutableLiveData() {
        return sliderMutableLiveData;
    }

    public MutableLiveData<Integer> getSliderVisibilityLiveData() {
        return sliderVisibilityLiveData;
    }
}
