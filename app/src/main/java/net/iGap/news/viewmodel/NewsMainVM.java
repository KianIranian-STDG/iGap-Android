package net.iGap.news.viewmodel;

import androidx.lifecycle.MutableLiveData;

import net.iGap.R;
import net.iGap.api.apiService.BaseAPIViewModel;
import net.iGap.api.apiService.ResponseCallback;
import net.iGap.api.errorhandler.ErrorModel;
import net.iGap.news.repository.MainRepo;
import net.iGap.news.repository.model.NewsError;
import net.iGap.news.repository.model.NewsFirstPage;

import java.util.List;

public class NewsMainVM extends BaseAPIViewModel {

    private MutableLiveData<List<NewsFirstPage>> mainList;
    private MutableLiveData<NewsError> error;
    private MutableLiveData<Boolean> progressState;
    private MainRepo repo;

    public NewsMainVM() {
        mainList = new MutableLiveData<>();
        error = new MutableLiveData<>();
        progressState = new MutableLiveData<>();
        repo = new MainRepo();
    }

    public void getNews() {
        progressState.setValue(true);
        repo.getMainPage(this, new ResponseCallback<List<NewsFirstPage>>() {
            @Override
            public void onSuccess(List<NewsFirstPage> data) {
                mainList.setValue(data);
                progressState.setValue(false);
            }

            @Override
            public void onError(String e) {
                error.setValue(new NewsError(true, "KB", e, R.string.news_serverError));
                progressState.setValue(false);
            }

            @Override
            public void onFailed() {
                progressState.setValue(false);
            }
        });
    }

    public MutableLiveData<List<NewsFirstPage>> getMainList() {
        return mainList;
    }

    public void setMainList(MutableLiveData<List<NewsFirstPage>> mainList) {
        this.mainList = mainList;
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
