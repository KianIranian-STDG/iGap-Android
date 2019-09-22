package net.iGap.news.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import net.iGap.R;
import net.iGap.api.apiService.ApiResponse;
import net.iGap.news.repository.MainRepo;
import net.iGap.news.repository.model.NewsError;
import net.iGap.news.repository.model.NewsGroup;
import net.iGap.news.repository.model.NewsPublisher;

import java.util.List;

public class NewsPublisherListVM extends ViewModel {

    private MutableLiveData<List<NewsPublisher>> mData;
    private MutableLiveData<NewsError> error;
    private MutableLiveData<Boolean> progressState;
    private MainRepo repo;

    public NewsPublisherListVM() {
        mData = new MutableLiveData<>();
        error = new MutableLiveData<>();
        progressState = new MutableLiveData<>();
        repo = new MainRepo();
    }

    public void getData() {
        repo.getNewsPublishers(0, 50, new ApiResponse<List<NewsPublisher>>() {
            @Override
            public void onResponse(List<NewsPublisher> newsPublishers) {
                mData.setValue(newsPublishers);
            }

            @Override
            public void onFailed(String errorM) {
                error.setValue(new NewsError(true, "", "", R.string.news_serverError));
            }

            @Override
            public void setProgressIndicator(boolean visibility) {
                progressState.setValue(visibility);
            }
        });
    }

    public MutableLiveData<List<NewsPublisher>> getmData() {
        return mData;
    }

    public void setmData(MutableLiveData<List<NewsPublisher>> mData) {
        this.mData = mData;
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
