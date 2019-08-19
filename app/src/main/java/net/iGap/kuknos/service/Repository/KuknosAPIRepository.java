package net.iGap.kuknos.service.Repository;

import android.util.Log;

import net.iGap.api.BeepTunesApi;
import net.iGap.api.KuknosApi;
import net.iGap.api.KuknosHorizenApi;
import net.iGap.api.apiService.ApiResponse;
import net.iGap.api.apiService.ApiServiceProvider;
import net.iGap.kuknos.service.model.KuknosInfoM;
import net.iGap.kuknos.service.model.KuknosLoginM;
import net.iGap.kuknos.service.model.KuknosSendM;
import net.iGap.module.api.beepTunes.Album;
import net.iGap.module.api.beepTunes.AlbumTrack;
import net.iGap.module.api.beepTunes.Albums;
import net.iGap.module.api.beepTunes.Artist;
import net.iGap.module.api.beepTunes.SearchArtist;
import net.iGap.module.api.beepTunes.SearchTrack;
import net.iGap.module.api.beepTunes.TrackInfo;

import org.stellar.sdk.responses.AccountResponse;
import org.stellar.sdk.responses.AssetResponse;
import org.stellar.sdk.responses.Page;
import org.stellar.sdk.responses.SubmitTransactionResponse;
import org.stellar.sdk.responses.operations.OperationResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class KuknosAPIRepository {
    private KuknosApi apiService = ApiServiceProvider.getKuknosClient();
    //private KuknosHorizenApi apiHorizenService = ApiServiceProvider.getKuknosHorizonClient();

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
        KuknosAPIAsync<AccountResponse> temp = new KuknosAPIAsync(apiResponse, KuknosAPIAsync.API.USER_ACCOUNT);
        temp.execute(userID);
    }

    public void paymentUser(KuknosSendM model, ApiResponse<SubmitTransactionResponse> apiResponse) {
        KuknosAPIAsync<SubmitTransactionResponse> temp = new KuknosAPIAsync(apiResponse, KuknosAPIAsync.API.PAYMENT_SEND);
        temp.execute(model.getSrc(), model.getDest(), model.getAmount(), model.getMemo());
    }

    public void getUserHistory(String userID, ApiResponse<Page<OperationResponse>> apiResponse) {
        KuknosAPIAsync<Page<OperationResponse>> temp = new KuknosAPIAsync(apiResponse, KuknosAPIAsync.API.PAYMENTS_ACCOUNT);
        temp.execute(userID);
    }

    public void getAssets(ApiResponse<Page<AssetResponse>> apiResponse) {
        KuknosAPIAsync<Page<AssetResponse>> temp = new KuknosAPIAsync(apiResponse, KuknosAPIAsync.API.ASSETS);
        temp.execute();
    }

    public void changeTrust(String accountSeed, String code, String issuer, ApiResponse<SubmitTransactionResponse> apiResponse) {
        KuknosAPIAsync<SubmitTransactionResponse> temp = new KuknosAPIAsync(apiResponse, KuknosAPIAsync.API.CHANGE_TRUST);
        temp.execute(accountSeed, code, issuer);
    }
}
