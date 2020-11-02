package net.iGap.viewmodel.igasht;

import android.view.View;

import androidx.databinding.ObservableInt;
import androidx.lifecycle.MutableLiveData;

import net.iGap.model.igasht.IGashtTicketDetail;
import net.iGap.model.igasht.TicketHistoryListResponse;
import net.iGap.repository.IGashtRepository;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class IGashtHistoryPlaceViewModel extends BaseIGashtViewModel<TicketHistoryListResponse<IGashtTicketDetail>> {

    private MutableLiveData<List<IGashtTicketDetail>> historyList = new MutableLiveData<>();
    private MutableLiveData<String> goToTicketDetail = new MutableLiveData<>();
    private ObservableInt showEmptyListMessage = new ObservableInt(View.GONE);

    private TicketHistoryListResponse<IGashtTicketDetail> response;
    private IGashtRepository repository;
    private boolean isLoadMoreItem = false;

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
        repository.getHistoryList(0, 10, this, this);
    }

    public void onRetryClick() {
        getHistoryData();
    }

    public void loadMoreItems(int totalCount, int lastVisibleItem) {
        if (!isLoadMoreItem && totalCount != 0) {
            if (response.getTotal() < (response.getOffset() * response.getLimit())) {
                showLoadingView.set(View.GONE);
                isLoadMoreItem = false;
                return;
            } else {
                isLoadMoreItem = true;
                showLoadingView.set(View.VISIBLE);
                repository.getHistoryList(response.getOffset() + 1, response.getLimit(), this, this);
            }

        }
    }

    public void onClickHistoryItem(int position) {
        goToTicketDetail.setValue(response.getData().get(position).getTicketInfo().getVoucherNumber());
    }
}
