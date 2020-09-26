package net.iGap.viewmodel.igasht;

import android.view.View;

import androidx.databinding.ObservableBoolean;
import androidx.lifecycle.MutableLiveData;

import net.iGap.model.igasht.IGashtLocationItem;
import net.iGap.model.igasht.purchaseResponse;
import net.iGap.repository.IGashtRepository;

public class IGashtLocationDetailViewModel extends BaseIGashtViewModel<purchaseResponse> {

    private ObservableBoolean showBuyTicketView = new ObservableBoolean(true);
    private MutableLiveData<Boolean> loadBuyTicketView = new MutableLiveData<>();
    private MutableLiveData<Boolean> goHistoryPage = new MutableLiveData<>();
    private MutableLiveData<String> goPayment = new MutableLiveData<>();
    private MutableLiveData<Boolean> paymentError = new MutableLiveData<>();
    private IGashtRepository repository;

    public IGashtLocationDetailViewModel() {
        repository = IGashtRepository.getInstance();
        loadBuyTicketView.setValue(true);
        showLoadingView.set(View.GONE);
        showMainView.set(View.VISIBLE);
        showViewRefresh.set(View.GONE);
    }

    public IGashtLocationItem getLocationItem() {
        return repository.getSelectedLocation();
    }

    public MutableLiveData<Boolean> getLoadBuyTicketView() {
        return loadBuyTicketView;
    }

    public ObservableBoolean getShowBuyTicketView() {
        return showBuyTicketView;
    }

    public MutableLiveData<Boolean> getGoHistoryPage() {
        return goHistoryPage;
    }

    public MutableLiveData<String> getGoPayment() {
        return goPayment;
    }

    public MutableLiveData<Boolean> getPaymentError() {
        return paymentError;
    }

    public void onTabItemClick(boolean loadBuyTicketView) {
        showBuyTicketView.set(loadBuyTicketView);
        this.loadBuyTicketView.setValue(loadBuyTicketView);
    }

    public void registerOrder() {
        showLoadingView.set(View.VISIBLE);
        repository.getRegisteredOrder(this, this);
    }

    @Override
    public void onSuccess(purchaseResponse data) {
        showLoadingView.set(View.GONE);
        goPayment.setValue(data.getmToken());
        //    goHistoryPage.setValue(true);
    }

    @Override
    public void onFailed() {
        super.onFailed();
        showLoadingView.set(View.GONE);
        paymentError.setValue(true);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        repository.clearSelectedServiceList();
    }
}
