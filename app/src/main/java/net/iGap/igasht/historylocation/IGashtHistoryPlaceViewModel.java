package net.iGap.igasht.historylocation;

import android.arch.lifecycle.MutableLiveData;
import android.view.View;

import net.iGap.igasht.BaseIGashtViewModel;
import net.iGap.igasht.IGashtRepository;

import java.util.List;

public class IGashtHistoryPlaceViewModel extends BaseIGashtViewModel<List<String>> {

    private MutableLiveData<List<String>> historyList= new MutableLiveData<>();
    private IGashtRepository repository;

    public IGashtHistoryPlaceViewModel() {
        repository = IGashtRepository.getInstance();
        getHistoryData();
    }

    public MutableLiveData<List<String>> getHistoryList() {
        return historyList;
    }

    @Override
    public void onSuccess(List<String> data) {
        showLoadingView.set(View.GONE);
        showMainView.set(View.VISIBLE);
        showViewRefresh.set(View.GONE);
        historyList.setValue(data);
    }

    private void getHistoryData() {
        showLoadingView.set(View.VISIBLE);
        showMainView.set(View.GONE);
        showViewRefresh.set(View.GONE);
        repository.getHistoryList(this);
    }

    public void onRetryClick() {
        getHistoryData();
    }

    public void onClickHistoryItem(int position) {

    }
}
