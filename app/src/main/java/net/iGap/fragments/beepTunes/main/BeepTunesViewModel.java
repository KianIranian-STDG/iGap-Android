package net.iGap.fragments.beepTunes.main;

import net.iGap.libs.bannerslider.BannerSlider;
import net.iGap.viewmodel.BaseViewModel;

public class BeepTunesViewModel extends BaseViewModel {

    @Override
    public void onCreateViewModel() {
        BannerSlider.init(new SliderBannerImageLoadingService());
    }
}
