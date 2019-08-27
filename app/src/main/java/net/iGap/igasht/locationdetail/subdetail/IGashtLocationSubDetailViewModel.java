package net.iGap.igasht.locationdetail.subdetail;

import android.databinding.ObservableBoolean;
import android.databinding.ObservableInt;
import android.view.View;

import net.iGap.R;
import net.iGap.igasht.BaseIGashtViewModel;
import net.iGap.igasht.IGashtRepository;
import net.iGap.igasht.locationlist.ExtraDetail;

public class IGashtLocationSubDetailViewModel extends BaseIGashtViewModel {
    private ObservableInt showText = new ObservableInt(View.GONE);
    private ObservableInt haveParking = new ObservableInt(R.string.close);
    private IGashtRepository repository;

    public IGashtLocationSubDetailViewModel() {
        repository = IGashtRepository.getInstance();
        if (getLocationDetail() != null) {
            showMainView.set(View.VISIBLE);
            showText.set(View.GONE);
        } else {
            showMainView.set(View.GONE);
            showText.set(View.VISIBLE);
        }
        if(getLocationDetail()!=null){
        haveParking.set(getLocationDetail().getParking() ? R.string.check_icon : R.string.close);}

    }

    public ObservableInt getHaveParking() {
        return haveParking;
    }

    public ObservableInt getShowText() {
        return showText;
    }

    public ExtraDetail getLocationDetail() {
        return repository.getSelectedLocation().getmExtraDetail();
    }

    @Override
    public void onSuccess(Object data) {

    }

}
