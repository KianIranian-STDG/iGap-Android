package net.iGap.igasht;

import android.arch.lifecycle.MutableLiveData;
import android.databinding.ObservableField;
import android.view.View;

public class IGashtProvinceViewModel extends BaseIGashtViewModel {

    private ObservableField<String> backgroundImageUrl = new ObservableField<>();

    //ui
    private MutableLiveData<Boolean> goToShowLocationListPage = new MutableLiveData<>();

    public IGashtProvinceViewModel() {
        // add send request get province list
        // repository.getProvinceList();
        //on response callback
        showLoadingView.set(View.GONE);
        showMainView.set(View.VISIBLE);
        // on fail Show View
        //showViewRefresh.set(View.VISIBLE);
        //showLoadingView.set(View.GONE);
    }

    public ObservableField<String> getBackgroundImageUrl() {
        return backgroundImageUrl;
    }

    public MutableLiveData<Boolean> getGoToShowLocationListPage() {
        return goToShowLocationListPage;
    }

    public void onClearProVinceSearchClick() {

    }

    public void onProvinceTextChange(String inputText) {

    }

    public void onSearchClick() {

    }

    public void onSearchPlaceButtonClick() {
        goToShowLocationListPage.setValue(true);
    }

    public void onRetryClick() {
        showViewRefresh.set(View.GONE);
        showLoadingView.set(View.VISIBLE);
    }
}
