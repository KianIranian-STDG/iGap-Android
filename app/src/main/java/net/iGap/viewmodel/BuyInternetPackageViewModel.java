package net.iGap.viewmodel;

import android.view.View;

import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableInt;
import androidx.lifecycle.MutableLiveData;

import net.iGap.G;
import net.iGap.R;
import net.iGap.api.apiService.BaseAPIViewModel;
import net.iGap.model.MciPurchaseResponse;
import net.iGap.model.OperatorType;
import net.iGap.model.igasht.BaseIGashtResponse;
import net.iGap.model.internetPackage.InternetPackage;
import net.iGap.model.internetPackage.InternetPackageFilter;
import net.iGap.model.internetPackage.MciInternetPackageFilter;
import net.iGap.observers.interfaces.ResponseCallback;
import net.iGap.repository.MciInternetPackageRepository;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static net.iGap.viewmodel.FragmentPaymentChargeViewModel.MCI;
import static net.iGap.viewmodel.FragmentPaymentChargeViewModel.MTN;
import static net.iGap.viewmodel.FragmentPaymentChargeViewModel.RIGHTEL;

public class BuyInternetPackageViewModel extends BaseAPIViewModel {

    private ObservableInt showDetail = new ObservableInt(View.GONE);
    private ObservableInt showFilterType = new ObservableInt(View.GONE);
    private ObservableInt showFilterPkgType = new ObservableInt(View.GONE);
    private ObservableInt showPackageList = new ObservableInt(View.GONE);
    private ObservableInt showPayButton = new ObservableInt(View.GONE);
    private ObservableInt showLoadingView = new ObservableInt(View.INVISIBLE);
    private ObservableInt showMainView = new ObservableInt(View.GONE);
    private ObservableInt showRetryView = new ObservableInt(View.GONE);
    private ObservableBoolean enabledPaymentButton = new ObservableBoolean(true);
    private MutableLiveData<Integer> showErrorMessage = new MutableLiveData<>();
    private MutableLiveData<String> showRequestErrorMessage = new MutableLiveData<>();
    private MutableLiveData<List<MciInternetPackageFilter>> typeList = new MutableLiveData<>();
    private MutableLiveData<List<InternetPackageFilter>> packageFiltersList = new MutableLiveData<>();
    private MutableLiveData<List<InternetPackage>> internetPackageFiltered = new MutableLiveData<>();
    private MutableLiveData<String> goToPaymentPage = new MutableLiveData<>();
    private MutableLiveData<Boolean> clearTypeChecked = new MutableLiveData<>();
    private MciInternetPackageRepository repository;
    private List<MciInternetPackageFilter> daysFilter;
    private List<MciInternetPackageFilter> trafficFilter;
    private List<InternetPackage> packageList;
    private List<InternetPackageFilter> packageFilters;
    private List<InternetPackage> packageListFiltered;
    private boolean isDaily;
    private int selectedPackageType;
    private String operator;
    private String selectedPackagesFilter;

    public BuyInternetPackageViewModel() {
        repository = MciInternetPackageRepository.getInstance();
        getPackagesFilters();
    }

    private void getPackagesFilters() {
        showLoadingView.set(View.VISIBLE);
        showMainView.set(View.GONE);
        showRetryView.set(View.GONE);

        repository.getInternetPackagesFilters(this, new ResponseCallback<BaseIGashtResponse<InternetPackageFilter>>() {
            @Override
            public void onSuccess(BaseIGashtResponse<InternetPackageFilter> data) {
                packageFilters = data.getData();
                packageFiltersList.setValue(packageFilters);
                showMainView();
            }

            @Override
            public void onError(String error) {
                requestError(error);
            }

            @Override
            public void onFailed() {
                onFailedHandler();
            }
        });
    }

    public ObservableInt getShowDetail() {
        return showDetail;
    }

    public ObservableInt getShowFilterType() {
        return showFilterType;
    }

