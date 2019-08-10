package net.iGap.igasht;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.databinding.ObservableInt;
import android.view.View;

import net.iGap.api.errorhandler.ErrorModel;
import net.iGap.api.errorhandler.ResponseCallback;

// base view model implements callback for repository and handle on fail and base onError.
// in other view model extends this you should override onSuccess and if have custom onError override it
public abstract class BaseIGashtViewModel<T> extends ViewModel implements ResponseCallback<T> {

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
