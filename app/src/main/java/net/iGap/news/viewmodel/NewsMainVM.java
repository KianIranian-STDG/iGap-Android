package net.iGap.news.viewmodel;

import androidx.lifecycle.MutableLiveData;

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
        repo.getMainPage(this, new ResponseCallback<List<NewsFirstPage>>() {
            @Override
            public void onSuccess(List<NewsFirstPage> data) {
                mainList.setValue(data);
            }

            @Override
            public void onError(ErrorModel error) {

            }

            @Override
            public void setProgressIndicator(boolean visibility) {
                progressState.setValue(visibility);
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
