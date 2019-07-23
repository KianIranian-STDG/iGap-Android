package net.iGap.igasht.locationdetail.buyticket;

import android.arch.lifecycle.MutableLiveData;
import android.databinding.ObservableField;
import android.util.Log;
import android.view.View;

import net.iGap.igasht.BaseIGashtResponse;
import net.iGap.igasht.BaseIGashtViewModel;
import net.iGap.igasht.IGashtRepository;
import net.iGap.module.SingleLiveEvent;

import java.util.ArrayList;
import java.util.List;

public class IGashtBuyTicketViewModel extends BaseIGashtViewModel<BaseIGashtResponse<IGashtLocationService>> {

    private ObservableField<String> totalPrice = new ObservableField<>("0");
    private SingleLiveEvent<List<String>> showDialogSelectService = new SingleLiveEvent<>();
    private SingleLiveEvent<List<IGashtServiceAmount>> showDialogSelectTicketType = new SingleLiveEvent<>();
    private SingleLiveEvent<Boolean> showDialogEnterCount = new SingleLiveEvent<>();
    private MutableLiveData<IGashtServiceAmount> addToTicketList = new MutableLiveData<>();
    private int locationId;
    private int totalPriceValue;
    private List<IGashtLocationService> serviceList;
    private List<IGashtVouchers> selectedServiceList;
    private List<IGashtServiceAmount> orderedTickets;
    private int selectedServicePosition = -1;
    private IGashtServiceAmount selectedAmount;
    private IGashtRepository repository;

    public IGashtBuyTicketViewModel() {
        repository = IGashtRepository.getInstance();
        selectedServiceList = new ArrayList<>();
        orderedTickets = new ArrayList<>();
    }

    public void setLocationId(int locationId) {
        this.locationId = locationId;
        showLoadingView.set(View.VISIBLE);
        showMainView.set(View.GONE);
        showViewRefresh.set(View.GONE);
        repository.getServiceList(locationId, this);
    }

    public ObservableField<String> getTotalPrice() {
        return totalPrice;
    }

    public SingleLiveEvent<List<String>> getShowDialogSelectService() {
        return showDialogSelectService;
    }

    public SingleLiveEvent<List<IGashtServiceAmount>> getShowDialogSelectTicketType() {
        return showDialogSelectTicketType;
    }

    public SingleLiveEvent<Boolean> getShowDialogEnterCount() {
        return showDialogEnterCount;
    }

    public MutableLiveData<IGashtServiceAmount> getAddToTicketList() {
        return addToTicketList;
    }

    @Override
    public void onSuccess(BaseIGashtResponse<IGashtLocationService> data) {
        serviceList = data.getData();
        showLoadingView.set(View.GONE);
        showMainView.set(View.VISIBLE);
        showViewRefresh.set(View.GONE);
    }

    public void onAddPlaceClick() {
        List<String> tmp = new ArrayList<>();
        for (int i = 0; i < serviceList.size(); i++) {
            tmp.add(serviceList.get(i).getSeviceNameWithLanguage());
        }
        showDialogSelectService.setValue(tmp);
    }

    public void selectedService(int position) {
        Log.wtf(this.getClass().getName(), "selected service: " + serviceList.get(position).getSeviceNameWithLanguage());
        selectedServicePosition = position;
        showDialogSelectTicketType.setValue(serviceList.get(selectedServicePosition).getAmounts());
    }

    public void selectedService() {
        if (selectedServicePosition != -1) {
            showDialogSelectTicketType.setValue(serviceList.get(selectedServicePosition).getAmounts());
        }
    }

    public void selectedTicketType(IGashtServiceAmount selectedAmount) {
        Log.wtf(this.getClass().getName(), "selected service: " + selectedAmount.getVoucherType());
        this.selectedAmount = selectedAmount;
        showDialogEnterCount.setValue(true);
    }

    public void setTicketCount(int ticketCount) {
        selectedAmount.setCount(ticketCount);
        selectedAmount.setTitle(serviceList.get(selectedServicePosition).getSeviceNameWithLanguage());
        totalPriceValue = +totalPriceValue + (selectedAmount.getAmount() * ticketCount);
        totalPrice.set(String.valueOf(totalPriceValue));
        orderedTickets.add(selectedAmount);
        addToTicketList.setValue(selectedAmount);
        selectedServicePosition = 0;
        selectedAmount = null;
    }

    public void removeOrderedTicket(int position) {
        IGashtServiceAmount tmp = orderedTickets.remove(position);
        totalPriceValue = totalPriceValue - (tmp.getAmount() * tmp.getCount());
        totalPrice.set(String.valueOf(totalPriceValue));
    }

    public void onPayClick() {

    }
}
