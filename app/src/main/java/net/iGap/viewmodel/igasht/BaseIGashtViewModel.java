package net.iGap.viewmodel.igasht;

import android.view.View;

import androidx.databinding.ObservableInt;
import androidx.lifecycle.MutableLiveData;

import net.iGap.api.apiService.BaseAPIViewModel;
import net.iGap.observers.interfaces.ResponseCallback;

// base view model implements callback for repository and handle on fail and base onError.
// in other view model extends this you should override onSuccess and if have custom onError override it
public abstract class BaseIGashtViewModel<T> extends BaseAPIViewModel implements ResponseCallback<T> {

    protected ObservableInt showLoadingView = new ObservableInt(View.VISIBLE);
    protected ObservableInt showMainView = new ObservableInt(View.INVISIBLE);
    protected ObservableInt showViewRefresh = new ObservableInt(View.GONE);
    private MutableLiveData<String> requestErrorMessage = new MutableLiveData<>();

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

    @Override
    public void onError(String error) {
        showLoadingView.set(View.GONE);
        showViewRefresh.set(View.VISIBLE);
        requestErrorMessage.setValue(error);
    }

    @Override
    public void onFailed() {
        showViewRefresh.set(View.VISIBLE);
        showLoadingView.set(View.GONE);
    }
}
