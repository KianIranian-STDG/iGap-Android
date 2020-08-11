package net.iGap.viewmodel.news;

import androidx.lifecycle.MutableLiveData;

import net.iGap.R;
import net.iGap.api.apiService.BaseAPIViewModel;
import net.iGap.model.news.NewsApiArg;
import net.iGap.model.news.NewsError;
import net.iGap.model.news.NewsList;
import net.iGap.observers.interfaces.ResponseCallback;
import net.iGap.repository.news.NewsAPIRepository;

public class NewsListVM extends BaseAPIViewModel {

    private MutableLiveData<NewsList> mData;
    private MutableLiveData<NewsError> error;
    private MutableLiveData<Boolean> progressState;
    private NewsAPIRepository repo;
    private NewsApiArg apiArg;

    public NewsListVM() {
        mData = new MutableLiveData<>();
        error = new MutableLiveData<>();
        progressState = new MutableLiveData<>();
        repo = new NewsAPIRepository();
    }

    public void getData(NewsApiArg arg) {
        progressState.setValue(true);
        repo.getNewsList(arg, this, new ResponseCallback<NewsList>() {
            @Override
            public void onSuccess(NewsList data) {
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

    public MutableLiveData<NewsList> getmData() {
        return mData;
    }

    public void setmData(MutableLiveData<NewsList> mData) {
        this.mData = mData;
    }

    public NewsApiArg getApiArg() {
        return apiArg;
    }

    public void setApiArg(NewsApiArg apiArg) {
        this.apiArg = apiArg;
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
