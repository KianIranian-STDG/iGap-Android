package net.iGap.igasht.historylocation;

import android.arch.lifecycle.MutableLiveData;
import android.databinding.ObservableInt;
import android.util.Log;
import android.view.View;

import net.iGap.igasht.BaseIGashtViewModel;
import net.iGap.igasht.IGashtRepository;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class IGashtHistoryPlaceViewModel extends BaseIGashtViewModel<TicketHistoryListResponse<IGashtTicketDetail>> {

    private MutableLiveData<List<IGashtTicketDetail>> historyList = new MutableLiveData<>();
    private MutableLiveData<String> goToTicketDetail = new MutableLiveData<>();
    private ObservableInt showEmptyListMessage = new ObservableInt(View.GONE);

    private TicketHistoryListResponse<IGashtTicketDetail> response;
    private IGashtRepository repository;

    public IGashtHistoryPlaceViewModel() {
        repository = IGashtRepository.getInstance();
        response = new TicketHistoryListResponse<>();
        getHistoryData();
    }

    public MutableLiveData<List<IGashtTicketDetail>> getHistoryList() {
        return historyList;
    }

    public MutableLiveData<String> getGoToTicketDetail() {
        return goToTicketDetail;
    }

    public ObservableInt getShowEmptyListMessage() {
        return showEmptyListMessage;
    }

    @Override
    public void onSuccess(@NotNull TicketHistoryListResponse<IGashtTicketDetail> data) {
        showLoadingView.set(View.GONE);
        showMainView.set(View.VISIBLE);
        showViewRefresh.set(View.GONE);
        response.setLimit(data.getLimit());
        response.setOffset(data.getOffset());
        response.setTotal(data.getTotal());
        response.getData().addAll(data.getData());
        historyList.setValue(response.getData());
        if (response.getData().size() > 0) {
            showEmptyListMessage.set(View.GONE);
        } else {
            showEmptyListMessage.set(View.VISIBLE);
        }
    }

    private void getHistoryData() {
        showLoadingView.set(View.VISIBLE);
        showMainView.set(View.GONE);
        showViewRefresh.set(View.GONE);
        if (response.getOffset() == 0) {
            repository.getHistoryList(0, 10, this);
        } else {
            repository.getHistoryList(response.getOffset(), response.getLimit(), this);
        }
    }

    public void onRetryClick() {
        getHistoryData();
    }

    public void onClickHistoryItem(int position) {
        goToTicketDetail.setValue(response.getData().get(position).getVoucherNumber());
    }
}
