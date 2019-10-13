package net.iGap.news.viewmodel;

import androidx.lifecycle.MutableLiveData;

import net.iGap.R;
import net.iGap.api.apiService.BaseAPIViewModel;
import net.iGap.api.apiService.ResponseCallback;
import net.iGap.api.errorhandler.ErrorModel;
import net.iGap.news.repository.MainRepo;
import net.iGap.news.repository.model.NewsApiArg;
import net.iGap.news.repository.model.NewsError;
import net.iGap.news.repository.model.NewsList;

import java.util.ArrayList;

public class NewsListVM extends BaseAPIViewModel {

    private MutableLiveData<NewsList> mData;
    private MutableLiveData<NewsError> error;
    private MutableLiveData<Boolean> progressState;
    private MainRepo repo;
    private NewsApiArg apiArg;

    public NewsListVM() {
        mData = new MutableLiveData<>();
        error = new MutableLiveData<>();
        progressState = new MutableLiveData<>();
        repo = new MainRepo();
    }

    public void getData(NewsApiArg arg) {
        repo.getNewsList(arg, this, new ResponseCallback<NewsList>() {
            @Override
            public void onSuccess(NewsList data) {
                mData.setValue(data);
            }

            @Override
            public void onError(ErrorModel errorM) {
                error.setValue(new NewsError(true, "", "", R.string.news_serverError));
            }

            @Override
            public void setProgressIndicator(boolean visibility) {
                progressState.setValue(visibility);
            }
        });
    }

    private NewsList addFakeData() {
        NewsList temp = new NewsList();
        temp.setNews(new ArrayList<>());
        temp.getNews().addAll(temp.getFake());
        return temp;
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
