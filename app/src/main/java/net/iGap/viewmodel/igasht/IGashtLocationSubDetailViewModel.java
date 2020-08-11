package net.iGap.viewmodel.igasht;

import android.view.View;

import androidx.databinding.ObservableInt;

import net.iGap.R;
import net.iGap.model.igasht.ExtraDetail;
import net.iGap.repository.IGashtRepository;

public class IGashtLocationSubDetailViewModel extends BaseIGashtViewModel {
    private IGashtRepository repository;
    private ObservableInt noDetail = new ObservableInt(View.GONE);

    private ObservableInt haveParking = new ObservableInt(R.string.close_icon);
    private ObservableInt haveWiFi = new ObservableInt(R.string.close_icon);
    private ObservableInt haveRestaurant = new ObservableInt(R.string.close_icon);
    private ObservableInt haveCoffeeShop = new ObservableInt(R.string.close_icon);
    private ObservableInt haveWheelchairRamp = new ObservableInt(R.string.close_icon);
    private ObservableInt havePrayerRoom = new ObservableInt(R.string.close_icon);
    private ObservableInt haveWc = new ObservableInt(R.string.close_icon);

    public ObservableInt getHaveParking() {
        return haveParking;
    }

    public ObservableInt getHaveWiFi() {
        return haveWiFi;
    }

    public ObservableInt getHaveRestaurant() {
        return haveRestaurant;
    }

    public ObservableInt getHaveCoffeeShop() {
        return haveCoffeeShop;
    }

    public ObservableInt getHaveWheelchairRamp() {
        return haveWheelchairRamp;
    }

    public ObservableInt getHavePrayerRoom() {
        return havePrayerRoom;
    }

    public ObservableInt getHaveWc() {
        return haveWc;
    }

    public IGashtLocationSubDetailViewModel() {
        repository = IGashtRepository.getInstance();
        if (getLocationDetail() != null) {
            showMainView.set(View.VISIBLE);
            noDetail.set(View.GONE);
        } else {
            showMainView.set(View.GONE);
            noDetail.set(View.VISIBLE);
        }
        if (getLocationDetail() != null) {
            haveParking.set(getLocationDetail().getParking() ? R.string.check_icon : R.string.close_icon);
            haveWiFi.set(getLocationDetail().getmWifi() ? R.string.check_icon : R.string.close_icon);
            haveRestaurant.set(getLocationDetail().getResturant() ? R.string.check_icon : R.string.close_icon);
            haveCoffeeShop.set(getLocationDetail().getCoffeeShop() ? R.string.check_icon : R.string.close_icon);
            haveWheelchairRamp.set(getLocationDetail().getWeelchairRamp() ? R.string.check_icon : R.string.close_icon);
            havePrayerRoom.set(getLocationDetail().getPrayerRoom() ? R.string.check_icon : R.string.close_icon);
            haveWc.set(getLocationDetail().getWc() ? R.string.check_icon : R.string.close_icon);
        }
    }

    public ObservableInt getShowText() {
        return noDetail;
    }

    public ExtraDetail getLocationDetail() {
        return repository.getSelectedLocation().getmExtraDetail();
    }

    @Override
    public void onSuccess(Object data) {

    }
}
