package net.iGap.igasht.locationdetail;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.databinding.ObservableBoolean;

import net.iGap.igasht.locationlist.IGashtLocationItem;
import net.iGap.igasht.locationlist.LocationDetail;

public class IGashtLocationDetailViewModel extends ViewModel {

    private ObservableBoolean showBuyTicketView = new ObservableBoolean(true);

    private MutableLiveData<Integer> loadBuyTicketView = new MutableLiveData<>();
    private MutableLiveData<LocationDetail> loadDetailSubView = new MutableLiveData<>();

    private IGashtLocationItem locationItem;

    public IGashtLocationItem getLocationItem() {
        return locationItem;
    }

    public void setLocationItem(IGashtLocationItem locationItem) {
        this.locationItem = locationItem;
        loadBuyTicketView.setValue(locationItem.getId());
    }

    public MutableLiveData<Integer> getLoadBuyTicketView() {
        return loadBuyTicketView;
    }

    public MutableLiveData<LocationDetail> getLoadDetailSubView() {
        return loadDetailSubView;
    }

    public ObservableBoolean getShowBuyTicketView() {
        return showBuyTicketView;
    }

    public void onTabItemClick(boolean loadBuyTicketView) {
        showBuyTicketView.set(loadBuyTicketView);
        if (loadBuyTicketView) {
            this.loadBuyTicketView.setValue(locationItem.getId());
        } else {
            loadDetailSubView.setValue(locationItem.getDetail());
        }
    }
}
