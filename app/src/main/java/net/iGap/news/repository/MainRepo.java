package net.iGap.news.repository;

import net.iGap.api.apiService.ApiResponse;
import net.iGap.news.repository.api.NewsAPIRepository;
import net.iGap.news.repository.model.NewsFPList;
import net.iGap.news.repository.model.NewsGroup;
import net.iGap.news.repository.model.NewsList;
import net.iGap.news.repository.model.NewsPublisher;

import java.util.List;

public class MainRepo {

    private NewsAPIRepository repository = new NewsAPIRepository();

    public void getSlideNews(int start, int display, ApiResponse<NewsList> apiResponse) {
        repository.getNewsList(NewsAPIRepository.NewsType.LATEST, start, display, apiResponse);
    }

    public void getLastGroupNews(ApiResponse<List<NewsFPList>> apiResponse) {
        repository.getMainPageNews(1, apiResponse);
    }

    public void getNewsGroups(ApiResponse<NewsGroup> apiResponse) {
        repository.getNewsGroup(apiResponse);
    }

    public void getNewsPublishers(int start, int display, ApiResponse<List<NewsPublisher>> apiResponse) {
        repository.getNewsPublishers(start, display, apiResponse);
    }

}
