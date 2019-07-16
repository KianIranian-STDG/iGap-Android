package net.iGap.igasht;

import android.arch.lifecycle.ViewModel;
import android.databinding.ObservableInt;
import android.view.View;

public class BaseIGashtViewModel extends ViewModel {

    protected ObservableInt showLoadingView = new ObservableInt(View.VISIBLE);
    protected ObservableInt showMainView = new ObservableInt(View.GONE);
    protected ObservableInt showViewRefresh = new ObservableInt(View.GONE);

    public ObservableInt getShowLoadingView() {
        return showLoadingView;
    }

    public ObservableInt getShowMainView() {
        return showMainView;
    }

    public ObservableInt getShowViewRefresh() {
        return showViewRefresh;
    }


}
