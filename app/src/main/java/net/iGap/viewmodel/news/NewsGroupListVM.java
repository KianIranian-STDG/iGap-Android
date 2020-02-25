package net.iGap.viewmodel.news;

import androidx.lifecycle.MutableLiveData;

import net.iGap.R;
import net.iGap.api.apiService.BaseAPIViewModel;
import net.iGap.observers.interfaces.ResponseCallback;
import net.iGap.repository.news.NewsAPIRepository;
import net.iGap.model.news.NewsError;
import net.iGap.model.news.NewsGroup;

public class NewsGroupListVM extends BaseAPIViewModel {

    private MutableLiveData<NewsGroup> mGroups;
    private MutableLiveData<NewsError> error;
    private MutableLiveData<Boolean> progressState;
    private NewsAPIRepository repo;

    public NewsGroupListVM() {
        mGroups = new MutableLiveData<>();
        error = new MutableLiveData<>();
        progressState = new MutableLiveData<>();
        repo = new NewsAPIRepository();
    }

    public void getData() {
        progressState.setValue(true);
        repo.getNewsGroup(this, new ResponseCallback<NewsGroup>() {
            @Override
            public void onSuccess(NewsGroup data) {
                progressState.setValue(false);
                mGroups.setValue(data);
            }

            @Override
            public void onError(String e) {
                progressState.setValue(false);
                error.setValue(new NewsError(true, "", "", R.string.news_serverError));
            }

            @Override
            public void onFailed() {
                progressState.setValue(false);
            }
        });
    }

    public MutableLiveData<NewsGroup> getmGroups() {
        return mGroups;
    }

    public void setmGroups(MutableLiveData<NewsGroup> mGroups) {
        this.mGroups = mGroups;
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
