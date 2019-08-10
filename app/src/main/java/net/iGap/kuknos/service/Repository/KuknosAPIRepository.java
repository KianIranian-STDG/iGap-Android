package net.iGap.kuknos.service.Repository;

import android.util.Log;

import net.iGap.api.BeepTunesApi;
import net.iGap.api.KuknosApi;
import net.iGap.api.KuknosHorizenApi;
import net.iGap.api.apiService.ApiResponse;
import net.iGap.api.apiService.ApiServiceProvider;
import net.iGap.kuknos.service.model.KuknosInfoM;
import net.iGap.kuknos.service.model.KuknosLoginM;
import net.iGap.module.api.beepTunes.Album;
import net.iGap.module.api.beepTunes.AlbumTrack;
import net.iGap.module.api.beepTunes.Albums;
import net.iGap.module.api.beepTunes.Artist;
import net.iGap.module.api.beepTunes.SearchArtist;
import net.iGap.module.api.beepTunes.SearchTrack;
import net.iGap.module.api.beepTunes.TrackInfo;

import org.stellar.sdk.responses.AccountResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class KuknosAPIRepository {
    private KuknosApi apiService = ApiServiceProvider.getKuknosClient();
    private KuknosHorizenApi apiHorizenService = ApiServiceProvider.getKuknosHorizonClient();

    public void getUserAuthentication(String phoneNum, String nID, ApiResponse<KuknosLoginM> apiResponse) {
        apiResponse.setProgressIndicator(true);
        apiService.getUserVerfication(phoneNum, nID).enqueue(new Callback<KuknosLoginM>() {
            @Override
            public void onResponse(Call<KuknosLoginM> call, Response<KuknosLoginM> response) {
                if (response.isSuccessful())
                    apiResponse.onResponse(response.body());
                else if (response.code() == 400) {
                    apiResponse.onFailed("registeredBefore");
                }
                apiResponse.setProgressIndicator(false);
            }

            @Override
            public void onFailure(Call<KuknosLoginM> call, Throwable t) {
                apiResponse.onFailed(t.getMessage());
                apiResponse.setProgressIndicator(false);
            }
        });
    }

    public void getUserInfo(ApiResponse<KuknosInfoM> apiResponse) {
        apiResponse.setProgressIndicator(true);
        apiService.getUserInfo().enqueue(new Callback<KuknosInfoM>() {
            @Override
            public void onResponse(Call<KuknosInfoM> call, Response<KuknosInfoM> response) {
                apiResponse.onResponse(response.body());
                apiResponse.setProgressIndicator(false);
            }

            @Override
            public void onFailure(Call<KuknosInfoM> call, Throwable t) {
                apiResponse.onFailed(t.getMessage());
                apiResponse.setProgressIndicator(false);
            }
        });
    }

    public void getUserAccount(String userID, ApiResponse<AccountResponse> apiResponse) {
        apiResponse.setProgressIndicator(true);
        apiHorizenService.getUserAccount(userID).enqueue(new Callback<AccountResponse>() {
            @Override
            public void onResponse(Call<AccountResponse> call, Response<AccountResponse> response) {
                apiResponse.onResponse(response.body());
                apiResponse.setProgressIndicator(false);
            }

            @Override
            public void onFailure(Call<AccountResponse> call, Throwable t) {
                apiResponse.onFailed(t.getMessage());
                apiResponse.setProgressIndicator(false);
            }
        });
    }

}
