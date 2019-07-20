package net.iGap.igasht;

import android.arch.lifecycle.MutableLiveData;
import android.databinding.ObservableField;
import android.util.Log;
import android.view.View;

import net.iGap.R;

import java.util.List;

public class IGashtProvinceViewModel extends BaseIGashtViewModel {

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
        Log.wtf(this.getClass().getName(), "setSelectedLocation");
        selectedProvince = provinceList.getValue().get(position);
    }

    public void onRetryClick() {
        getProvinceList();
    }

    private void getProvinceList() {
        showViewRefresh.set(View.GONE);
        showLoadingView.set(View.VISIBLE);
        repository.getProvinceList(new IGashtRepository.ResponseCallback<ProvinceListResponse>() {
            @Override
            public void onSuccess(ProvinceListResponse data) {
                showLoadingView.set(View.GONE);
                showMainView.set(View.VISIBLE);
                provinceList.setValue(data.getProvinces());
            }

            @Override
            public void onError(ErrorModel error) {
                showLoadingView.set(View.GONE);
                showViewRefresh.set(View.VISIBLE);
                requestErrorMessage.setValue(error.getMessage());
            }

            @Override
            public void onFailed() {
                showViewRefresh.set(View.VISIBLE);
                showLoadingView.set(View.GONE);
            }
        });
    }
}
