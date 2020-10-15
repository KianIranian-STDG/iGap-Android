package net.iGap.module;

import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;

import net.iGap.module.api.beepTunes.PlayingSong;
import net.iGap.module.api.beepTunes.ProgressDuration;

import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import static net.iGap.G.context;

public class BeepTunesPlayerService extends Service {
    public static final String SONG_PATH = "songUri";
    public static final String SONG_ID = "songId";
    public static final String SONG_ARTIST_ID = "songArtistId";
    public static final String SONG_ALBUM_ID = "songAlbumId";
    public static final String ACTION_PLAY = "play";
    public static final String ACTION_PLAY_NEW = "playNew";
    public static final String ACTION_PAUSE = "pause";
    private static final String TAG = "aabolfazlService";
    public static long playingSongId;
    private static boolean serviceRunning = false;
    private BeepTunesBinder binder = new BeepTunesBinder();
    private MediaPlayer mediaPlayer;
    private PlayingSong playingSong;
    private MutableLiveData<PlayingSong> playingSongMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<ProgressDuration> progressDurationLiveData = new MutableLiveData<>();

    public static boolean isServiceRunning() {
        return serviceRunning;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onCreate() {
        serviceRunning = true;
        mediaPlayer = new MediaPlayer();
        playingSong = new PlayingSong();
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            playingSong.setSongPath(intent.getStringExtra(SONG_PATH));
            playingSong.setSongId(intent.getLongExtra(SONG_ID, 0));
            playingSong.setArtistId(intent.getLongExtra(SONG_ARTIST_ID, 0));
            playingSong.setAlbumId(intent.getLongExtra(SONG_ALBUM_ID, 0));

            if (playingSong != null) {
                if (playingSongId == 0)
                    setDataSource(playingSong);
                else if (playingSong.getSongId() != playingSongId)
                    setNewDataSource(playingSong);
                if (mediaPlayer.isPlaying() && playingSongId == playingSong.getSongId())
                    pause(playingSong);
                else
                    play(playingSong);

            }
        }

        return START_NOT_STICKY;
    }

    private void setNewDataSource(PlayingSong playingSong) {
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(playingSong.getSongPath());
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setDataSource(PlayingSong playingSong) {
        try {
            mediaPlayer.setDataSource(playingSong.getSongPath());
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        serviceRunning = false;
        mediaPlayer.release();
        super.onDestroy();
    }

    public MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }

    private void play(PlayingSong playingSong) {
        mediaPlayer.start();
        getSongInfo(playingSong);
        progress(playingSong);
        playingSong.setStatus(PlayingSong.PLAY);
        playingSongId = playingSong.getSongId();
        playingSongMutableLiveData.postValue(playingSong);
    }

    private void pause(PlayingSong playingSong) {
        mediaPlayer.pause();
        progress(playingSong);
        getSongInfo(playingSong);
        playingSong.setStatus(PlayingSong.PAUSE);
        playingSongMutableLiveData.postValue(playingSong);
    }

    private void getSongInfo(PlayingSong playingSong) {
        MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
        Uri uri = Uri.fromFile(new File(playingSong.getSongPath()));
        if (uri != null) {
            mediaMetadataRetriever.setDataSource(context, uri);
            playingSong.setTitle(mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE));
            playingSong.setAlbumName(mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM));
            playingSong.setArtistName(mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST));
            byte[] data = mediaMetadataRetriever.getEmbeddedPicture();
            if (data != null)
                playingSong.setImageData(BitmapFactory.decodeByteArray(data, 0, data.length));
        }
    }

    public MutableLiveData<PlayingSong> getPlayingSongMutableLiveData() {
        return playingSongMutableLiveData;
    }

    private void progress(PlayingSong playingSong) {
        Timer timer = new Timer();
        ProgressDuration progressDuration = new ProgressDuration();

        progressDuration.setId(playingSong.getSongId());
        progressDuration.setTotal(mediaPlayer.getDuration() / 1000);

        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (mediaPlayer.isPlaying()) {
                    progressDuration.setCurrent(mediaPlayer.getCurrentPosition() / 1000);
                    progressDurationLiveData.postValue(progressDuration);
                }
            }
        }, 0, 1000);
    }

    public MutableLiveData<ProgressDuration> getProgressDurationLiveData() {
        return progressDurationLiveData;
    }

    public interface ServiceUpdate {
        void playingSong(PlayingSong playingSong);
    }

    public class BeepTunesBinder extends Binder {
        public BeepTunesPlayerService getService() {
            return BeepTunesPlayerService.this;
        }
    }
}
