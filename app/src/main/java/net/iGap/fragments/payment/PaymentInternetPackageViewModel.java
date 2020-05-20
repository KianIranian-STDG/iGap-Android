package net.iGap.fragments.payment;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import net.iGap.G;
import net.iGap.R;
import net.iGap.api.apiService.BaseAPIViewModel;
import net.iGap.model.igasht.BaseIGashtResponse;
import net.iGap.model.paymentPackage.InternetPackage;
import net.iGap.model.paymentPackage.MciInternetPackageFilter;
import net.iGap.model.paymentPackage.MciPurchaseResponse;
import net.iGap.observers.interfaces.ResponseCallback;
import net.iGap.repository.MciInternetPackageRepository;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class PaymentInternetPackageViewModel extends BaseAPIViewModel {
    private MutableLiveData<List<MciInternetPackageFilter>> timeFilterListObservable = new MutableLiveData<>();
    private MutableLiveData<List<MciInternetPackageFilter>> trafficFilterListObservable = new MutableLiveData<>();
    private MutableLiveData<List<InternetPackage>> packageListSuggestedObservable = new MutableLiveData<>();
    private MutableLiveData<List<InternetPackage>> packageListOthersObservable = new MutableLiveData<>();
    private MutableLiveData<String> goToPaymentPage = new MutableLiveData<>();
    private MutableLiveData<Integer> showErrorMessage = new MutableLiveData<>();
    private MutableLiveData<String> showRequestErrorMessage = new MutableLiveData<>();
    private MutableLiveData<Boolean> loadingVisibility = new MutableLiveData<>();
    private MutableLiveData<Boolean> isDataLoaded = new MutableLiveData<>();

    private List<MciInternetPackageFilter> timeFilters;
    private List<MciInternetPackageFilter> trafficFilters;
    private List<InternetPackage> packageListSuggested;
    private List<InternetPackage> packageListOthers;
    private List<InternetPackage> packageList;

    private MciInternetPackageRepository repository;
    private String operator;
    private String simType;
    private String phoneNumber;
    private int selectedPackageType = -10;


    public PaymentInternetPackageViewModel() {
        repository = MciInternetPackageRepository.getInstance();
    }

    public void setOperator(String opt) {
        operator = opt;
    }

    public void getData() {
        loadingVisibility.setValue(true);

        repository.getFilterListData(operator, this, new ResponseCallback<List<MciInternetPackageFilter>>() {
            @Override
            public void onSuccess(List<MciInternetPackageFilter> data) {
                fillTimeAndTraffic(data);
            }

            @Override
            public void onError(String error) {
                showRequestErrorMessage.setValue(error);
            }

            @Override
            public void onFailed() {
                showErrorMessage.setValue(R.string.time_out_error);
            }
        });

        repository.getInternetPackageList(operator, simType, this, new ResponseCallback<BaseIGashtResponse<InternetPackage>>() {
            @Override
            public void onSuccess(BaseIGashtResponse<InternetPackage> data) {
                packageList = data.getData();
                updateInternetPackager(-1, -1);
                loadingVisibility.setValue(false);
                isDataLoaded.setValue(true);
            }

            @Override
            public void onError(String error) {
                showRequestErrorMessage.setValue(error);
                loadingVisibility.setValue(false);
                isDataLoaded.setValue(true);
            }

            @Override
            public void onFailed() {
                loadingVisibility.setValue(false);
                showErrorMessage.setValue(R.string.time_out_error);
                isDataLoaded.setValue(true);
            }
        });
    }

    private void notifyPackageListFiltered(List<InternetPackage> packageListSuggested, List<InternetPackage> packageListOthers) {
        packageListSuggestedObservable.setValue(packageListSuggested);
        packageListOthersObservable.setValue(packageListOthers);
    }

    public void updateInternetPackager(int timePosition, int trafficPosition) {
        packageListSuggested = new ArrayList<>();
        packageListOthers = new ArrayList<>();

        if (packageList == null || timeFilters == null || trafficFilters == null)
            return;

        if (timePosition < 0 && trafficPosition < 0) {
            packageListSuggested.clear();
            packageListOthers.clear();
            for (int i = 0; i < packageList.size(); i++) {
                if (packageList.get(i).isSpecial()) {
                    packageListSuggested.add(packageList.get(i));
                } else {
                    packageListOthers.add(packageList.get(i));
                }
            }
        } else if (timePosition >= 0 && trafficPosition >= 0) {
            for (int i = 0; i < packageList.size(); i++) {
                if (packageList.get(i).getDurationId() != null &&
                        packageList.get(i).getDurationId().equals(timeFilters.get(timePosition).getId()) &&
                        packageList.get(i).getTrafficId() != null &&
                        packageList.get(i).getTrafficId().equals(trafficFilters.get(trafficPosition).getId())) {

                    if (packageList.get(i).isSpecial())
                        packageListSuggested.add(packageList.get(i));
                    else
                        packageListOthers.add(packageList.get(i));
                }
            }
        } else {
            for (int i = 0; i < packageList.size(); i++) {
                if (timePosition >= 0 && timePosition < timeFilters.size() &&
                        packageList.get(i).getDurationId() != null &&
                        packageList.get(i).getDurationId().equals(timeFilters.get(timePosition).getId())) {
                    if (packageList.get(i).isSpecial())
                        packageListSuggested.add(packageList.get(i));
                    else
                        packageListOthers.add(packageList.get(i));
                } else if (trafficPosition >= 0 && trafficPosition < trafficFilters.size() &&
                        packageList.get(i).getTrafficId() != null &&
                        packageList.get(i).getTrafficId().equals(trafficFilters.get(trafficPosition).getId())) {
                    if (packageList.get(i).isSpecial())
                        packageListSuggested.add(packageList.get(i));
                    else
                        packageListOthers.add(packageList.get(i));
                }
            }
        }
        notifyPackageListFiltered(packageListSuggested, packageListOthers);
        isDataLoaded.setValue(true);
    }

    public void requestPayment() {
        if (selectedPackageType < -9) {
            showErrorMessage.setValue(R.string.no_package_selected);
            return;
        }
        loadingVisibility.setValue(true);

        repository.purchaseInternetPackage(operator, phoneNumber.substring(1),
                String.valueOf(selectedPackageType), this, new ResponseCallback<MciPurchaseResponse>() {
                    @Override
                    public void onSuccess(MciPurchaseResponse data) {
                        G.handler.post(() -> {
                            goToPaymentPage.setValue(data.getToken());
                            loadingVisibility.setValue(false);
                        });
                    }

                    @Override
                    public void onError(String error) {
                        showRequestErrorMessage.setValue(error);
                        loadingVisibility.setValue(false);
                    }

                    @Override
                    public void onFailed() {
                        loadingVisibility.setValue(false);
                        showErrorMessage.setValue(R.string.time_out_error);
                    }
                });
    }

    private void fillTimeAndTraffic(@NotNull List<MciInternetPackageFilter> data) {
        timeFilters = new ArrayList<>();
        trafficFilters = new ArrayList<>();

        for (int i = 0; i < data.size(); i++) {
            if (data.get(i).getCategory().getType().toLowerCase().equals("duration")) {
                timeFilters.add(data.get(i));
            } else if (data.get(i).getCategory().getType().toLowerCase().equals("traffic")) {
                trafficFilters.add(data.get(i));
            }
        }
        timeFilterListObservable.postValue(timeFilters);
        trafficFilterListObservable.postValue(trafficFilters);
    }

    public LiveData<List<MciInternetPackageFilter>> getTimeFilterListObservable() {
        return timeFilterListObservable;
    }

    public LiveData<List<MciInternetPackageFilter>> getTrafficFilterListObservable() {
        return trafficFilterListObservable;
    }

    public LiveData<List<InternetPackage>> getPackageListSuggestedObservable() {
        return packageListSuggestedObservable;
    }

    public LiveData<List<InternetPackage>> getPackageListOthersObservable() {
        return packageListOthersObservable;
    }

    public LiveData<String> getGoToPaymentPage() {
        return goToPaymentPage;
    }

    public LiveData<Integer> getShowErrorMessage() {
        return showErrorMessage;
    }

    public LiveData<String> getShowRequestErrorMessage() {
        return showRequestErrorMessage;
    }

    public LiveData<Boolean> getLoadingVisibility() {
        return loadingVisibility;
    }

    public LiveData<Boolean> getIsDataLoaded() {
        return isDataLoaded;
    }

    public void setSimType(String simType) {
        this.simType = simType;
    }

    public void setPackageType(InternetPackage item) {
        selectedPackageType = item.getType();
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
