package net.iGap.news.repository.api;

import net.iGap.api.NewsApi;
import net.iGap.api.apiService.ApiResponse;
import net.iGap.api.apiService.ApiServiceProvider;
import net.iGap.news.repository.model.NewsDetail;
import net.iGap.news.repository.model.NewsFPList;
import net.iGap.news.repository.model.NewsGroup;
import net.iGap.news.repository.model.NewsList;
import net.iGap.news.repository.model.NewsPN;
import net.iGap.news.repository.model.NewsPublisher;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewsAPIRepository {

    public enum NewsType {LATEST, HITS, FEATURED}

    private NewsApi apiService = ApiServiceProvider.getNewsClient();

    public void getMainPageNews(int numOfNewsPerSource, ApiResponse<List<NewsFPList>> apiResponse) {
        apiResponse.setProgressIndicator(true);
        apiService.getMainPageNews(numOfNewsPerSource).enqueue(new Callback<List<NewsFPList>>() {
            @Override
            public void onResponse(Call<List<NewsFPList>> call, Response<List<NewsFPList>> response) {
                apiResponse.onResponse(response.body());
                apiResponse.setProgressIndicator(false);
            }

            @Override
            public void onFailure(Call<List<NewsFPList>> call, Throwable t) {
                apiResponse.onFailed(t.getMessage());
                apiResponse.setProgressIndicator(false);
            }
        });
    }

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
        switch (type) {
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

    public void getFeaturedNewsForGroup(int groupID, int start, int display, ApiResponse<NewsList> apiResponse) {
        apiResponse.setProgressIndicator(true);
        apiService.getFeaturedNewsForGroup(groupID, start, display).enqueue(new Callback<NewsList>() {
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

    public void getErgentNewsForGroup(int groupID, int start, int display, ApiResponse<NewsList> apiResponse) {
        apiResponse.setProgressIndicator(true);
        apiService.getErgentNewsForGroup(groupID, start, display).enqueue(new Callback<NewsList>() {
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

    public void getGroupsNews(int groupID, int start, int display, ApiResponse<NewsList> apiResponse) {
        apiResponse.setProgressIndicator(true);
        apiService.getGroupNews(groupID, start, display).enqueue(new Callback<NewsList>() {
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

    public void getNewsDetail(int newsID, ApiResponse<NewsDetail> apiResponse) {
        apiResponse.setProgressIndicator(true);
        apiService.getNewsDetail(newsID).enqueue(new Callback<NewsDetail>() {
            @Override
            public void onResponse(Call<NewsDetail> call, Response<NewsDetail> response) {
                apiResponse.onResponse(response.body());
                apiResponse.setProgressIndicator(false);
            }

            @Override
            public void onFailure(Call<NewsDetail> call, Throwable t) {
                apiResponse.onFailed(t.getMessage());
                apiResponse.setProgressIndicator(false);
            }
        });
    }

    public void getNewsPublishers(int start, int display, ApiResponse<List<NewsPublisher>> apiResponse) {
        apiResponse.setProgressIndicator(true);
        apiService.getNewsPublishers(start, display).enqueue(new Callback<List<NewsPublisher>>() {
            @Override
            public void onResponse(Call<List<NewsPublisher>> call, Response<List<NewsPublisher>> response) {
                apiResponse.onResponse(response.body());
                apiResponse.setProgressIndicator(false);
            }

            @Override
            public void onFailure(Call<List<NewsPublisher>> call, Throwable t) {
                apiResponse.onFailed(t.getMessage());
                apiResponse.setProgressIndicator(false);
            }
        });
    }

    public void getPublisherNews(int publisherID, int start, int display, ApiResponse<NewsPN> apiResponse) {
        apiResponse.setProgressIndicator(true);
        apiService.getPublisherNews(publisherID, start, display).enqueue(new Callback<NewsPN>() {
            @Override
            public void onResponse(Call<NewsPN> call, Response<NewsPN> response) {
                apiResponse.onResponse(response.body());
                apiResponse.setProgressIndicator(false);
            }

            @Override
            public void onFailure(Call<NewsPN> call, Throwable t) {
                apiResponse.onFailed(t.getMessage());
                apiResponse.setProgressIndicator(false);
            }
        });
    }
}
