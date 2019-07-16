package net.iGap.fragments.beepTunes.album;

import android.arch.lifecycle.MutableLiveData;
import android.databinding.ObservableField;
import android.util.Log;

import net.iGap.G;
import net.iGap.api.apiService.ApiResponse;
import net.iGap.api.repository.BeepTunesRepository;
import net.iGap.module.api.beepTunes.Album;
import net.iGap.module.api.beepTunes.AlbumTrack;
import net.iGap.module.api.beepTunes.Track;
import net.iGap.viewmodel.BaseViewModel;

import java.util.List;

public class AlbumViewModel extends BaseViewModel {

    private static final String TAG = "aabolfazlAlbum";
    public MutableLiveData<String> albumImageUrl = new MutableLiveData<>();
    public MutableLiveData<String> albumName = new MutableLiveData<>();
    public ObservableField<String> artistName = new ObservableField<>();
    public ObservableField<String> albumCost = new ObservableField<>();
    public MutableLiveData<List<Track>> albumTrackMutableLiveData = new MutableLiveData<>();

    private boolean isRtl = G.isAppRtl;

    private BeepTunesRepository repository = new BeepTunesRepository();

    public void loadAlbum() {
        repository.getAlbumInfo(261434813, new ApiResponse<Album>() {
            @Override
            public void onResponse(Album album) {
                setAlbumDetail(album);
            }

            @Override
            public void onFailed(String error) {

            }

            @Override
            public void setProgressIndicator(boolean visibility) {

            }
        });

        repository.getAlbumTrack(261434813, new ApiResponse<AlbumTrack>() {
            @Override
            public void onResponse(AlbumTrack albumTrack) {
                albumTrackMutableLiveData.setValue(albumTrack.getData());
            }

            @Override
            public void onFailed(String error) {
                Log.i(TAG, "onFailed: " + error);
            }

            @Override
            public void setProgressIndicator(boolean visibility) {

            }
        });
    }

    private void setAlbumDetail(Album album) {
        albumImageUrl.setValue(album.getImage());

        if (isRtl)
            albumName.setValue(album.getName());
        else
            albumName.setValue(album.getEnglishName());

        artistName.set(album.getArtists().get(0).getName());
        albumCost.set(album.getFinalPrice().toString());

    }

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
}
