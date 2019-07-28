package net.iGap.igasht;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.databinding.ObservableInt;
import android.view.View;

// base view model implements callback for repository and handle on fail and base onError.
// in other view model extends this you should override onSuccess and if have custom onError override it
public abstract class BaseIGashtViewModel<T> extends ViewModel implements IGashtRepository.ResponseCallback<T>{

    protected ObservableInt showLoadingView = new ObservableInt(View.VISIBLE);
    protected ObservableInt showMainView = new ObservableInt(View.INVISIBLE);
    protected ObservableInt showViewRefresh = new ObservableInt(View.GONE);
    protected MutableLiveData<String> requestErrorMessage = new MutableLiveData<>();

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
    public void onError(ErrorModel error) {
        showLoadingView.set(View.GONE);
        showViewRefresh.set(View.VISIBLE);
        requestErrorMessage.setValue(error.getMessage());
    }

    @Override
    public void onFailed() {
        showViewRefresh.set(View.VISIBLE);
        showLoadingView.set(View.GONE);
    }
}
