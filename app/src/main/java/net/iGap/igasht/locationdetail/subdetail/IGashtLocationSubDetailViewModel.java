package net.iGap.igasht.locationdetail.subdetail;

import android.util.Log;
import android.view.View;

import net.iGap.igasht.BaseIGashtViewModel;
import net.iGap.igasht.IGashtRepository;
import net.iGap.igasht.locationlist.ExtraDetail;

public class IGashtLocationSubDetailViewModel extends BaseIGashtViewModel {

    private IGashtRepository repository;

    public IGashtLocationSubDetailViewModel() {
        repository = IGashtRepository.getInstance();
        if (getLocationDetail() != null) {
            Log.wtf(this.getClass().getName(),"not null");
            showMainView.set(View.VISIBLE);
        } else {
            Log.wtf(this.getClass().getName(),"is null");
            showMainView.set(View.GONE);
        }
    }

    public ExtraDetail getLocationDetail() {
        return repository.getSelectedLocation().getmExtraDetail();
    }

    @Override
    public void onSuccess(Object data) {

    }
}
