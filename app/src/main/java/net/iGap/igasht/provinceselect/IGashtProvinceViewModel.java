package net.iGap.igasht.provinceselect;

import android.arch.lifecycle.MutableLiveData;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;
import android.view.View;

import net.iGap.G;
import net.iGap.R;
import net.iGap.igasht.BaseIGashtResponse;
import net.iGap.igasht.BaseIGashtViewModel;
import net.iGap.igasht.IGashtRepository;

import java.util.List;

public class IGashtProvinceViewModel extends BaseIGashtViewModel<BaseIGashtResponse<IGashtProvince>> {

    private ObservableField<String> backgroundImageUrl = new ObservableField<>("");
    private ObservableInt selectIcon = new ObservableInt(R.string.down_arrow_icon);
    private ObservableInt backgroundShap = new ObservableInt(R.drawable.shape_igasht_yellow);
    private MutableLiveData<List<IGashtProvince>> provinceList = new MutableLiveData<>();
    private MutableLiveData<Boolean> goToShowLocationListPage = new MutableLiveData<>();
    private MutableLiveData<Boolean> clearEditText = new MutableLiveData<>();

    private IGashtRepository repository;

    public IGashtProvinceViewModel() {
        if(G.isDarkTheme){
            backgroundShap.set(R.drawable.shape_igasht_gradient_gray);
        }
        repository = IGashtRepository.getInstance();
        getProvinceList();
    }

    public ObservableField<String> getBackgroundImageUrl() {
        return backgroundImageUrl;
    }

    public ObservableInt getBackgroundShap() {
        return backgroundShap;
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
        repository.setSelectedProvince(provinceList.getValue().get(position));
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
        repository.getProvinceList(this);
    }

    @Override
    public void onSuccess(BaseIGashtResponse<IGashtProvince> data) {
        showLoadingView.set(View.GONE);
        showMainView.set(View.VISIBLE);
        provinceList.setValue(data.getData());
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        repository.clearInstance();
    }
}
