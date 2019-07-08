package net.iGap.api.repository;

import net.iGap.api.BeepTunesApi;
import net.iGap.api.apiService.ApiResponse;
import net.iGap.api.apiService.ApiServiceProvider;
import net.iGap.module.api.beepTunes.Album;
import net.iGap.module.api.beepTunes.AlbumTrack;
import net.iGap.module.api.beepTunes.Albums;
import net.iGap.module.api.beepTunes.Artist;
import net.iGap.module.api.beepTunes.SearchArtist;
import net.iGap.module.api.beepTunes.SearchTrack;
import net.iGap.module.api.beepTunes.TrackInfo;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BeepTunesRepository {
    private BeepTunesApi apiService = ApiServiceProvider.getBeepTunesClient();

    public void getSearchAlbum(String q, int page, ApiResponse<Albums> apiResponse) {
        apiResponse.onStart();
        apiService.getSearchAlbum(q, page).enqueue(new Callback<Albums>() {
            @Override
            public void onResponse(Call<Albums> call, Response<Albums> response) {
                apiResponse.onResponse(response.body());
                apiResponse.onFinish();
            }

            @Override
            public void onFailure(Call<Albums> call, Throwable t) {
                apiResponse.onFailed(t.getMessage());
                apiResponse.onFinish();
            }
        });
    }

    public void getAlbumTrack(long id, ApiResponse<AlbumTrack> apiResponse) {
        apiResponse.onStart();
        apiService.getAlbumTrack(id).enqueue(new Callback<AlbumTrack>() {
            @Override
            public void onResponse(Call<AlbumTrack> call, Response<AlbumTrack> response) {
                if (response.isSuccessful()) {
                    apiResponse.onResponse(response.body());
                    apiResponse.onFinish();
                }
            }

            @Override
            public void onFailure(Call<AlbumTrack> call, Throwable t) {
                apiResponse.onFailed(t.getMessage());
                apiResponse.onFinish();
            }
        });
    }

    public void getAlbumInfo(long id, ApiResponse<Album> apiResponse) {
        apiResponse.onStart();
        apiService.getAlbumInfo(id).enqueue(new Callback<Album>() {
            @Override
            public void onResponse(Call<Album> call, Response<Album> response) {
                apiResponse.onResponse(response.body());
                apiResponse.onFinish();
            }

            @Override
            public void onFailure(Call<Album> call, Throwable t) {
                apiResponse.onFailed(t.getMessage());
                apiResponse.onFinish();
            }
        });
    }

    public void getSearchTrack(String q, int page, ApiResponse<SearchTrack> apiResponse) {
        apiResponse.onStart();
        apiService.getSearchTrack(q, page).enqueue(new Callback<SearchTrack>() {
            @Override
            public void onResponse(Call<SearchTrack> call, Response<SearchTrack> response) {
                apiResponse.onResponse(response.body());
                apiResponse.onFinish();
            }

            @Override
            public void onFailure(Call<SearchTrack> call, Throwable t) {
                apiResponse.onFailed(t.getMessage());
                apiResponse.onFinish();
            }
        });
    }

    public void getTrackInfo(long id, ApiResponse<TrackInfo> apiResponse) {
        apiResponse.onStart();
        apiService.getTrackInfo(id).enqueue(new Callback<TrackInfo>() {
            @Override
            public void onResponse(Call<TrackInfo> call, Response<TrackInfo> response) {
                apiResponse.onResponse(response.body());
                apiResponse.onFinish();
            }

            @Override
            public void onFailure(Call<TrackInfo> call, Throwable t) {
                apiResponse.onFinish();
                apiResponse.onFailed(t.getMessage());
            }
        });
    }

    public void purchaseTrack() {
        // TODO: 7/7/19 do it
    }

    public void getArtistAlbums(long id, ApiResponse<Albums> apiResponse) {
        apiResponse.onStart();
        apiService.getArtistAlbums(id).enqueue(new Callback<Albums>() {
            @Override
            public void onResponse(Call<Albums> call, Response<Albums> response) {
                apiResponse.onResponse(response.body());
                apiResponse.onFinish();
            }

            @Override
            public void onFailure(Call<Albums> call, Throwable t) {
                apiResponse.onFailed(t.getMessage());
                apiResponse.onFinish();
            }
        });
    }

    public void getArtistInfo(long id, ApiResponse<Artist> apiResponse) {
        apiResponse.onStart();
        apiService.getArtistInfo(id).enqueue(new Callback<Artist>() {
            @Override
            public void onResponse(Call<Artist> call, Response<Artist> response) {
                apiResponse.onResponse(response.body());
                apiResponse.onFinish();
            }

            @Override
            public void onFailure(Call<Artist> call, Throwable t) {
                apiResponse.onFailed(t.getMessage());
                apiResponse.onFinish();
            }
        });
    }

    public void getSearchArtist(String q, int page, ApiResponse<SearchArtist> apiResponse) {
        apiResponse.onStart();
        apiService.getSearchArtist(q, page).enqueue(new Callback<SearchArtist>() {
            @Override
            public void onResponse(Call<SearchArtist> call, Response<SearchArtist> response) {
                apiResponse.onResponse(response.body());
                apiResponse.onFinish();
            }

            @Override
            public void onFailure(Call<SearchArtist> call, Throwable t) {
                apiResponse.onFailed(t.getMessage());
                apiResponse.onFinish();
            }
        });
    }
}
