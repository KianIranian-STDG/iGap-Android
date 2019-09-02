package net.iGap.news.repository.api;

import net.iGap.api.NewsApi;
import net.iGap.api.apiService.ApiResponse;
import net.iGap.api.apiService.ApiServiceProvider;
import net.iGap.news.repository.model.NewsGroup;
import net.iGap.news.repository.model.NewsList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.PUT;

public class NewsAPIRepository {

    public enum NewsType {LATEST, HITS, FEATURED}
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

    public void getNewsList(NewsType type, int start, int display, ApiResponse<NewsList> apiResponse) {
        switch (type){
            case LATEST:
                getLatestNews(start, display, apiResponse);
                break;
            case HITS:
                getHitNews(start, display, apiResponse);
                break;
            case FEATURED:
                getFeaturedNews(start, display, apiResponse);
                break;
        }
    }

    private void getLatestNews(int start, int display, ApiResponse<NewsList> apiResponse) {
        apiResponse.setProgressIndicator(true);
        apiService.getLatestNews(start, display).enqueue(new Callback<NewsList>() {
            @Override
            public void onResponse(Call<NewsList> call, Response<NewsList> response) {
                apiResponse.onResponse(response.body());
                apiResponse.setProgressIndicator(false);
            }

            @Override
            public void onFailure(Call<NewsList> call, Throwable t) {
                apiResponse.onFailed(t.getMessage());
                apiResponse.setProgressIndicator(false);
            }
        });
    }

    private void getHitNews(int start, int display, ApiResponse<NewsList> apiResponse) {
        apiResponse.setProgressIndicator(true);
        apiService.getMHitsNews(start, display).enqueue(new Callback<NewsList>() {
            @Override
            public void onResponse(Call<NewsList> call, Response<NewsList> response) {
                apiResponse.onResponse(response.body());
                apiResponse.setProgressIndicator(false);
            }

            @Override
            public void onFailure(Call<NewsList> call, Throwable t) {
                apiResponse.onFailed(t.getMessage());
                apiResponse.setProgressIndicator(false);
            }
        });
    }

    private void getFeaturedNews(int start, int display, ApiResponse<NewsList> apiResponse) {
        apiResponse.setProgressIndicator(true);
        apiService.getFeaturedNews(start, display).enqueue(new Callback<NewsList>() {
            @Override
            public void onResponse(Call<NewsList> call, Response<NewsList> response) {
                apiResponse.onResponse(response.body());
                apiResponse.setProgressIndicator(false);
            }

            @Override
            public void onFailure(Call<NewsList> call, Throwable t) {
                apiResponse.onFailed(t.getMessage());
                apiResponse.setProgressIndicator(false);
            }
        });
    }

}
