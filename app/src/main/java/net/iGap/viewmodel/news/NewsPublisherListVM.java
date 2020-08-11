package net.iGap.viewmodel.news;

import androidx.lifecycle.MutableLiveData;

import net.iGap.R;
import net.iGap.api.apiService.BaseAPIViewModel;
import net.iGap.model.news.NewsError;
import net.iGap.model.news.NewsPublisher;
import net.iGap.observers.interfaces.ResponseCallback;
import net.iGap.repository.news.NewsAPIRepository;

import java.util.List;

public class NewsPublisherListVM extends BaseAPIViewModel {

    private MutableLiveData<List<NewsPublisher>> mData;
    private MutableLiveData<NewsError> error;
    private MutableLiveData<Boolean> progressState;
    private NewsAPIRepository repo;

    public NewsPublisherListVM() {
        mData = new MutableLiveData<>();
        error = new MutableLiveData<>();
        progressState = new MutableLiveData<>();
        repo = new NewsAPIRepository();
    }

    public void getData() {
        progressState.setValue(true);
        repo.getNewsPublishers(0, 50, this, new ResponseCallback<List<NewsPublisher>>() {
            @Override
            public void onSuccess(List<NewsPublisher> data) {
                mData.setValue(data);
                progressState.setValue(false);
            }

            @Override
            public void onError(String e) {
                error.setValue(new NewsError(true, "", "", R.string.news_serverError));
                progressState.setValue(false);
            }

            @Override
            public void onFailed() {
                progressState.setValue(false);
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
