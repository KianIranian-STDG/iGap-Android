package net.iGap.news.repository.api;

import net.iGap.api.NewsApi;
import net.iGap.api.apiService.ApiResponse;
import net.iGap.api.apiService.ApiServiceProvider;
import net.iGap.news.repository.model.NewsGroup;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewsAPIRepository {

    private NewsApi apiService = ApiServiceProvider.getNewsClient();

    public void getNewsGroup(ApiResponse<NewsGroup> apiResponse) {
        apiResponse.setProgressIndicator(true);
        apiService.getNewsGroups().enqueue(new Callback<NewsGroup>() {
            @Override
            public void onResponse(Call<NewsGroup> call, Response<NewsGroup> response) {
                apiResponse.onResponse(response.body());
                apiResponse.setProgressIndicator(false);
            }

            @Override
            public void onFailure(Call<NewsGroup> call, Throwable t) {
                apiResponse.onFailed(t.getMessage());
                apiResponse.setProgressIndicator(false);
            }
        });
    }

}
