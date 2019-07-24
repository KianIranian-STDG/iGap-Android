package net.iGap.igasht.locationlist;

import android.arch.lifecycle.MutableLiveData;
import android.view.View;

import net.iGap.igasht.BaseIGashtResponse;
import net.iGap.igasht.BaseIGashtViewModel;
import net.iGap.igasht.IGashtRepository;

import java.util.List;

public class IGashtLocationViewModel extends BaseIGashtViewModel<BaseIGashtResponse<IGashtLocationItem>> {

    private MutableLiveData<List<IGashtLocationItem>> locationList = new MutableLiveData<>();
    private MutableLiveData<Boolean> goToLocationDetail = new MutableLiveData<>();
    private MutableLiveData<Boolean> addToFavorite = new MutableLiveData<>();

    private IGashtRepository repository;

    public MutableLiveData<List<IGashtLocationItem>> getLocationList() {
        return locationList;
    }

    public MutableLiveData<Boolean> getGoToLocationDetail() {
        return goToLocationDetail;
    }

    public MutableLiveData<Boolean> getAddToFavorite() {
        return addToFavorite;
    }

    public IGashtLocationViewModel() {
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
        repository.getLocationListWithProvince(this);
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
            repository.setSelectedLocation(locationList.getValue().get(position));
            goToLocationDetail.setValue(true);
        }else{
            goToLocationDetail.setValue(false);
        }
    }
}
