package net.iGap.fragments.beepTunes.main;

import android.arch.lifecycle.MutableLiveData;

import net.iGap.libs.bannerslider.BannerSlider;
import net.iGap.module.api.beepTunes.PlayerSong;
import net.iGap.realm.RealmDownloadSong;
import net.iGap.viewmodel.BaseViewModel;

public class BeepTunesViewModel extends BaseViewModel {
    private MutableLiveData<PlayerSong> playerStatusLiveData = new MutableLiveData<>();

    @Override
    public void onCreateViewModel() {
        BannerSlider.init(new SliderBannerImageLoadingService());
    }

    public void onPlaySong(RealmDownloadSong song, int status) {

    }
}
