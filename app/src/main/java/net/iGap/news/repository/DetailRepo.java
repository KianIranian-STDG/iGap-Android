package net.iGap.news.repository;

import net.iGap.api.apiService.ApiResponse;
import net.iGap.news.repository.api.NewsAPIRepository;
import net.iGap.news.repository.model.NewsApiArg;
import net.iGap.news.repository.model.NewsComment;
import net.iGap.news.repository.model.NewsDetail;
import net.iGap.news.repository.model.NewsFPList;
import net.iGap.news.repository.model.NewsGroup;
import net.iGap.news.repository.model.NewsList;
import net.iGap.news.repository.model.NewsPublisher;

import java.util.List;

public class DetailRepo {

    private NewsAPIRepository repository = new NewsAPIRepository();

    public void getNewsDetail(int newsID, ApiResponse<NewsDetail> apiResponse) {
        repository.getNewsDetail(newsID, apiResponse);
    }

    public void getNewsComment(int newsID, int start, int display, ApiResponse<NewsComment> apiResponse) {
        repository.getNewsComment(newsID, start, display, apiResponse);
    }

    public void postNewsComment(String newsID, String comment, String author, String email, ApiResponse<NewsDetail> apiResponse) {
        repository.postNewsComment(newsID, comment, author, email, apiResponse);
    }

}
