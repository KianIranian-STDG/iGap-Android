package net.iGap.igasht;

import android.view.View;

import androidx.databinding.ObservableInt;
import androidx.lifecycle.MutableLiveData;

import net.iGap.api.apiService.BaseAPIViewModel;
import net.iGap.api.apiService.ResponseCallback;
import net.iGap.api.errorhandler.ErrorModel;

// base view model implements callback for repository and handle on fail and base onError.
// in other view model extends this you should override onSuccess and if have custom onError override it
public abstract class BaseIGashtViewModel<T> extends BaseAPIViewModel implements ResponseCallback<T> {

    protected ObservableInt showLoadingView = new ObservableInt(View.VISIBLE);
    protected ObservableInt showMainView = new ObservableInt(View.INVISIBLE);
    protected ObservableInt showViewRefresh = new ObservableInt(View.GONE);
    private MutableLiveData<String> requestErrorMessage = new MutableLiveData<>();
    private MutableLiveData<Boolean> updateGooglePlay = new MutableLiveData<>();

    public ObservableInt getShowLoadingView() {
        return showLoadingView;
    }

    public ObservableInt getShowMainView() {
        return showMainView;
    }

    public ObservableInt getShowViewRefresh() {
        return showViewRefresh;
    }

    public MutableLiveData<String> getRequestErrorMessage() {
        return requestErrorMessage;
    }

    public MutableLiveData<Boolean> getUpdateGooglePlay() {
        return updateGooglePlay;
    }

    @Override
    public void onError(ErrorModel error) {
        showLoadingView.set(View.GONE);
        showViewRefresh.set(View.VISIBLE);
        requestErrorMessage.setValue(error.getMessage());
    }

    @Override
    public void onFailed(boolean isNeedUpdate) {
        if (isNeedUpdate) {
            updateGooglePlay.setValue(true);
        } else {
            showViewRefresh.set(View.VISIBLE);
            showLoadingView.set(View.GONE);
        }
    }
}
