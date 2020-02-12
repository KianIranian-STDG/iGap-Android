package net.iGap.fragments.giftStickers;

import android.view.View;

import androidx.lifecycle.MutableLiveData;

import net.iGap.fragments.emoji.apiModels.SliderDataModel;
import net.iGap.repository.StickerRepository;
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
                        sliderMutableLiveData.postValue(dataModel);
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        sliderVisibilityLiveData.postValue(View.GONE);
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
