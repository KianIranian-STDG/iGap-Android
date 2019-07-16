package net.iGap.fragments.beepTunes.album;

import android.arch.lifecycle.MutableLiveData;

import net.iGap.api.BeepTunesApi;
import net.iGap.api.apiService.ApiServiceProvider;
import net.iGap.module.api.beepTunes.AlbumTrack;
import net.iGap.module.api.beepTunes.Albums;
import net.iGap.module.api.beepTunes.Track;
import net.iGap.viewmodel.BaseViewModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AlbumViewModel extends BaseViewModel {

    private static final String TAG = "aabolfazlAlbum";
    private MutableLiveData<List<Track>> trackMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<Albums> albumMutableLiveData = new MutableLiveData<>();
    private BeepTunesApi apiService = ApiServiceProvider.getBeepTunesClient();
    private MutableLiveData<Boolean> songProgressMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<Boolean> albumProgressMutableLiveData = new MutableLiveData<>();

    @Override
    public void onCreateViewModel() {

    }

    @Override
    public void onStart() {

    }

    @Override
    public void onDestroy() {

    }

    @Override
    public void onPause() {

    }

    @Override
    public void onResume() {

    }

    public void getAlbumSong(long id) {
        songProgressMutableLiveData.postValue(true);
        apiService.getAlbumTrack(id).enqueue(new Callback<AlbumTrack>() {
            @Override
            public void onResponse(Call<AlbumTrack> call, Response<AlbumTrack> response) {
                songProgressMutableLiveData.postValue(false);
                if (response.isSuccessful()) {
                    trackMutableLiveData.postValue(response.body().getData());
                }
            }

            @Override
            public void onFailure(Call<AlbumTrack> call, Throwable t) {
                songProgressMutableLiveData.postValue(false);
            }
        });
    }

    public void getArtistOtherAlbum(long id) {
        albumProgressMutableLiveData.postValue(true);
        apiService.getArtistAlbums(id).enqueue(new Callback<Albums>() {
            @Override
            public void onResponse(Call<Albums> call, Response<Albums> response) {
                albumProgressMutableLiveData.postValue(false);
                if (response.isSuccessful()) {
                    albumMutableLiveData.postValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<Albums> call, Throwable t) {
                albumProgressMutableLiveData.postValue(false);
            }
        });
    }

    public MutableLiveData<List<Track>> getTrackMutableLiveData() {
        return trackMutableLiveData;
    }

    public MutableLiveData<Albums> getAlbumMutableLiveData() {
        return albumMutableLiveData;
    }

    public MutableLiveData<Boolean> getAlbumProgressMutableLiveData() {
        return albumProgressMutableLiveData;
    }

    public MutableLiveData<Boolean> getSongProgressMutableLiveData() {
        return songProgressMutableLiveData;
    }
}
