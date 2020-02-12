package net.iGap.repository.news;

import net.iGap.api.NewsApi;
import net.iGap.api.apiService.ApiInitializer;
import net.iGap.observers.interfaces.HandShakeCallback;
import net.iGap.observers.interfaces.ResponseCallback;
import net.iGap.api.apiService.RetrofitFactory;
import net.iGap.model.news.NewsApiArg;
import net.iGap.model.news.NewsComment;
import net.iGap.model.news.NewsDetail;
import net.iGap.model.news.NewsFirstPage;
import net.iGap.model.news.NewsGroup;
import net.iGap.model.news.NewsList;
import net.iGap.model.news.NewsPN;
import net.iGap.model.news.NewsPublisher;
import net.iGap.model.news.NewsSubmitComment;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class NewsAPIRepository {

    private NewsApi apiService = new RetrofitFactory().getNewsRetrofit();

    public void getMainPageNews(HandShakeCallback handShakeCallback, ResponseCallback<List<NewsFirstPage>> apiResponse) {
        new ApiInitializer<List<NewsFirstPage>>().initAPI(apiService.getMainPageNews(), handShakeCallback, apiResponse);
    }

    public void getNewsGroup(HandShakeCallback handShakeCallback, ResponseCallback<NewsGroup> apiResponse) {
        new ApiInitializer<NewsGroup>().initAPI(apiService.getNewsGroups(0, 0), handShakeCallback, apiResponse);
    }

    public void getNewsList(@NotNull NewsApiArg arg, HandShakeCallback handShakeCallback, ResponseCallback<NewsList> apiResponse) {
        switch (arg.getmType()) {
            case Latest:
                getLatestNews(arg.getStart(), arg.getDisplay(), handShakeCallback, apiResponse);
                break;
            case MOST_HITS:
                getHitNews(arg.getGroupID(), arg.getStart(), arg.getDisplay(), handShakeCallback, apiResponse);
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
                getMControversialNews(arg.getGroupID(), arg.getStart(), arg.getDisplay(), handShakeCallback, apiResponse);
                break;
            case RELATED_NEWS:
                getRelatedNews(arg.getGroupID(), arg.getStart(), arg.getDisplay(), handShakeCallback, apiResponse);
                break;
        }
    }

    private void getLatestNews(int start, int display, HandShakeCallback handShakeCallback, ResponseCallback<NewsList> apiResponse) {
        new ApiInitializer<NewsList>().initAPI(apiService.getLatestNews(start, display), handShakeCallback, apiResponse);
    }

    private void getHitNews(int groupID, int start, int display, HandShakeCallback handShakeCallback, ResponseCallback<NewsList> apiResponse) {
        new ApiInitializer<NewsList>().initAPI(apiService.getMHitsNews(groupID, start, display), handShakeCallback, apiResponse);
    }

    private void getMControversialNews(int groupID, int start, int display, HandShakeCallback handShakeCallback, ResponseCallback<NewsList> apiResponse) {
        new ApiInitializer<NewsList>().initAPI(apiService.getMControversialNews(groupID, start, display), handShakeCallback, apiResponse);
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

    public void getNewsComment(int newsID, int start, int display, HandShakeCallback handShakeCallback, ResponseCallback<List<NewsComment>> apiResponse) {
        new ApiInitializer<List<NewsComment>>().initAPI(apiService.getNewsComment(newsID, start, display), handShakeCallback, apiResponse);
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
