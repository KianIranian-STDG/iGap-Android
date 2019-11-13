package net.iGap.news.viewmodel;

import androidx.lifecycle.MutableLiveData;

import net.iGap.R;
import net.iGap.api.apiService.BaseAPIViewModel;
import net.iGap.api.apiService.ResponseCallback;
import net.iGap.api.errorhandler.ErrorModel;
import net.iGap.news.repository.MainRepo;
import net.iGap.news.repository.model.NewsError;
import net.iGap.news.repository.model.NewsGroup;

import java.util.ArrayList;

public class NewsGroupListVM extends BaseAPIViewModel {

    private MutableLiveData<NewsGroup> mGroups;
    private MutableLiveData<NewsError> error;
    private MutableLiveData<Boolean> progressState;
    private MainRepo repo;

    public NewsGroupListVM() {
        mGroups = new MutableLiveData<>();
        error = new MutableLiveData<>();
        progressState = new MutableLiveData<>();
        repo = new MainRepo();
    }

    public void getData() {
        repo.getNewsGroups(this, new ResponseCallback<NewsGroup>() {
            @Override
            public void onSuccess(NewsGroup data) {
                mGroups.setValue(data);
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
