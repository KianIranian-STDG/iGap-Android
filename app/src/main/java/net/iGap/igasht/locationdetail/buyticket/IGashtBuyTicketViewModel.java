package net.iGap.igasht.locationdetail.buyticket;

import android.arch.lifecycle.MutableLiveData;
import android.databinding.ObservableInt;
import android.util.Log;
import android.view.View;

import net.iGap.igasht.BaseIGashtResponse;
import net.iGap.igasht.BaseIGashtViewModel;
import net.iGap.igasht.IGashtRepository;
import net.iGap.module.SingleLiveEvent;

import java.util.ArrayList;
import java.util.List;

public class IGashtBuyTicketViewModel extends BaseIGashtViewModel<BaseIGashtResponse<IGashtLocationService>> {

    private ObservableInt totalPrice = new ObservableInt(0);
    private SingleLiveEvent<Boolean> registerVoucher = new SingleLiveEvent<>();
    private MutableLiveData<List<IGashtLocationService>> serviceList = new MutableLiveData<>();

    private IGashtRepository repository;
    private List<IGashtServiceAmount> orderedTickets;
    private int selectedServicePosition = -1;
    private IGashtServiceAmount selectedAmount;

    public IGashtBuyTicketViewModel() {
        repository = IGashtRepository.getInstance();
        orderedTickets = new ArrayList<>();
        getTicketData();
    }

    public ObservableInt getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(int totalPrice) {
        this.totalPrice.set(totalPrice);
    }

    public SingleLiveEvent<Boolean> getRegisterVoucher() {
        return registerVoucher;
    }

    public MutableLiveData<List<IGashtLocationService>> getServiceList() {
        return serviceList;
    }

    @Override
    public void onSuccess(BaseIGashtResponse<IGashtLocationService> data) {
        serviceList.setValue(data.getData());
        showLoadingView.set(View.GONE);
        showMainView.set(View.VISIBLE);
        showViewRefresh.set(View.GONE);
    }

    public void onAddPlaceClick() {
        /*List<String> tmp = new ArrayList<>();
        for (int i = 0; i < serviceList.size(); i++) {
            tmp.add(serviceList.get(i).getSeviceNameWithLanguage());
        }
        showDialogSelectService.setValue(tmp);*/
    }

    public void onRetryClick() {
        getTicketData();
    }

    public void selectedService(int position) {
        /*Log.wtf(this.getClass().getName(), "selected service: " + serviceList.get(position).getSeviceNameWithLanguage());
        selectedServicePosition = position;
        showDialogSelectTicketType.setValue(serviceList.get(selectedServicePosition).getAmounts());*/
    }

    public void selectedService() {
        /*if (selectedServicePosition != -1) {
            showDialogSelectTicketType.setValue(serviceList.get(selectedServicePosition).getAmounts());
        }*/
    }

    public void selectedTicketType(IGashtServiceAmount selectedAmount) {
       /* Log.wtf(this.getClass().getName(), "selected service: " + selectedAmount.getVoucherType());
        this.selectedAmount = selectedAmount;
        showDialogEnterCount.setValue(true);*/
    }

    public void setTicketCount(int ticketCount) {
        /*selectedAmount.setCount(ticketCount);
        selectedAmount.setTitle(serviceList.get(selectedServicePosition).getSeviceNameWithLanguage());
        totalPrice.set(totalPrice.get() + (selectedAmount.getAmount() * ticketCount));
        orderedTickets.add(selectedAmount);
        addToTicketList.setValue(selectedAmount);
        repository.addToVoucherList(selectedAmount);
        selectedServicePosition = -1;
        selectedAmount = null;*/
    }

    public void removeOrderedTicket(int position) {
        /*IGashtServiceAmount tmp = orderedTickets.remove(position);
        repository.removeFromVoucherList(tmp);
        totalPrice.set(totalPrice.get() - (tmp.getAmount() * tmp.getCount()));*/
    }

    public void onPayClick() {
        if (serviceList.getValue() != null) {
            repository.createVoucherList(serviceList.getValue());
            registerVoucher.setValue(repository.hasVoucher());
        }
    }

    private void getTicketData() {
        showLoadingView.set(View.VISIBLE);
        showMainView.set(View.GONE);
        showViewRefresh.set(View.GONE);
        repository.getServiceList(this);
    }
}
