package net.iGap.igasht.locationdetail.buyticket;

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
    private SingleLiveEvent<List<String>> showDialogSelectTicketType = new SingleLiveEvent<>();
    private SingleLiveEvent<Boolean> showDialogEnterCount = new SingleLiveEvent<>();
    private int locationId;
    private List<IGashtLocationService> serviceList;
    private List<IGashtVouchers> selectedServiceList;
    private int selectedServicePosition = -1;
    private IGashtServiceAmount selectedAmount;
    private IGashtRepository repository;

    public IGashtBuyTicketViewModel() {
        repository = IGashtRepository.getInstance();
        selectedServiceList = new ArrayList<>();
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

    public SingleLiveEvent<List<String>> getShowDialogSelectTicketType() {
        return showDialogSelectTicketType;
    }

    public SingleLiveEvent<Boolean> getShowDialogEnterCount() {
        return showDialogEnterCount;
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
        setDataForShowDialogSelectTicketType();
    }

    public void selectedService() {
        if (selectedServicePosition != -1) {
            setDataForShowDialogSelectTicketType();
        }
    }

    private void setDataForShowDialogSelectTicketType() {
        List<String> tmp = new ArrayList<>();
        for (int i = 0; i < serviceList.get(selectedServicePosition).getAmounts().size(); i++) {
            tmp.add(serviceList.get(selectedServicePosition).getAmounts().get(i).getVoucherType());
        }
        showDialogSelectTicketType.setValue(tmp);
    }

    public void selectedTicketType(int position) {
        Log.wtf(this.getClass().getName(), "selected service: " + serviceList.get(selectedServicePosition).getAmounts().get(position).getVoucherType());
        selectedAmount = serviceList.get(selectedServicePosition).getAmounts().get(position);
        showDialogEnterCount.setValue(true);
    }

    public void onPayClick() {

    }
}
