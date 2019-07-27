package net.iGap.fragments.beepTunes.main;

import android.arch.lifecycle.MutableLiveData;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.MediaPlayer;
import android.os.IBinder;

import net.iGap.fragments.BaseFragment;
import net.iGap.libs.bannerslider.BannerSlider;
import net.iGap.module.BeepTunesPlayerService;
import net.iGap.module.api.beepTunes.PlayingSong;
import net.iGap.module.api.beepTunes.ProgressDuration;
import net.iGap.viewmodel.BaseViewModel;

public class BeepTunesViewModel extends BaseViewModel {
    private static final String TAG = "aabolfazlMainViewModel";
    private MediaPlayer mediaPlayer;
    private ServiceConnection serviceConnection;
    private MutableLiveData<PlayingSong> serviceConnectionLiveData;
    private MutableLiveData<ProgressDuration> progressDurationLiveData;
    private MutableLiveData<ProgressDuration> progressViewLiveData = new MutableLiveData<>();
    private MutableLiveData<Boolean> behaviorStatusLiveData = new MutableLiveData<>();

    @Override
    public void onCreateViewModel() {
        BannerSlider.init(new SliderBannerImageLoadingService());
    }

    @Override
    public void onCreateFragment(BaseFragment fragment) {
        serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                if (service instanceof BeepTunesPlayerService.BeepTunesBinder) {
                    BeepTunesPlayerService beepTunesPlayerService = ((BeepTunesPlayerService.BeepTunesBinder) service).getService();
                    mediaPlayer = beepTunesPlayerService.getMediaPlayer();
                    serviceConnectionLiveData = beepTunesPlayerService.getPlayingSongMutableLiveData();
                    progressDurationLiveData = beepTunesPlayerService.getProgressDurationLiveData();
                    serviceConnected(fragment);
                }
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        };
    }

    private void serviceConnected(BaseFragment fragment) {

        serviceConnectionLiveData.observe(fragment.getViewLifecycleOwner(), playingSong -> {
            if (playingSong != null) {

            }
        });

        progressDurationLiveData.observe(fragment.getViewLifecycleOwner(), progressDuration -> {
            if (progressDuration != null) {
                progressViewLiveData.postValue(progressDuration);
            }
        });

    }

    void onPlaySongClicked(PlayingSong playingSong, Context context) {
        if (!BeepTunesPlayerService.isServiceRunning()) {
            Intent intent = new Intent(context, BeepTunesPlayerService.class);
            intent.putExtra(BeepTunesPlayerService.SONG_PATH, playingSong.getSongPath());
            intent.putExtra(BeepTunesPlayerService.SONG_ID, playingSong.getSongId());
            context.startService(intent);
            context.bindService(new Intent(context, BeepTunesPlayerService.class), serviceConnection, Context.BIND_AUTO_CREATE);
            behaviorStatusLiveData.postValue(true);
        } else {

        }

    }


    @Override
    public void onStartFragment(BaseFragment fragment) {
        if (BeepTunesPlayerService.isServiceRunning()) {
            if (fragment.getContext() != null)
                fragment.getContext().bindService(new Intent(fragment.getContext(), BeepTunesPlayerService.class), serviceConnection, Context.BIND_AUTO_CREATE);
            behaviorStatusLiveData.postValue(true);
        }
    }

    @Override
    public void onDestroyFragment(BaseFragment fragment) {
        if (BeepTunesPlayerService.isServiceRunning()) {
            if (fragment.getContext() != null)
                fragment.getContext().unbindService(serviceConnection);
        }
    }

    public MutableLiveData<ProgressDuration> getProgressDurationLiveData() {
        return progressViewLiveData;
    }

    public MutableLiveData<Boolean> getBehaviorStatusLiveData() {
        return behaviorStatusLiveData;
    }
}
