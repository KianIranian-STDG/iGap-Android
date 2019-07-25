package net.iGap.fragments.beepTunes.main;

import android.arch.lifecycle.MutableLiveData;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;

import net.iGap.libs.bannerslider.BannerSlider;
import net.iGap.module.api.beepTunes.PlayingSong;
import net.iGap.module.api.beepTunes.ProgressDuration;
import net.iGap.viewmodel.BaseViewModel;

import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import static net.iGap.G.context;

public class BeepTunesViewModel extends BaseViewModel implements MediaPlayer.OnPreparedListener {
    private static final String TAG = "aabolfazlMainViewModel";
    private MediaPlayer mediaPlayer;
    private PlayingSong nowPlaySong;
    private MutableLiveData<PlayingSong> playerStatusLiveData = new MutableLiveData<>();
    private MutableLiveData<Boolean> behaviorStatusLiveData = new MutableLiveData<>();
    private MutableLiveData<ProgressDuration> songProgressLiveData = new MutableLiveData<>();
    private ProgressDuration progressDuration = new ProgressDuration();

    @Override
    public void onCreateViewModel() {
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnPreparedListener(this);
        BannerSlider.init(new SliderBannerImageLoadingService());
    }

    public void onPlaySongClicked(PlayingSong playingSong) {
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
    }

    private void pause(PlayingSong playingSong) {
        mediaPlayer.pause();
        playingSong.setPlay(false);
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

    public MutableLiveData<PlayingSong> getPlayerStatusLiveData() {
        return playerStatusLiveData;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {

    }

    public MutableLiveData<Boolean> getBehaviorStatusLiveData() {
        return behaviorStatusLiveData;
    }

    public MutableLiveData<ProgressDuration> getSongProgressLiveData() {
        return songProgressLiveData;
    }
}
