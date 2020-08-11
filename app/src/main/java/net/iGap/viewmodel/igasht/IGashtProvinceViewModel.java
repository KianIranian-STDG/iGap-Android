package net.iGap.viewmodel.igasht;

import android.view.View;

import androidx.databinding.ObservableField;
import androidx.databinding.ObservableInt;
import androidx.lifecycle.MutableLiveData;

import net.iGap.R;
import net.iGap.model.igasht.BaseIGashtResponse;
import net.iGap.model.igasht.IGashtProvince;
import net.iGap.repository.IGashtRepository;

import java.util.List;

public class IGashtProvinceViewModel extends BaseIGashtViewModel<BaseIGashtResponse<IGashtProvince>> {

    private ObservableField<String> backgroundImageUrl = new ObservableField<>("");
    private ObservableInt selectIcon = new ObservableInt(R.string.down_arrow_icon);
    private MutableLiveData<List<IGashtProvince>> provinceList = new MutableLiveData<>();
    private MutableLiveData<Boolean> goToShowLocationListPage = new MutableLiveData<>();
    private MutableLiveData<Boolean> clearEditText = new MutableLiveData<>();

    private IGashtRepository repository;

    public IGashtProvinceViewModel() {
        repository = IGashtRepository.getInstance();
        getProvinceList();
    }

    public ObservableField<String> getBackgroundImageUrl() {
        return backgroundImageUrl;
    }

    public MutableLiveData<Boolean> getGoToShowLocationListPage() {
        return goToShowLocationListPage;
    }

    public MutableLiveData<List<IGashtProvince>> getProvinceListResult() {
        return provinceList;
    }

    public MutableLiveData<Boolean> getClearEditText() {
        return clearEditText;
    }

    public ObservableInt getSelectIcon() {
        return selectIcon;
    }

    public void onClearProVinceSearchClick() {
        repository.setSelectedProvince(null);
        clearEditText.setValue(true);
    }

    public void onSearchPlaceButtonClick() {
        if (repository.getSelectedProvince() != null) {
            goToShowLocationListPage.setValue(true);
        } else {
            goToShowLocationListPage.setValue(false);
        }
    }

    public void setSelectedLocation(int position) {
        if (provinceList.getValue() != null) {
            repository.setSelectedProvince(provinceList.getValue().get(position));
            onSearchPlaceButtonClick();
        }
    }

    public void onRetryClick() {
        getProvinceList();
    }

    public void onProvinceSearchTextChange(String s) {
        if (s.length() > 0) {
            selectIcon.set(R.string.close_icon);
        } else {
            selectIcon.set(R.string.down_arrow_icon);
        }
    }

    private void getProvinceList() {
        showViewRefresh.set(View.GONE);
        showLoadingView.set(View.VISIBLE);
        // base view model implements callback and get response in on Success
        repository.getProvinceList(this, this);
    }

    @Override
    public void onSuccess(BaseIGashtResponse<IGashtProvince> data) {
        showLoadingView.set(View.GONE);
        showMainView.set(View.VISIBLE);
        repository.setProvinceList(data.getData());
        provinceList.setValue(data.getData());
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        repository.clearInstance();
    }
}
