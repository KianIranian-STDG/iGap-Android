package net.iGap.igasht.locationlist;

import android.arch.lifecycle.MutableLiveData;
import android.view.View;

import net.iGap.igasht.BaseIGashtResponse;
import net.iGap.igasht.BaseIGashtViewModel;
import net.iGap.igasht.IGashtRepository;
import net.iGap.igasht.provinceselect.IGashtProvince;

import java.util.List;

public class IGashtLocationViewModel extends BaseIGashtViewModel<BaseIGashtResponse<IGashtLocationItem>> {

    private MutableLiveData<List<IGashtLocationItem>> locationList = new MutableLiveData<>();
    private MutableLiveData<IGashtLocationItem> goToLocationDetail = new MutableLiveData<>();
    private MutableLiveData<Boolean> addToFavorite = new MutableLiveData<>();

    private IGashtRepository repository;
    private IGashtProvince province;

    public MutableLiveData<List<IGashtLocationItem>> getLocationList() {
        return locationList;
    }

    public MutableLiveData<IGashtLocationItem> getGoToLocationDetail() {
        return goToLocationDetail;
    }

    public MutableLiveData<Boolean> getAddToFavorite() {
        return addToFavorite;
    }

    public void init(IGashtProvince province) {
        this.province = province;
        this.repository = IGashtRepository.getInstance();
        getLocationListOfProvince();
    }


    public void onRetryClick() {
        getLocationListOfProvince();
    }

    private void getLocationListOfProvince() {
        showLoadingView.set(View.VISIBLE);
        showMainView.set(View.GONE);
        showViewRefresh.set(View.GONE);
        repository.getLocationListWithProvince(province.getId(), this);
    }

    @Override
    public void onSuccess(BaseIGashtResponse<IGashtLocationItem> data) {
        showLoadingView.set(View.GONE);
        showMainView.set(View.VISIBLE);
        locationList.postValue(data.getData());
    }

    public void addToFavorite(int position) {
        addToFavorite.setValue(true);
    }

    public void buyTicket(int position) {
        if (locationList.getValue() != null) {
            goToLocationDetail.setValue(locationList.getValue().get(position));
        }
    }
}
