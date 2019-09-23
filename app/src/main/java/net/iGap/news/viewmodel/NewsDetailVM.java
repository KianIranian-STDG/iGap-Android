package net.iGap.news.viewmodel;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import net.iGap.api.apiService.ApiResponse;
import net.iGap.news.repository.MainRepo;
import net.iGap.news.repository.model.NewsDetail;
import net.iGap.news.repository.model.NewsError;
import net.iGap.news.repository.model.NewsFPList;
import net.iGap.news.repository.model.NewsFirstPage;
import net.iGap.news.repository.model.NewsList;
import net.iGap.news.repository.model.NewsMainBTN;
import net.iGap.news.repository.model.NewsSlider;

import java.util.ArrayList;
import java.util.List;

public class NewsDetailVM extends ViewModel {

    private MutableLiveData<NewsDetail> data;
    private MutableLiveData<List<NewsDetail>> comments;
    private MutableLiveData<NewsError> error;
    private MutableLiveData<Boolean> progressState;
    private MainRepo repo;

    public NewsDetailVM() {
        error = new MutableLiveData<>();
        progressState = new MutableLiveData<>();
        repo = new MainRepo();
    }

    public void getData() {
        repo.getSlideNews(1, 5, new ApiResponse<NewsList>() {
            @Override
            public void onResponse(NewsList newsList) {
                getNews();
            }

            @Override
            public void onFailed(String errorM) {
                getNews();
            }

            @Override
            public void setProgressIndicator(boolean visibility) {
                if (visibility)
                    progressState.setValue(visibility);
            }
        });
    }

    private void getNews() {
        repo.getLastGroupNews(new ApiResponse<List<NewsFPList>>() {
            @Override
            public void onResponse(List<NewsFPList> newsFPList) {
//                addFakeData(newsFPList);
            }

            @Override
            public void onFailed(String errorM) {
//                error.setValue(new NewsError(true, "", "", R.string.news_serverError));
            }

            @Override
            public void setProgressIndicator(boolean visibility) {
                progressState.setValue(visibility);
            }
        });
    }

    public MutableLiveData<NewsError> getError() {
        return error;
    }

    public void setError(MutableLiveData<NewsError> error) {
        this.error = error;
    }

    public MutableLiveData<Boolean> getProgressState() {
        return progressState;
    }

    public void setProgressState(MutableLiveData<Boolean> progressState) {
        this.progressState = progressState;
    }
}
