package net.iGap.igasht.provinceselect;

import android.arch.lifecycle.MutableLiveData;
import android.databinding.ObservableField;
import android.view.View;

import net.iGap.R;
import net.iGap.igasht.BaseIGashtResponse;
import net.iGap.igasht.BaseIGashtViewModel;
import net.iGap.igasht.IGashtRepository;

import java.util.List;

public class IGashtProvinceViewModel extends BaseIGashtViewModel<BaseIGashtResponse<IGashtProvince>> {

    private ObservableField<String> backgroundImageUrl = new ObservableField<>();
    private MutableLiveData<List<IGashtProvince>> provinceList = new MutableLiveData<>();
    private MutableLiveData<IGashtProvince> goToShowLocationListPage = new MutableLiveData<>();
    private MutableLiveData<Boolean> clearEditText = new MutableLiveData<>();
    private MutableLiveData<Integer> showErrorSelectProvince = new MutableLiveData<>();
    private IGashtProvince selectedProvince;

    private IGashtRepository repository;

    public IGashtProvinceViewModel() {
        repository = IGashtRepository.getInstance();
        getProvinceList();
    }

    public ObservableField<String> getBackgroundImageUrl() {
        return backgroundImageUrl;
    }

    public MutableLiveData<IGashtProvince> getGoToShowLocationListPage() {
        return goToShowLocationListPage;
    }

    public MutableLiveData<List<IGashtProvince>> getProvinceListResult() {
        return provinceList;
    }

    public MutableLiveData<Boolean> getClearEditText() {
        return clearEditText;
    }

    public MutableLiveData<Integer> getShowErrorSelectProvince() {
        return showErrorSelectProvince;
    }

    public void onClearProVinceSearchClick() {
        selectedProvince = null;
        clearEditText.setValue(true);
    }

    public void onSearchPlaceButtonClick() {
        if (selectedProvince != null) {
            goToShowLocationListPage.setValue(selectedProvince);
        } else {
            showErrorSelectProvince.setValue(R.string.select_province);
        }
    }

    public void setSelectedLocation(int position) {
        selectedProvince = provinceList.getValue().get(position);
    }

    public void onRetryClick() {
        getProvinceList();
    }

    private void getProvinceList() {
        showViewRefresh.set(View.GONE);
        showLoadingView.set(View.VISIBLE);
        // base view model implements callback and get response in on Success
        repository.getProvinceList(this);
    }

    @Override
    public void onSuccess(BaseIGashtResponse<IGashtProvince> data) {
        showLoadingView.set(View.GONE);
        showMainView.set(View.VISIBLE);
        provinceList.setValue(data.getData());
    }
}
