package net.iGap.api.repository;

import android.util.Log;

import net.iGap.api.BeepTunesApi;
import net.iGap.api.apiService.ApiResponse;
import net.iGap.api.apiService.ApiServiceProvider;
import net.iGap.module.api.beepTunes.AlbumTrack;
import net.iGap.module.api.beepTunes.SearchAlbum;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BeepTunesRepository {
    private BeepTunesApi apiService = ApiServiceProvider.getBeepTunesClient();

    public void getSearchAlbum(String q, int page, ApiResponse<SearchAlbum> apiResponse) {
        apiResponse.onStart();
        Call<SearchAlbum> results = apiService.getSearchAlbume(q, page);
        results.enqueue(new Callback<SearchAlbum>() {
            @Override
            public void onResponse(Call<SearchAlbum> call, Response<SearchAlbum> response) {
                apiResponse.onResponse(response.body());
                apiResponse.onFinish();
            }

            @Override
            public void onFailure(Call<SearchAlbum> call, Throwable t) {
                apiResponse.onFailed(t.getMessage());
                apiResponse.onFinish();
            }
        });
    }

    public void getAlbumTrack(long id, ApiResponse<AlbumTrack> apiResponse) {
        apiResponse.onStart();
        Call<AlbumTrack> result = apiService.getAlbumeTrack(id);
        result.enqueue(new Callback<AlbumTrack>() {
            @Override
            public void onResponse(Call<AlbumTrack> call, Response<AlbumTrack> response) {
                apiResponse.onResponse(response.body());
                apiResponse.onFinish();
                Log.i("aabolfazl", "onResponse: "+response.toString());
            }

            @Override
            public void onFailure(Call<AlbumTrack> call, Throwable t) {
                apiResponse.onFailed(t.getMessage());
                apiResponse.onFinish();
            }
        });

    }

}
