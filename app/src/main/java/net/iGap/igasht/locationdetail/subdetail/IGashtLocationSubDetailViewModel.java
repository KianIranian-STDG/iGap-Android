package net.iGap.igasht.locationdetail.subdetail;

import android.arch.lifecycle.ViewModel;

import net.iGap.igasht.IGashtRepository;
import net.iGap.igasht.locationlist.ExtraDetail;
import net.iGap.igasht.locationlist.LocationDetail;

public class IGashtLocationSubDetailViewModel extends ViewModel {

    private IGashtRepository repository;

    public IGashtLocationSubDetailViewModel(){
        repository = IGashtRepository.getInstance();
    }

    public ExtraDetail getLocationDetail() {
        return repository.getSelectedLocation().getmExtraDetail();
    }
}
