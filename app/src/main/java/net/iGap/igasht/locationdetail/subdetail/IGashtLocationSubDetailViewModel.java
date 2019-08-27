package net.iGap.igasht.locationdetail.subdetail;

import android.databinding.ObservableInt;
import android.view.View;

import net.iGap.igasht.BaseIGashtViewModel;
import net.iGap.igasht.IGashtRepository;
import net.iGap.igasht.locationlist.ExtraDetail;

public class IGashtLocationSubDetailViewModel extends BaseIGashtViewModel {
    private ObservableInt showText= new ObservableInt(View.GONE);
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
