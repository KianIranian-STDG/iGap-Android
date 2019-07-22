package net.iGap.igasht.locationdetail.subdetail;

import android.arch.lifecycle.ViewModel;

import net.iGap.igasht.locationlist.LocationDetail;

public class IGashtLocationSubDetailViewModel extends ViewModel {

    private LocationDetail locationDetail;

    public LocationDetail getLocationDetail() {
        return locationDetail;
    }

    public void setLocationDetail(LocationDetail locationDetail) {
        this.locationDetail = locationDetail;
    }
}
