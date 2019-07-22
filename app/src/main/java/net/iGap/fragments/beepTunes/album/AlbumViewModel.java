package net.iGap.fragments.beepTunes.album;

import android.arch.lifecycle.MutableLiveData;
import android.content.SharedPreferences;
import android.support.v4.app.FragmentManager;

import com.downloader.Error;
import com.downloader.PRDownloader;
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
import net.iGap.realm.RealmDownloadSong;
import net.iGap.viewmodel.BaseViewModel;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AlbumViewModel extends BaseViewModel implements OnSongDownload {

    private static final String TAG = "aabolfazlAlbum";
    private MutableLiveData<List<Track>> trackMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<Albums> albumMutableLiveData = new MutableLiveData<>();
    private BeepTunesApi apiService = ApiServiceProvider.getBeepTunesClient();
    private MutableLiveData<Boolean> LoadingProgressMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<DownloadSong> downloadStatusMutableLiveData = new MutableLiveData<>();
    private DownloadSong downloadingSong;
    private List<DownloadSong> downloadQueue = new ArrayList<>();

    private Realm realm;

    @Override
    public void onCreateViewModel() {
        super.onCreateViewModel();
        realm = Realm.getDefaultInstance();
    }

    void getAlbumSong(long id) {
        LoadingProgressMutableLiveData.postValue(true);
        apiService.getAlbumTrack(id).enqueue(new Callback<AlbumTrack>() {
            @Override
            public void onResponse(Call<AlbumTrack> call, Response<AlbumTrack> response) {
                LoadingProgressMutableLiveData.postValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    trackMutableLiveData.postValue(response.body().getData());
                }
            }

            @Override
            public void onFailure(Call<AlbumTrack> call, Throwable t) {
                LoadingProgressMutableLiveData.postValue(false);
            }
        });
    }

    void getArtistOtherAlbum(long id) {
        LoadingProgressMutableLiveData.postValue(true);
        apiService.getArtistAlbums(id).enqueue(new Callback<Albums>() {
            @Override
            public void onResponse(Call<Albums> call, Response<Albums> response) {
                LoadingProgressMutableLiveData.postValue(false);
                if (response.isSuccessful()) {
                    albumMutableLiveData.postValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<Albums> call, Throwable t) {
                LoadingProgressMutableLiveData.postValue(false);
            }
        });
    }

    void onDownloadClick(Track track, String path, FragmentManager fragmentManager, SharedPreferences sharedPreferences) {
        File file = new File(path + "/" + track.getName());
        if (!file.exists()) {
            if (downloadingSong != null) {
                if (!track.getId().equals(downloadingSong.getId()))
                    startDownload(track, path, fragmentManager, sharedPreferences);
                else {
                    pauseDownloadSong(downloadingSong);
                }
            } else {
                startDownload(track, path, fragmentManager, sharedPreferences);
            }
        } else {

        }
    }

    private void startDownload(Track track, String path, FragmentManager fragmentManager, SharedPreferences sharedPreferences) {
        if (!sharedPreferences.getBoolean(SHP_SETTING.KEY_BBEP_TUNES_DOWNLOAD, false)) {
            DownloadQualityFragment fragment = new DownloadQualityFragment();
            fragment.setDownloadDialog(quality -> {
                if (quality == 128) {
                    DownloadSong downloadSong = new DownloadSong(track.getDownloadLinks().getL128(), track.getId(), track.getName(), path);
                    addToDownloadQueue(downloadSong);
                } else {
                    DownloadSong downloadSong = new DownloadSong(track.getDownloadLinks().getH320(), track.getId(), track.getName(), path);
                    addToDownloadQueue(downloadSong);
                }
            });
            fragment.show(fragmentManager, null);
        } else {
            if (sharedPreferences.getInt(SHP_SETTING.KEY_BBEP_TUNES_DOWNLOAD_QUALITY, 128) == 128) {
                DownloadSong downloadSong = new DownloadSong(track.getDownloadLinks().getL128(), track.getId(), track.getName(), path);
                addToDownloadQueue(downloadSong);
            } else {
                DownloadSong downloadSong = new DownloadSong(track.getDownloadLinks().getH320(), track.getId(), track.getName(), path);
                addToDownloadQueue(downloadSong);
            }
        }
    }

    private void pauseDownloadSong(DownloadSong downloadSong) {
        PRDownloader.pause(downloadSong.getDownloadId());
        removeFromQueue(downloadSong);
    }

    private void addToDownloadQueue(DownloadSong downloadSong) {
        if (downloadQueue.size() == 0) {
            downloadQueue.add(downloadSong);
            downLoadTrack(downloadSong);
        } else {
            boolean isQueue = false;
            for (int i = 0; i < downloadQueue.size(); i++) {
                if (downloadQueue.get(i).getId().equals(downloadSong.getId()))
                    return;
                else
                    isQueue = true;
            }
            if (isQueue) {
                downloadQueue.add(downloadSong);
            }
        }
    }

    private void removeFromQueue(DownloadSong downloadSong) {
        downloadQueue.remove(downloadSong);
        if (downloadQueue.size() > 0)
            downLoadTrack(downloadQueue.get(0));
    }

    private void downLoadTrack(DownloadSong downloadSong) {
        HelperDownloadFile.startDownloadManager(downloadSong, this);
    }


    @Override
    public void progressDownload(DownloadSong downloadSong, Progress progress) {
        downloadSong.setDownloadProgress((int) ((progress.currentBytes * 100) / progress.totalBytes));
        if (downloadSong.getDownloadStatus() == DownloadSong.STATUS_START)
            downloadSong.setDownloadStatus(DownloadSong.STATUS_DOWNLOADING);
        downloadStatusMutableLiveData.postValue(downloadSong);
    }

    @Override
    public void completeDownload(DownloadSong downloadSong) {
        downloadSong.setDownloadStatus(DownloadSong.STATUS_COMPLETE);

        RealmDownloadSong song = new RealmDownloadSong();
        song.setId(downloadSong.getId());
        song.setPath(downloadSong.getPath());
        realm.executeTransactionAsync(realm -> {
            realm.copyToRealmOrUpdate(song);
        });

        removeFromQueue(downloadSong);
        downloadStatusMutableLiveData.postValue(downloadSong);
    }

    @Override
    public void downloadError(DownloadSong downloadSong, Error error) {
        downloadSong.setDownloadStatus(DownloadSong.STATUS_ERROR);
        downloadStatusMutableLiveData.postValue(downloadSong);
    }

    @Override
    public void pauseDownload(DownloadSong downloadSong) {
        downloadSong.setDownloadStatus(DownloadSong.STATUS_PAUSE);
        downloadStatusMutableLiveData.postValue(downloadSong);
    }

    @Override
    public void startOrResume(DownloadSong downloadSong) {
        downloadingSong = downloadSong;
        downloadSong.setDownloadStatus(DownloadSong.STATUS_START);
        downloadStatusMutableLiveData.postValue(downloadSong);
    }

    @Override
    public void cancelDownload(DownloadSong downloadSong) {
        downloadSong.setDownloadStatus(DownloadSong.STATUS_CANCEL);
        downloadStatusMutableLiveData.postValue(downloadSong);
    }

    MutableLiveData<List<Track>> getTrackMutableLiveData() {
        return trackMutableLiveData;
    }

    MutableLiveData<Albums> getAlbumMutableLiveData() {
        return albumMutableLiveData;
    }

    MutableLiveData<Boolean> getLoadingProgressMutableLiveData() {
        return LoadingProgressMutableLiveData;
    }

    MutableLiveData<DownloadSong> getDownloadStatusMutableLiveData() {
        return downloadStatusMutableLiveData;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        realm.close();
    }
}
