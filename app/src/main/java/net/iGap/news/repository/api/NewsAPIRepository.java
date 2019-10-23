package net.iGap.news.repository.api;

import net.iGap.api.NewsApi;
import net.iGap.api.apiService.ApiInitializer;
import net.iGap.api.apiService.ApiServiceProvider;
import net.iGap.api.apiService.HandShakeCallback;
import net.iGap.api.apiService.ResponseCallback;
import net.iGap.news.repository.model.NewsApiArg;
import net.iGap.news.repository.model.NewsComment;
import net.iGap.news.repository.model.NewsDetail;
import net.iGap.news.repository.model.NewsFirstPage;
import net.iGap.news.repository.model.NewsGroup;
import net.iGap.news.repository.model.NewsList;
import net.iGap.news.repository.model.NewsPN;
import net.iGap.news.repository.model.NewsPublisher;
import net.iGap.news.repository.model.NewsSubmitComment;

import java.util.List;

public class NewsAPIRepository {

    private NewsApi apiService = ApiServiceProvider.getNewsClient();

    public void getMainPageNews(HandShakeCallback handShakeCallback, ResponseCallback<List<NewsFirstPage>> apiResponse) {

        new ApiInitializer<List<NewsFirstPage>>().initAPI(apiService.getMainPageNews(), handShakeCallback, apiResponse);

    }

    public void getNewsGroup(HandShakeCallback handShakeCallback, ResponseCallback<NewsGroup> apiResponse) {
        new ApiInitializer<NewsGroup>().initAPI(apiService.getNewsGroups(0,0), handShakeCallback, apiResponse);
    }

    public void getNewsList(NewsApiArg arg, HandShakeCallback handShakeCallback, ResponseCallback<NewsList> apiResponse) {
        switch (arg.getmType()) {
            case Latest:
                getLatestNews(arg.getStart(), arg.getDisplay(), handShakeCallback, apiResponse);
                break;
            case MOST_HITS:
                getHitNews(arg.getStart(), arg.getDisplay(), handShakeCallback, apiResponse);
                break;
            case FEATURED_NEWS:
                getFeaturedNews(arg.getStart(), arg.getDisplay(), handShakeCallback, apiResponse);
                break;
            case GROUP_NEWS:
                getGroupsNews(arg.getGroupID(), arg.getStart(), arg.getDisplay(), handShakeCallback, apiResponse);
                break;
            case FEATURED_GROUP:
                getFeaturedNewsForGroup(arg.getGroupID(), arg.getStart(), arg.getDisplay(), handShakeCallback, apiResponse);
                break;
            case ERGENT_GROUP:
                getErgentNewsForGroup(arg.getGroupID(), arg.getStart(), arg.getDisplay(), handShakeCallback, apiResponse);
                break;
            case ERGENT:
                getErgentNews(arg.getStart(), arg.getDisplay(), handShakeCallback, apiResponse);
                break;
            case CONTROVERSIAL_NEWS:
                getMControversialNews(arg.getStart(), arg.getDisplay(), handShakeCallback, apiResponse);
                break;
            case RELATED_NEWS:
                getRelatedNews(arg.getGroupID(), arg.getStart(), arg.getDisplay(), handShakeCallback, apiResponse);
                break;
        }
    }

    private void getLatestNews(int start, int display, HandShakeCallback handShakeCallback, ResponseCallback<NewsList> apiResponse) {
        new ApiInitializer<NewsList>().initAPI(apiService.getLatestNews(start, display), handShakeCallback, apiResponse);
    }

    private void getHitNews(int start, int display, HandShakeCallback handShakeCallback, ResponseCallback<NewsList> apiResponse) {
        new ApiInitializer<NewsList>().initAPI(apiService.getMHitsNews(start, display), handShakeCallback, apiResponse);
    }

    private void getMControversialNews(int start, int display, HandShakeCallback handShakeCallback, ResponseCallback<NewsList> apiResponse) {
        new ApiInitializer<NewsList>().initAPI(apiService.getMControversialNews(start, display), handShakeCallback, apiResponse);
    }

    private void getFeaturedNews(int start, int display, HandShakeCallback handShakeCallback, ResponseCallback<NewsList> apiResponse) {
        new ApiInitializer<NewsList>().initAPI(apiService.getFeaturedNews(start, display), handShakeCallback, apiResponse);
    }

    private void getFeaturedNewsForGroup(int groupID, int start, int display, HandShakeCallback handShakeCallback, ResponseCallback<NewsList> apiResponse) {
        new ApiInitializer<NewsList>().initAPI(apiService.getFeaturedNewsForGroup(groupID, start, display), handShakeCallback, apiResponse);
    }

    private void getErgentNewsForGroup(int groupID, int start, int display, HandShakeCallback handShakeCallback, ResponseCallback<NewsList> apiResponse) {
        new ApiInitializer<NewsList>().initAPI(apiService.getErgentNewsForGroup(groupID, start, display), handShakeCallback, apiResponse);
    }

    private void getErgentNews(int start, int display, HandShakeCallback handShakeCallback, ResponseCallback<NewsList> apiResponse) {
        new ApiInitializer<NewsList>().initAPI(apiService.getErgentNews(start, display), handShakeCallback, apiResponse);
    }

    private void getGroupsNews(int groupID, int start, int display, HandShakeCallback handShakeCallback, ResponseCallback<NewsList> apiResponse) {
        new ApiInitializer<NewsList>().initAPI(apiService.getGroupNews(groupID, start, display), handShakeCallback, apiResponse);
    }

    private void getRelatedNews(int newsID, int start, int display, HandShakeCallback handShakeCallback, ResponseCallback<NewsList> apiResponse) {
        new ApiInitializer<NewsList>().initAPI(apiService.getRelatedNews(newsID, start, display), handShakeCallback, apiResponse);
    }

    public void getNewsDetail(int newsID, HandShakeCallback handShakeCallback, ResponseCallback<NewsDetail> apiResponse) {
        new ApiInitializer<NewsDetail>().initAPI(apiService.getNewsDetail(newsID), handShakeCallback, apiResponse);
    }

    public void getNewsComment(int newsID, int start, int display, HandShakeCallback handShakeCallback, ResponseCallback<NewsComment> apiResponse) {
        new ApiInitializer<NewsComment>().initAPI(apiService.getNewsComment(newsID, start, display), handShakeCallback, apiResponse);
    }

    public void postNewsComment(String newsID, String comment, String author, String email, HandShakeCallback handShakeCallback, ResponseCallback<NewsDetail> apiResponse) {
        new ApiInitializer<NewsDetail>().initAPI(apiService.postNewsComment(new NewsSubmitComment(newsID, comment, author, email)), handShakeCallback, apiResponse);
    }

    public void getNewsPublishers(int start, int display, HandShakeCallback handShakeCallback, ResponseCallback<List<NewsPublisher>> apiResponse) {
        new ApiInitializer<List<NewsPublisher>>().initAPI(apiService.getNewsPublishers(start, display), handShakeCallback, apiResponse);
    }

    public void getPublisherNews(int publisherID, int start, int display, HandShakeCallback handShakeCallback, ResponseCallback<NewsPN> apiResponse) {
        new ApiInitializer<NewsPN>().initAPI(apiService.getPublisherNews(publisherID, start, display), handShakeCallback, apiResponse);
    }
}
