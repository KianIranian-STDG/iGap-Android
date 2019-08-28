package net.iGap.igasht.locationdetail.subdetail;

import android.databinding.ObservableInt;
import android.view.View;

import net.iGap.R;
import net.iGap.igasht.BaseIGashtViewModel;
import net.iGap.igasht.IGashtRepository;
import net.iGap.igasht.locationlist.ExtraDetail;

public class IGashtLocationSubDetailViewModel extends BaseIGashtViewModel {
    private ObservableInt nodetail = new ObservableInt(View.GONE);
    private ObservableInt haveParking = new ObservableInt(R.string.close_icon);
    private ObservableInt haveWc=new ObservableInt(R.string.close_icon);
    private ObservableInt haveCoffeeShop=new ObservableInt(R.string.close_icon);
    private ObservableInt havePrayerRoom=new ObservableInt(R.string.close_icon);
    private ObservableInt haveResturant=new ObservableInt(R.string.close_icon);
    private ObservableInt haveWeelchairRamp=new ObservableInt(R.string.close_icon);
    private ObservableInt haveWiFi=new ObservableInt(R.string.close_icon);
    private IGashtRepository repository;

    public IGashtLocationSubDetailViewModel() {
        repository = IGashtRepository.getInstance();
        if (getLocationDetail() != null) {
            showMainView.set(View.VISIBLE);
            nodetail.set(View.GONE);
        } else {
            showMainView.set(View.GONE);
            nodetail.set(View.VISIBLE);
        }
        if (getLocationDetail() != null) {
            haveParking.set(getLocationDetail().getParking() ? R.string.check_icon : R.string.close_icon);
            haveWc.set(getLocationDetail().getWc()?R.string.check_icon:R.string.close_icon);
        }

    }

    public ObservableInt getHaveParking() {
        return haveParking;
    }

    public ObservableInt getShowText() {
        return nodetail;
    }

    public ExtraDetail getLocationDetail() {
        return repository.getSelectedLocation().getmExtraDetail();
    }

    @Override
    public void onSuccess(Object data) {

    }

}
