package net.iGap.fragments.beepTunes.album;

import android.arch.lifecycle.MutableLiveData;
import android.util.Log;

import com.downloader.Error;
import com.downloader.Progress;

import net.iGap.api.BeepTunesApi;
import net.iGap.api.apiService.ApiServiceProvider;
import net.iGap.helper.HelperDownloadFile;
import net.iGap.interfaces.OnSongDownload;
import net.iGap.module.api.beepTunes.AlbumTrack;
import net.iGap.module.api.beepTunes.Albums;
import net.iGap.module.api.beepTunes.DownloadSong;
import net.iGap.module.api.beepTunes.Track;
import net.iGap.viewmodel.BaseViewModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AlbumViewModel extends BaseViewModel implements OnSongDownload {

    public static final int NOT_PURCHASED = 0;
    public static final int PURCHASED = 1;
    public static final int FREE = 1;
    public static final int PURCHESED_NOT_DOWNLOAD = 2;
    public static final int PURCHESED_AND_DOWNLOAD = 3;
    public static final int PURCHESED_UNSUCCESSFUL = 4;

    private static final String TAG = "aabolfazlAlbum";
    private MutableLiveData<List<Track>> trackMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<Albums> albumMutableLiveData = new MutableLiveData<>();
    private BeepTunesApi apiService = ApiServiceProvider.getBeepTunesClient();
    private MutableLiveData<Boolean> progressMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<Integer> albumStatus = new MutableLiveData<>();

    public void getAlbumSong(long id) {
        progressMutableLiveData.postValue(true);
        apiService.getAlbumTrack(id).enqueue(new Callback<AlbumTrack>() {
            @Override
            public void onResponse(Call<AlbumTrack> call, Response<AlbumTrack> response) {
                progressMutableLiveData.postValue(false);
                if (response.isSuccessful()) {
                    trackMutableLiveData.postValue(response.body().getData());
                }
            }

            @Override
            public void onFailure(Call<AlbumTrack> call, Throwable t) {
                progressMutableLiveData.postValue(false);
            }
        });
    }

    public void getArtistOtherAlbum(long id) {
        progressMutableLiveData.postValue(true);
        apiService.getArtistAlbums(id).enqueue(new Callback<Albums>() {
            @Override
            public void onResponse(Call<Albums> call, Response<Albums> response) {
                progressMutableLiveData.postValue(false);
                if (response.isSuccessful()) {
                    albumMutableLiveData.postValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<Albums> call, Throwable t) {
                progressMutableLiveData.postValue(false);
            }
        });
    }

    public void onActionButtonClick(Track track, String path) {
        DownloadSong downloadSong = new DownloadSong(track.getDownloadLinks().getH360(), track.getId(), track.getName());
        downLoadTrack(downloadSong, path);
    }


    private void downLoadTrack(DownloadSong song, String path) {
        HelperDownloadFile.startDownloadManager(path, song, this);
    }

    public MutableLiveData<List<Track>> getTrackMutableLiveData() {
        return trackMutableLiveData;
    }

    public MutableLiveData<Albums> getAlbumMutableLiveData() {
        return albumMutableLiveData;
    }

    public MutableLiveData<Boolean> getProgressMutableLiveData() {
        return progressMutableLiveData;
    }

    public MutableLiveData<Integer> getAlbumStatus() {
        return albumStatus;
    }

    @Override
    public void progressDownload(DownloadSong downloadSong, Progress progress) {
        Log.i(TAG, "progressDownload: " + downloadSong.getId() + " " + progress.currentBytes + " " + progress.totalBytes);
    }

    @Override
    public void completeDownload(DownloadSong downloadSong) {
        Log.i(TAG, "completeDownload: " + downloadSong.getName());
    }

    @Override
    public void downloadError(DownloadSong downloadSong, Error error) {
        Log.i(TAG, "downloadError: " + downloadSong.getId() + error.getConnectionException().getMessage());
    }

    @Override
    public void pauseDownload(DownloadSong downloadSong) {
        Log.i(TAG, "pauseDownload: " + downloadSong.getId());
    }

    @Override
    public void startOrResume(DownloadSong downloadSong) {
        Log.i(TAG, "startOrResume: " + downloadSong.getId());
    }

    @Override
    public void cancelDownload(DownloadSong downloadSong) {
        Log.i(TAG, "cancelDownload: " + downloadSong.getId());
    }
}
