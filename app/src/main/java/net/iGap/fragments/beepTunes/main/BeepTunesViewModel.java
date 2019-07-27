package net.iGap.fragments.beepTunes.main;

import android.arch.lifecycle.MutableLiveData;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.IBinder;
import android.util.Log;

import net.iGap.fragments.BaseFragment;
import net.iGap.libs.bannerslider.BannerSlider;
import net.iGap.module.BeepTunesPlayerService;
import net.iGap.module.api.beepTunes.PlayingSong;
import net.iGap.module.api.beepTunes.ProgressDuration;
import net.iGap.viewmodel.BaseViewModel;

import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import static net.iGap.G.context;

public class BeepTunesViewModel extends BaseViewModel implements BeepTunesPlayerService.ServiceUpdate {
    private static final String TAG = "aabolfazlMainViewModel";
    private MediaPlayer mediaPlayer;
    private PlayingSong nowPlaySong;
    private MutableLiveData<PlayingSong> playerStatusLiveData = new MutableLiveData<>();
    private MutableLiveData<Boolean> behaviorStatusLiveData = new MutableLiveData<>();
    private MutableLiveData<ProgressDuration> songProgressLiveData = new MutableLiveData<>();
    private ProgressDuration progressDuration = new ProgressDuration();
    private ServiceConnection serviceConnection;

    @Override
    public void onCreateViewModel() {
        mediaPlayer = new MediaPlayer();
        BannerSlider.init(new SliderBannerImageLoadingService());

        serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                if (service instanceof BeepTunesPlayerService.BeepTunesBinder) {
                    BeepTunesPlayerService beepTunesPlayerService = ((BeepTunesPlayerService.BeepTunesBinder) service).getService();
                    beepTunesPlayerService.setServiceUpdate(BeepTunesViewModel.this);
                }
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        };
    }

    public void onPlaySongClicked(PlayingSong playingSong, Context context) {
        if (!BeepTunesPlayerService.isServiceRunning()) {
            context.startService(new Intent(context, BeepTunesPlayerService.class));
            context.bindService(new Intent(context, BeepTunesPlayerService.class), serviceConnection, Context.BIND_AUTO_CREATE);
        }
        if (nowPlaySong == null) {
            try {
                mediaPlayer.setDataSource(playingSong.getSongPath());
                mediaPlayer.prepare();
                play(playingSong);
            } catch (IOException e) {
                Log.e(TAG, "Player exception: " + e.getLocalizedMessage());
                e.printStackTrace();
            }
        } else {
            if (nowPlaySong.getSongId() == playingSong.getSongId())
                if (playingSong.isPlay()) {
                    pause(playingSong);
                } else
                    play(playingSong);
            else {
                Log.i(TAG, "play another song  " + playingSong.getSongId());
                // TODO: 7/25/19 change mediaPlayer data resource with new play song
            }
        }
    }

    private void play(PlayingSong playingSong) {
        mediaPlayer.start();
        playingSong.setPlay(true);
        getSongInfo(playingSong);
        nowPlaySong = playingSong;
        progress(playingSong);
        playerStatusLiveData.postValue(playingSong);
        behaviorStatusLiveData.postValue(true);
        Log.i(TAG, "play: " + playingSong.getSongId());
    }

    private void pause(PlayingSong playingSong) {
        mediaPlayer.pause();
        playingSong.setPlay(false);
        nowPlaySong = playingSong;
        playerStatusLiveData.postValue(playingSong);
    }

    private void progress(PlayingSong playingSong) {
        Timer timer = new Timer();

        progressDuration.setId(playingSong.getSongId());
        progressDuration.setTotal(mediaPlayer.getDuration() / 1000);

        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                progressDuration.setCurrent(mediaPlayer.getCurrentPosition() / 1000);
                songProgressLiveData.postValue(progressDuration);
            }
        }, 0, 1000);
    }

    private void getSongInfo(PlayingSong song) {
        MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
        Uri uri = Uri.fromFile(new File(song.getSongPath()));
        if (uri != null) {
            mediaMetadataRetriever.setDataSource(context, uri);
            song.setTitle(mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE));
            song.setAlbumName(mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM));
            song.setArtistName(mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST));
            byte[] data = mediaMetadataRetriever.getEmbeddedPicture();
            if (data != null)
                song.setImageData(BitmapFactory.decodeByteArray(data, 0, data.length));
        }
    }

    @Override
    public void onStartFragment(BaseFragment fragment) {
        if (BeepTunesPlayerService.isServiceRunning()){
            fragment.getContext().bindService(new Intent(fragment.getContext(), BeepTunesPlayerService.class), serviceConnection, Context.BIND_AUTO_CREATE);
        }
    }

    public MutableLiveData<PlayingSong> getPlayerStatusLiveData() {
        return playerStatusLiveData;
    }

    public MutableLiveData<Boolean> getBehaviorStatusLiveData() {
        return behaviorStatusLiveData;
    }

    public MutableLiveData<ProgressDuration> getSongProgressLiveData() {
        return songProgressLiveData;
    }

    @Override
    public void playingSong(PlayingSong playingSong) {
        Log.i(TAG, "playingSong: " + playingSong.getStatus());
    }
}