    public ObservableInt getShowFilterPkgType() {
        return showFilterPkgType;
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

    public MutableLiveData<List<InternetPackageFilter>> getPackageFiltersList() {
        return packageFiltersList;
    }

    public MutableLiveData<String> getShowRequestErrorMessage() {
        return showRequestErrorMessage;
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
            String prefix = phoneNumber.substring(0, 4);
            if (new OperatorType().isValidType(prefix)) {
                OperatorType.Type opt = new OperatorType().getOperation(prefix);
                if (opt != null) {
                    if (opt == OperatorType.Type.RITEL)
                        operator = RIGHTEL;
                    else if (opt == OperatorType.Type.HAMRAH_AVAL)
                        operator = MCI;
                    else if (opt == OperatorType.Type.IRANCELL)
                        operator = MTN;

                    showFilterPkgType.set(View.VISIBLE);
                }

            } else {
                showErrorMessage.setValue(R.string.error);
            }
        } else {
            packageFiltersList.setValue(null);
            clearTypeChecked.setValue(true);
            showFilterPkgType.set(View.GONE);
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

    public void onItemSelectedPkgTypeFilter(int position) {

        clearTypeChecked.setValue(true);
        showDetail.set(View.GONE);
        showFilterType.set(View.GONE);
        showPackageList.set(View.GONE);
        showPayButton.set(View.GONE);

        if (position != 0) {
            selectedPackagesFilter = packageFilters.get(position - 1).getType();
            packageList = null;
            daysFilter = null;
            trafficFilter = null;

            getData();
        }
    }

    private void getPackagesByFilter(String selectedPackagesFilter) {
        showLoadingView.set(View.VISIBLE);
        repository.getInternetPackageList(operator, selectedPackagesFilter, this, new ResponseCallback<BaseIGashtResponse<InternetPackage>>() {
            @Override
            public void onSuccess(BaseIGashtResponse<InternetPackage> data) {
                packageList = data.getData();
                showMainView();
                if (data.getData().size() == 0) {
                    showDetail.set(View.GONE);
                    showErrorMessage.setValue(R.string.no_item);
                } else {
                    showDetail.set(View.VISIBLE);
                }
            }

            @Override
            public void onError(String error) {
                requestError(error);
            }

            @Override
            public void onFailed() {
                onFailedHandler();
            }
        });
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
            repository.purchaseInternetPackage(operator, phoneNumber.substring(1), String.valueOf(selectedPackageType), this, new ResponseCallback<MciPurchaseResponse>() {
                @Override
                public void onSuccess(MciPurchaseResponse data) {
                    G.handler.post(() -> {
                        showLoadingView.set(View.INVISIBLE);
                        enabledPaymentButton.set(true);
                        goToPaymentPage.setValue(data.getToken());
                    });
                }

                @Override
                public void onError(String error) {
                    showLoadingView.set(View.INVISIBLE);
                    showRequestErrorMessage.setValue(error);
                    enabledPaymentButton.set(true);
                }

                @Override
                public void onFailed() {
                    showLoadingView.set(View.INVISIBLE);
                    enabledPaymentButton.set(true);
                }
            });
        } else {
            showErrorMessage.setValue(R.string.error);
        }
    }

    public void onRetryClick() {
        if (packageFilters == null) {
            getPackagesFilters();
        } else {
            getData();
        }
    }

    private void getData() {
        showLoadingView.set(View.VISIBLE);
        //showMainView.set(View.GONE);
        showRetryView.set(View.GONE);
        if (daysFilter == null || trafficFilter == null) {
            repository.getFilterListData(operator, this, new ResponseCallback<List<MciInternetPackageFilter>>() {
                @Override
                public void onSuccess(List<MciInternetPackageFilter> data) {
                    getFilterListDuration(data);
                    getFilterListTraffic(data);
                    getData();
                }

                @Override
                public void onError(String error) {
                    requestError(error);
                }

                @Override
                public void onFailed() {
                    onFailedHandler();
                }
            });
        } else if (packageList == null) {
            getPackagesByFilter(selectedPackagesFilter);
        } else {
            showMainView();
        }
    }

    private void showMainView() {
        showLoadingView.set(View.INVISIBLE);
        showMainView.set(View.VISIBLE);
        showRetryView.set(View.GONE);
    }

    private void requestError(String error) {
        showRetryView.set(View.VISIBLE);
        showLoadingView.set(View.INVISIBLE);
        showRequestErrorMessage.setValue(error);
    }

    private void onFailedHandler() {
        showLoadingView.set(View.INVISIBLE);
        showRetryView.set(View.VISIBLE);
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
                if (packageList.get(i).getDurationId() != null && packageList.get(i).getDurationId().equals(filter.getId())) {
                    tmp.add(packageList.get(i));
                }
            } else {
                if (packageList.get(i).getTrafficId() != null && packageList.get(i).getTrafficId().equals(filter.getId())) {
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
