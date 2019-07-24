package net.iGap.fragments.beepTunes.main;

import android.arch.lifecycle.MutableLiveData;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;

import net.iGap.libs.bannerslider.BannerSlider;
import net.iGap.module.api.beepTunes.PlayingSong;
import net.iGap.viewmodel.BaseViewModel;

import java.io.File;

import static net.iGap.G.context;

public class BeepTunesViewModel extends BaseViewModel {
    private MediaPlayer mediaPlayer;
    private MutableLiveData<PlayingSong> playerStatusLiveData = new MutableLiveData<>();

    @Override
    public void onCreateViewModel() {
        mediaPlayer = new MediaPlayer();
        BannerSlider.init(new SliderBannerImageLoadingService());
    }

    public void onPlaySongClicked(PlayingSong playingSong) {

    }

    private void play(PlayingSong playingSong) {
        mediaPlayer.start();
        playingSong.setPlay(true);
        playerStatusLiveData.postValue(playingSong);
    }

    private void pause(PlayingSong playingSong) {
        mediaPlayer.pause();
        playingSong.setPlay(false);
        playerStatusLiveData.postValue(playingSong);
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
}
