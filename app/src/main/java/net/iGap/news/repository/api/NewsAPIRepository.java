package net.iGap.news.repository.api;

import android.util.Log;

import net.iGap.api.NewsApi;
import net.iGap.api.apiService.ApiResponse;
import net.iGap.api.apiService.ApiServiceProvider;
import net.iGap.news.repository.model.NewsApiArg;
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
import yogesh.firzen.mukkiasevaigal.P;

public class NewsAPIRepository {

    private NewsApi apiService = ApiServiceProvider.getNewsClient();

    public void getMainPageNews(int numOfNewsPerSource, ApiResponse<List<NewsFPList>> apiResponse) {
        apiResponse.setProgressIndicator(true);
        apiService.getMainPageNews(numOfNewsPerSource).enqueue(new Callback<List<NewsFPList>>() {
            @Override
            public void onResponse(Call<List<NewsFPList>> call, Response<List<NewsFPList>> response) {
                if (!response.isSuccessful()) {
                    onFailure(call, new Exception("" + response.code()));
                    return;
                }
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
        apiService.getNewsGroups(0,0).enqueue(new Callback<NewsGroup>() {
            @Override
            public void onResponse(Call<NewsGroup> call, Response<NewsGroup> response) {
                if (!response.isSuccessful()) {
                    onFailure(call, new Exception("" + response.code()));
                    return;
                }
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

    public void getNewsList(NewsApiArg arg, ApiResponse<NewsList> apiResponse) {
        switch (arg.getmType()) {
            case Latest:
                getLatestNews(arg.getStart(), arg.getDisplay(), apiResponse);
                break;
            case MOST_HITS:
                getHitNews(arg.getStart(), arg.getDisplay(), apiResponse);
                break;
            case FEATURED_NEWS:
                getFeaturedNews(arg.getStart(), arg.getDisplay(), apiResponse);
                break;
            case GROUP_NEWS:
                getGroupsNews(arg.getGroupID(), arg.getStart(), arg.getDisplay(), apiResponse);
                break;
            case FEATURED_GROUP:
                getFeaturedNewsForGroup(arg.getGroupID(), arg.getStart(), arg.getDisplay(), apiResponse);
                break;
            case ERGENT_GROUP:
                getErgentNewsForGroup(arg.getGroupID(), arg.getStart(), arg.getDisplay(), apiResponse);
                break;
            case ERGENT:
                getErgentNews(arg.getStart(), arg.getDisplay(), apiResponse);
                break;
            case CONTROVERSIAL_NEWS:
                getMControversialNews(arg.getStart(), arg.getDisplay(), apiResponse);
                break;
        }
    }

    private void getLatestNews(int start, int display, ApiResponse<NewsList> apiResponse) {
        apiResponse.setProgressIndicator(true);
        apiService.getLatestNews(start, display).enqueue(new Callback<NewsList>() {
            @Override
            public void onResponse(Call<NewsList> call, Response<NewsList> response) {
                if (!response.isSuccessful()) {
                    onFailure(call, new Exception("" + response.code()));
                    return;
                }
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
                if (!response.isSuccessful()) {
                    onFailure(call, new Exception("" + response.code()));
                    return;
                }
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

    private void getMControversialNews(int start, int display, ApiResponse<NewsList> apiResponse) {
        apiResponse.setProgressIndicator(true);
        apiService.getMControversialNews(start, display).enqueue(new Callback<NewsList>() {
            @Override
            public void onResponse(Call<NewsList> call, Response<NewsList> response) {
                if (!response.isSuccessful()) {
                    onFailure(call, new Exception("" + response.code()));
                    return;
                }
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
                if (!response.isSuccessful()) {
                    onFailure(call, new Exception("" + response.code()));
                    return;
                }
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
                if (!response.isSuccessful()) {
                    onFailure(call, new Exception("" + response.code()));
                    return;
                }
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
                if (!response.isSuccessful()) {
                    onFailure(call, new Exception("" + response.code()));
                    return;
                }
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

    public void getErgentNews(int start, int display, ApiResponse<NewsList> apiResponse) {
        apiResponse.setProgressIndicator(true);
        apiService.getErgentNews(start, display).enqueue(new Callback<NewsList>() {
            @Override
            public void onResponse(Call<NewsList> call, Response<NewsList> response) {
                if (!response.isSuccessful()) {
                    onFailure(call, new Exception("" + response.code()));
                    return;
                }
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
                if (!response.isSuccessful()) {
                    onFailure(call, new Exception("" + response.code()));
                    return;
                }
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
                if (!response.isSuccessful()) {
                    onFailure(call, new Exception("" + response.code()));
                    return;
                }
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

    public void getNewsComment(int newsID, int start, int display, ApiResponse<NewsDetail> apiResponse) {
        apiResponse.setProgressIndicator(true);
        apiService.getNewsComment(newsID, start, display).enqueue(new Callback<NewsDetail>() {
            @Override
            public void onResponse(Call<NewsDetail> call, Response<NewsDetail> response) {
                if (!response.isSuccessful()) {
                    onFailure(call, new Exception("" + response.code()));
                    return;
                }
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

    public void postNewsComment(String newsID, String comment, String author, String email, ApiResponse<NewsDetail> apiResponse) {
        apiResponse.setProgressIndicator(true);
        apiService.postNewsComment(newsID, comment, author, email).enqueue(new Callback<NewsDetail>() {
            @Override
            public void onResponse(Call<NewsDetail> call, Response<NewsDetail> response) {
                if (!response.isSuccessful()) {
                    onFailure(call, new Exception("" + response.code()));
                    return;
                }
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
                if (!response.isSuccessful()) {
                    onFailure(call, new Exception("" + response.code()));
                    return;
                }
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
                if (!response.isSuccessful()) {
                    onFailure(call, new Exception("" + response.code()));
                    return;
                }
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
