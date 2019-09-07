package net.iGap.internetpackage;

import android.view.View;

import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableInt;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import net.iGap.R;
import net.iGap.api.errorhandler.ErrorModel;
import net.iGap.api.errorhandler.ResponseCallback;
import net.iGap.model.MciPurchaseResponse;
import net.iGap.model.OperatorType;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class BuyInternetPackageViewModel extends ViewModel {

    private ObservableInt showDetail = new ObservableInt(View.GONE);
    private ObservableInt showFilterType = new ObservableInt(View.GONE);
    private ObservableInt showPackageList = new ObservableInt(View.GONE);
    private ObservableInt showPayButton = new ObservableInt(View.GONE);
    private ObservableInt showLoadingView = new ObservableInt(View.INVISIBLE);
    private ObservableInt showMainView = new ObservableInt(View.GONE);
    private ObservableInt showRetryView = new ObservableInt(View.GONE);
    private ObservableBoolean enabledPaymentButton = new ObservableBoolean(true);
    private MutableLiveData<Integer> showErrorMessage = new MutableLiveData<>();
    private MutableLiveData<ErrorModel> showRequestErrorMessage = new MutableLiveData<>();
    private MutableLiveData<List<MciInternetPackageFilter>> typeList = new MutableLiveData<>();
    private MutableLiveData<List<InternetPackage>> internetPackageFiltered = new MutableLiveData<>();
    private MutableLiveData<Boolean> needUpdateGooglePlay = new MutableLiveData<>();
    private MutableLiveData<String> goToPaymentPage = new MutableLiveData<>();
    private MutableLiveData<Boolean> clearTypeChecked = new MutableLiveData<>();
    private MciInternetPackageRepository repository;
    private List<MciInternetPackageFilter> daysFilter;
    private List<MciInternetPackageFilter> trafficFilter;
    private List<InternetPackage> packageList;
    private List<InternetPackage> packageListFiltered;
    private boolean isDaily;
    private int selectedPackageType;

    public BuyInternetPackageViewModel() {
        repository = MciInternetPackageRepository.getInstance();
        getData();
    }

    public ObservableInt getShowDetail() {
        return showDetail;
    }

    public ObservableInt getShowFilterType() {
        return showFilterType;
    }

    public ObservableInt getShowPackageList() {
        return showPackageList;
    }

    public ObservableInt getShowPayButton() {
        return showPayButton;
    }

    public ObservableInt getShowLoadingView() {
        return showLoadingView;
    }

    public ObservableInt getShowMainView() {
        return showMainView;
    }

    public ObservableBoolean getEnabledPaymentButton() {
        return enabledPaymentButton;
    }

    public ObservableInt getShowRetryView() {
        return showRetryView;
    }

    public MutableLiveData<Integer> getShowErrorMessage() {
        return showErrorMessage;
    }

    public MutableLiveData<List<MciInternetPackageFilter>> getTypeList() {
        return typeList;
    }

    public MutableLiveData<ErrorModel> getShowRequestErrorMessage() {
        return showRequestErrorMessage;
    }

    public MutableLiveData<Boolean> getNeedUpdateGooglePlay() {
        return needUpdateGooglePlay;
    }

    public MutableLiveData<String> getGoToPaymentPage() {
        return goToPaymentPage;
    }

    public MutableLiveData<List<InternetPackage>> getInternetPackageFiltered() {
        return internetPackageFiltered;
    }

    public MutableLiveData<Boolean> getClearTypeChecked() {
        return clearTypeChecked;
    }

    public void phoneNumberTextChangeListener(String phoneNumber) {
        if (phoneNumber.length() == 11) {
            if (new OperatorType().isMci(phoneNumber.substring(0, 4))) {
                showDetail.set(View.VISIBLE);
            } else {
                showErrorMessage.setValue(R.string.error);
            }
        } else {
            clearTypeChecked.setValue(true);
            showDetail.set(View.GONE);
            showFilterType.set(View.GONE);
            showPackageList.set(View.GONE);
            showPayButton.set(View.GONE);
        }
    }

    public void onCheckedListener(int checkedId) {
        if (checkedId == R.id.timeType) {
            isDaily = true;
            typeList.setValue(daysFilter);
        } else if (checkedId == R.id.volumeType) {
            isDaily = false;
            typeList.setValue(trafficFilter);
        }
        showFilterType.set(View.VISIBLE);
        showPackageList.set(View.GONE);
        showPayButton.set(View.GONE);
    }

    public void onItemSelectedTypeFilter(int position) {
        if (position != 0) {
            getPackageListWithFilter(position - 1);
            showPackageList.set(View.VISIBLE);
            internetPackageFiltered.setValue(packageListFiltered);
        } else {
            showPackageList.set(View.GONE);
            showPayButton.set(View.GONE);
        }
    }

    public void onItemSelectedPackageList(int position) {
        if (position != 0) {
            selectedPackageType = packageListFiltered.get(position - 1).getType();
            showPayButton.set(View.VISIBLE);
        } else {
            showPayButton.set(View.GONE);
        }
    }

    public void onBuyClick(String phoneNumber) {
        if (selectedPackageType > 0) {
            enabledPaymentButton.set(false);
            showLoadingView.set(View.VISIBLE);
            repository.purchaseInternetPackage(phoneNumber.substring(1), String.valueOf(selectedPackageType), new ResponseCallback<MciPurchaseResponse>() {
                @Override
                public void onSuccess(MciPurchaseResponse data) {
                    showLoadingView.set(View.INVISIBLE);
                    enabledPaymentButton.set(true);
                    goToPaymentPage.setValue(data.getToken());
                }

                @Override
                public void onError(ErrorModel error) {
                    showLoadingView.set(View.INVISIBLE);
                    showRequestErrorMessage.setValue(error);
                    enabledPaymentButton.set(true);
                }

                @Override
                public void onFailed(boolean handShakeError) {
                    showLoadingView.set(View.INVISIBLE);
                    needUpdateGooglePlay.setValue(handShakeError);
                    enabledPaymentButton.set(true);
                }
            });
        } else {
            showErrorMessage.setValue(R.string.error);
        }
    }

    public void onRetryClick() {
        getData();
    }

    private void getData() {
        showLoadingView.set(View.VISIBLE);
        showMainView.set(View.GONE);
        showRetryView.set(View.GONE);
        if (daysFilter == null || trafficFilter == null) {
            repository.getFilterListData(new ResponseCallback<List<MciInternetPackageFilter>>() {
                @Override
                public void onSuccess(List<MciInternetPackageFilter> data) {
                    getFilterListDuration(data);
                    getFilterListTraffic(data);
                    getData();
                }

                @Override
                public void onError(ErrorModel error) {
                    requestError(error);
                }

                @Override
                public void onFailed(boolean handShakeError) {
                    onFailedHandler(handShakeError);
                }
            });
        } else if (packageList == null) {
            repository.getInternetPackageList(new ResponseCallback<List<InternetPackage>>() {
                @Override
                public void onSuccess(List<InternetPackage> data) {
                    packageList = data;
                    showMainView();
                }

                @Override
                public void onError(ErrorModel error) {
                    requestError(error);
                }

                @Override
                public void onFailed(boolean handShakeError) {
                    onFailedHandler(handShakeError);
                }
            });
        } else {
            showMainView();
        }
    }

    private void showMainView() {
        showLoadingView.set(View.INVISIBLE);
        showMainView.set(View.VISIBLE);
        showRetryView.set(View.GONE);
    }

    private void requestError(ErrorModel error) {
        showRetryView.set(View.VISIBLE);
        showLoadingView.set(View.INVISIBLE);
        showRequestErrorMessage.setValue(error);
    }

    private void onFailedHandler(boolean isNeedUpdateGooglePlay) {
        showLoadingView.set(View.INVISIBLE);
        showRetryView.set(View.VISIBLE);
        needUpdateGooglePlay.setValue(isNeedUpdateGooglePlay);
    }

    private void getPackageListWithFilter(int position) {
        MciInternetPackageFilter tmp;
        if (isDaily) {
            tmp = daysFilter.get(position);
        } else {
            tmp = trafficFilter.get(position);
        }
        packageListFiltered = getPackageList(tmp);
    }

    private void getFilterListDuration(@NotNull List<MciInternetPackageFilter> data) {
        daysFilter = new ArrayList<>();
        for (int i = 0; i < data.size(); i++) {
            if (data.get(i).getCategory().getType().toLowerCase().equals("duration")) {
                daysFilter.add(data.get(i));
            }
        }
    }

    private void getFilterListTraffic(@NotNull List<MciInternetPackageFilter> data) {
        trafficFilter = new ArrayList<>();
        for (int i = 0; i < data.size(); i++) {
            if (data.get(i).getCategory().getType().toLowerCase().equals("traffic")) {
                trafficFilter.add(data.get(i));
            }
        }
    }

    private List<InternetPackage> getPackageList(MciInternetPackageFilter filter) {
        List<InternetPackage> tmp = new ArrayList<>();
        for (int i = 0; i < packageList.size(); i++) {
            if (isDaily) {
                if (packageList.get(i).getDurationId().equals(filter.getId())) {
                    tmp.add(packageList.get(i));
                }
            } else {
                if (packageList.get(i).getTrafficId().equals(filter.getId())) {
                    tmp.add(packageList.get(i));
                }
            }
        }
        return tmp;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        repository.onClear();
    }
}
