package net.iGap.igasht.historylocation;

import android.arch.lifecycle.MutableLiveData;
import android.view.View;

import net.iGap.igasht.BaseIGashtViewModel;
import net.iGap.igasht.IGashtRepository;

import java.util.List;

public class IGashtHistoryPlaceViewModel extends BaseIGashtViewModel<TicketHistoryListResponse<IGashtTicketDetail>> {

    private MutableLiveData<List<IGashtTicketDetail>> historyList = new MutableLiveData<>();
    private MutableLiveData<String> goToTicketDetail = new MutableLiveData<>();

    private TicketHistoryListResponse<IGashtTicketDetail> response;
    private String voucherId = null;
    private IGashtRepository repository;

    public IGashtHistoryPlaceViewModel(String voucherNumber, String voucherId) {
        repository = IGashtRepository.getInstance();
        response = new TicketHistoryListResponse<>();
        getHistoryData();
        if (voucherNumber != null) {
            goToTicketDetail.setValue(voucherNumber);
        }
        this.voucherId = voucherId;
    }

    public MutableLiveData<List<IGashtTicketDetail>> getHistoryList() {
        return historyList;
    }

    @Override
    public void onSuccess(TicketHistoryListResponse<IGashtTicketDetail> data) {
        showLoadingView.set(View.GONE);
        showMainView.set(View.VISIBLE);
        showViewRefresh.set(View.GONE);
        response.setLimit(data.getLimit());
        response.setOffset(data.getOffset());
        response.setTotal(data.getTotal());
        response.getData().addAll(data.getData());
        historyList.setValue(response.getData());
        if (voucherId != null){

        }
    }

    private void getHistoryData() {
        showLoadingView.set(View.VISIBLE);
        showMainView.set(View.GONE);
        showViewRefresh.set(View.GONE);
        if (response == null) {
            repository.getHistoryList(0, 10, this);
        } else {
            repository.getHistoryList(response.getOffset(), response.getLimit(), this);
        }
    }

    public void onRetryClick() {
        getHistoryData();
    }

    public void onClickHistoryItem(int position) {

    }
}
