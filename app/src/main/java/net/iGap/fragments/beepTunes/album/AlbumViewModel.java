package net.iGap.fragments.beepTunes.album;

import android.arch.lifecycle.MutableLiveData;
import android.os.Environment;

import net.iGap.api.BeepTunesApi;
import net.iGap.api.apiService.ApiServiceProvider;
import net.iGap.helper.HelperDownloadFile;
import net.iGap.module.api.beepTunes.AlbumTrack;
import net.iGap.module.api.beepTunes.Albums;
import net.iGap.module.api.beepTunes.Track;
import net.iGap.viewmodel.BaseViewModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AlbumViewModel extends BaseViewModel {

    public static final int NOT_PURCHASED = 0;
    public static final int PURCHASED = 1;
    public static final int FREE = 1;
    public static final int PURCHESED_NOT_DOWNLOAD = 2;
    public static final int PURCHESED_AND_DOWNLOAD = 3;
    public static final int PURCHESED_UNSUCCESSFUL = 4;
    private static final String PATH = Environment.getExternalStorageDirectory().toString();

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

    public void onActionButtonClick(List<Track> tracks) {

    }


    private void downLoadTrack(String url, String name, String path) {
        int DownloadId = HelperDownloadFile.startDownloadManager(path, url, name);
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
}
