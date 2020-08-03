package net.iGap.fragments.beepTunes.album;

import android.content.SharedPreferences;

import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.MutableLiveData;

import com.downloader.Error;
import com.downloader.PRDownloader;
import com.downloader.Progress;

import net.iGap.api.BeepTunesApi;
import net.iGap.api.apiService.RetrofitFactory;
import net.iGap.fragments.beepTunes.downloadQuality.DownloadQualityFragment;
import net.iGap.helper.HelperDownloadFile;
import net.iGap.module.SHP_SETTING;
import net.iGap.module.accountManager.DbManager;
import net.iGap.module.api.beepTunes.Albums;
import net.iGap.module.api.beepTunes.DownloadSong;
import net.iGap.module.api.beepTunes.Track;
import net.iGap.observers.interfaces.OnSongDownload;
import net.iGap.realm.RealmDownloadSong;
import net.iGap.viewmodel.BaseViewModel;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class AlbumViewModel extends BaseViewModel implements OnSongDownload {
    private static final String TAG = "aabolfazlAlbum";

    private BeepTunesApi apiService = new RetrofitFactory().getBeepTunesRetrofit();
    private List<DownloadSong> downloadQueue = new ArrayList<>();

    private MutableLiveData<List<Track>> trackMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<Albums> albumMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<Boolean> LoadingProgressMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<DownloadSong> downloadStatusMutableLiveData = new MutableLiveData<>();

    @Override
    public void onCreateViewModel() {
        super.onCreateViewModel();
    }

    void getAlbumSong(long id) {
        /*new ApiInitializer<AlbumTrack>().initAPI(apiService.getAlbumTrack(id), this, new ResponseCallback<AlbumTrack>() {
            @Override
            public void onSuccess(AlbumTrack data) {
                trackMutableLiveData.postValue(data.getData());
            }

            @Override
            public void onError(ErrorModel error) {

            }

            @Override
            public void setProgressIndicator(boolean visibility) {
                LoadingProgressMutableLiveData.postValue(visibility);
            }
        });*/
    }

    void getArtistOtherAlbum(long id) {

        /*new ApiInitializer<Albums>().initAPI(apiService.getArtistAlbums(id), this, new ResponseCallback<Albums>() {
            @Override
            public void onSuccess(Albums data) {
                albumMutableLiveData.postValue(data);
            }

            @Override
            public void onError(ErrorModel error) {

            }

            @Override
            public void setProgressIndicator(boolean visibility) {
                LoadingProgressMutableLiveData.postValue(visibility);
            }
        });*/

    }

    void onDownloadClick(Track track, String path, FragmentManager fragmentManager, SharedPreferences sharedPreferences) {
        File file = new File(path + "/" + track.getSavedName());
        if (!file.exists()) {
            startDownload(track, path, fragmentManager, sharedPreferences);
        }
    }

    private void startDownload(Track track, String path, FragmentManager fragmentManager, SharedPreferences sharedPreferences) {
        if (!sharedPreferences.getBoolean(SHP_SETTING.KEY_BBEP_TUNES_DOWNLOAD, false)) {
            DownloadQualityFragment fragment = new DownloadQualityFragment();
            fragment.setDownloadDialog(quality -> {
                if (quality == 128) {
                    DownloadSong downloadSong = new DownloadSong(track.getDownloadLinks().getL128(), track, track.getSavedName(), path);
                    addToDownloadQueue(downloadSong);
                } else {
                    DownloadSong downloadSong = new DownloadSong(track.getDownloadLinks().getH320(), track, track.getSavedName(), path);
                    addToDownloadQueue(downloadSong);
                }
            });
            fragment.show(fragmentManager, null);
        } else {
            if (sharedPreferences.getInt(SHP_SETTING.KEY_BBEP_TUNES_DOWNLOAD_QUALITY, 128) == 128) {
                DownloadSong downloadSong = new DownloadSong(track.getDownloadLinks().getL128(), track, track.getSavedName(), path);
                addToDownloadQueue(downloadSong);
            } else {
                DownloadSong downloadSong = new DownloadSong(track.getDownloadLinks().getH320(), track, track.getSavedName(), path);
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
        song.setPath(downloadSong.getPath() + "/" + downloadSong.getSavedName());
        song.setDisplayName(downloadSong.getTrack().getName());
        song.setArtistId(downloadSong.getArtistId());
        song.setAlbumId(downloadSong.getAlbumId());
        DbManager.getInstance().doRealmTask(realm -> {
            realm.executeTransactionAsync(realm2 -> realm2.copyToRealmOrUpdate(song));
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
}
