package net.iGap.viewmodel.igasht;

import android.view.View;

import androidx.databinding.ObservableInt;
import androidx.lifecycle.MutableLiveData;

import net.iGap.G;
import net.iGap.R;
import net.iGap.model.igasht.BaseIGashtResponse;
import net.iGap.repository.IGashtRepository;
import net.iGap.model.igasht.IGashtProvince;
import net.iGap.model.igasht.IGashtLocationItem;

import java.util.List;

public class IGashtLocationViewModel extends BaseIGashtViewModel<BaseIGashtResponse<IGashtLocationItem>> {

    private MutableLiveData<List<IGashtLocationItem>> locationList = new MutableLiveData<>();
    private MutableLiveData<Boolean> goToLocationDetail = new MutableLiveData<>();
    private MutableLiveData<List<IGashtProvince>> provinceList = new MutableLiveData<>();
    //    private MutableLiveData<Boolean> addToFavorite = new MutableLiveData<>();
    private ObservableInt selectIcon = new ObservableInt(R.string.down_arrow_icon);
    private MutableLiveData<Boolean> clearEditText = new MutableLiveData<>();

    private IGashtRepository repository;

    public MutableLiveData<List<IGashtLocationItem>> getLocationList() {
        return locationList;
    }

    public MutableLiveData<List<IGashtProvince>> getProvinceList() {
        return provinceList;
    }

    public MutableLiveData<Boolean> getGoToLocationDetail() {
        return goToLocationDetail;
    }

//    public MutableLiveData<Boolean> getAddToFavorite() {
//        return addToFavorite;
//    }

    public IGashtLocationViewModel() {
        repository = IGashtRepository.getInstance();
        provinceList.setValue(repository.getProvinceList());
        getLocationListOfProvince();
    }

    public void onRetryClick() {
        getLocationListOfProvince();
    }

    public String getSelectedProvinceName() {
        switch (G.selectedLanguage) {
            case "en":
                return repository.getSelectedProvince().getEnglishName();
            case "fa":
                return repository.getSelectedProvince().getProvinceName();
            default:
                return repository.getSelectedProvince().getProvinceName();
        }
    }

    private void getLocationListOfProvince() {
        showLoadingView.set(View.VISIBLE);
        showMainView.set(View.GONE);
        showViewRefresh.set(View.GONE);
        repository.getLocationListWithProvince(this, this);
    }

    @Override
    public void onSuccess(BaseIGashtResponse<IGashtLocationItem> data) {
        showLoadingView.set(View.GONE);
        showMainView.set(View.VISIBLE);
        locationList.postValue(data.getData());
    }

//    public void addToFavorite(int position) {
//        addToFavorite.setValue(true);
//    }

    public void buyTicket(int position) {
        if (locationList.getValue() != null) {
            repository.setSelectedLocation(locationList.getValue().get(position));
            goToLocationDetail.setValue(true);
        } else {
            goToLocationDetail.setValue(false);
        }
    }

    public void setSelectedProvince(int position) {
        repository.setSelectedProvince(provinceList.getValue().get(position));
    }

    public void onSearchClick() {
        getLocationListOfProvince();
    }

    public void onProvinceTextChange(String s) {
        if (s.length() > 0) {
            selectIcon.set(R.string.close_icon);
        } else {
            selectIcon.set(R.string.down_arrow_icon);
        }
    }

    public void onClearProVinceClick() {
        repository.setSelectedProvince(null);
        clearEditText.setValue(true);
    }
}
