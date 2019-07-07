package net.iGap.api.repository;

import android.util.Log;

import net.iGap.api.PopularChannelApi;
import net.iGap.api.apiService.ApiResponse;
import net.iGap.api.apiService.ApiServiceProvider;
import net.iGap.module.api.popularChannel.NormalChannel;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class PopularChannelRepository {

    private PopularChannelApi channelApi = ApiServiceProvider.getChannelApi();

    public void getNormalChannel(String orderBy, String sort, ApiResponse<NormalChannel> apiResponse) {
        apiResponse.onStart();
        channelApi.getNormalChannel(orderBy, sort).enqueue(new Callback<NormalChannel>() {
            @Override
            public void onResponse(Call<NormalChannel> call, Response<NormalChannel> response) {
                if (response.isSuccessful()) {
                    apiResponse.onResponse(response.body());
                    apiResponse.onFinish();
                }
            }

            @Override
            public void onFailure(Call<NormalChannel> call, Throwable t) {
                apiResponse.onFailed(t.getMessage());
                apiResponse.onFinish();
            }
        });
    }

}
