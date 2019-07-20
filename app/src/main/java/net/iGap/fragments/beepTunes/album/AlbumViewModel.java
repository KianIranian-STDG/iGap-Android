package net.iGap.fragments.beepTunes.album;

import android.arch.lifecycle.MutableLiveData;
import android.content.SharedPreferences;
import android.support.v4.app.FragmentManager;
import android.util.Log;

import com.downloader.Error;
import com.downloader.Progress;

import net.iGap.api.BeepTunesApi;
import net.iGap.api.apiService.ApiServiceProvider;
import net.iGap.fragments.beepTunes.downloadQuality.DownloadQualityFragment;
import net.iGap.helper.HelperDownloadFile;
import net.iGap.interfaces.OnSongDownload;
import net.iGap.module.SHP_SETTING;
import net.iGap.module.api.beepTunes.AlbumTrack;
import net.iGap.module.api.beepTunes.Albums;
import net.iGap.module.api.beepTunes.DownloadSong;
import net.iGap.module.api.beepTunes.Track;
import net.iGap.viewmodel.BaseViewModel;

import java.io.File;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AlbumViewModel extends BaseViewModel implements OnSongDownload {

    private static final String TAG = "aabolfazlAlbum";
    private MutableLiveData<List<Track>> trackMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<Albums> albumMutableLiveData = new MutableLiveData<>();
    private BeepTunesApi apiService = ApiServiceProvider.getBeepTunesClient();
    private MutableLiveData<Boolean> progressMutableLiveData = new MutableLiveData<>();

    @Override
    public void onStart() {

    }

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

    public void startDownload(Track track, String path, FragmentManager fragmentManager, SharedPreferences sharedPreferences) {
        File file = new File(path + "/" + track.getName());
        if (file.exists()) {
            // TODO: 7/20/19 file exists
            Log.i(TAG, "file exist " + track.getName());
        } else {
            if (!sharedPreferences.getBoolean(SHP_SETTING.KEY_BBEP_TUNES_DOWNLOAD, false)) {
                DownloadQualityFragment fragment = new DownloadQualityFragment();
                fragment.setDownloadDialog(quality -> {
                    if (quality == 128) {
                        DownloadSong downloadSong = new DownloadSong(track.getDownloadLinks().getL128(), track.getId(), track.getName());
                        downLoadTrack(downloadSong, path);
                    } else {
                        DownloadSong downloadSong = new DownloadSong(track.getDownloadLinks().getH360(), track.getId(), track.getName());
                        downLoadTrack(downloadSong, path);
                    }
                });
                fragment.show(fragmentManager, null);
            } else {
                if (sharedPreferences.getInt(SHP_SETTING.KEY_BBEP_TUNES_DOWNLOAD_QUALITY, 128) == 128) {
                    DownloadSong downloadSong = new DownloadSong(track.getDownloadLinks().getL128(), track.getId(), track.getName());
                    downLoadTrack(downloadSong, path);
                } else {
                    DownloadSong downloadSong = new DownloadSong(track.getDownloadLinks().getH360(), track.getId(), track.getName());
                    downLoadTrack(downloadSong, path);
                }
            }
        }
    }

    private void downLoadTrack(DownloadSong song, String path) {
        HelperDownloadFile.startDownloadManager(path, song, this);
    }

    MutableLiveData<List<Track>> getTrackMutableLiveData() {
        return trackMutableLiveData;
    }

    MutableLiveData<Albums> getAlbumMutableLiveData() {
        return albumMutableLiveData;
    }

    MutableLiveData<Boolean> getProgressMutableLiveData() {
        return progressMutableLiveData;
    }

    @Override
    public void progressDownload(DownloadSong downloadSong, Progress progress) {
//        Log.i(TAG, "progressDownload: " + downloadSong.getId() + " " + progress.currentBytes + " " + progress.totalBytes);
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
