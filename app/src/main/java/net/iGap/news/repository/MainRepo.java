package net.iGap.news.repository;

import net.iGap.api.apiService.HandShakeCallback;
import net.iGap.api.apiService.ResponseCallback;
import net.iGap.news.repository.api.NewsAPIRepository;
import net.iGap.news.repository.model.NewsApiArg;
import net.iGap.news.repository.model.NewsFirstPage;
import net.iGap.news.repository.model.NewsGroup;
import net.iGap.news.repository.model.NewsList;
import net.iGap.news.repository.model.NewsPublisher;

import java.util.List;

public class MainRepo {

    private NewsAPIRepository repository = new NewsAPIRepository();

    public void getSlideNews(int start, int display, HandShakeCallback handShakeCallback, ResponseCallback<NewsList> apiResponse) {
        repository.getNewsList(new NewsApiArg(start, display, -1, NewsApiArg.NewsType.FEATURED_NEWS), handShakeCallback, apiResponse);
    }

    public void getMainPage(HandShakeCallback handShakeCallback, ResponseCallback<List<NewsFirstPage>> apiResponse) {
        repository.getMainPageNews(handShakeCallback, apiResponse);
    }

    public void getNewsGroups(HandShakeCallback handShakeCallback, ResponseCallback<NewsGroup> apiResponse) {
        repository.getNewsGroup(handShakeCallback, apiResponse);
    }

    public void getNewsPublishers(int start, int display, HandShakeCallback handShakeCallback, ResponseCallback<List<NewsPublisher>> apiResponse) {
        repository.getNewsPublishers(start, display, handShakeCallback,  apiResponse);
    }

    public void getNewsList(NewsApiArg arg, HandShakeCallback handShakeCallback, ResponseCallback<NewsList> apiResponse) {
        repository.getNewsList(arg, handShakeCallback, apiResponse);
    }
}
