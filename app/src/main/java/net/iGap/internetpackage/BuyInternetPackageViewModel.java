package net.iGap.internetpackage;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableInt;
import android.view.View;

import net.iGap.R;
import net.iGap.api.errorhandler.ErrorModel;
import net.iGap.api.errorhandler.ResponseCallback;
import net.iGap.model.OperatorType;

import java.util.ArrayList;
import java.util.HashMap;
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
    private MutableLiveData<TypeFilter> typeList = new MutableLiveData<>();
    private MutableLiveData<List<InternetPackage>> internetPackageFiltered = new MutableLiveData<>();
    private MutableLiveData<Boolean> needUpdateGooglePlay = new MutableLiveData<>();

    private HashMap<String, OperatorType> phoneMap = new HashMap<String, OperatorType>() {
        {
            put("0910", OperatorType.HAMRAH_AVAL);
            put("0911", OperatorType.HAMRAH_AVAL);
            put("0912", OperatorType.HAMRAH_AVAL);
            put("0913", OperatorType.HAMRAH_AVAL);
            put("0914", OperatorType.HAMRAH_AVAL);
            put("0915", OperatorType.HAMRAH_AVAL);
            put("0916", OperatorType.HAMRAH_AVAL);
            put("0917", OperatorType.HAMRAH_AVAL);
            put("0918", OperatorType.HAMRAH_AVAL);
            put("0919", OperatorType.HAMRAH_AVAL);
            put("0990", OperatorType.HAMRAH_AVAL);
            put("0991", OperatorType.HAMRAH_AVAL);

            put("0901", OperatorType.IRANCELL);
            put("0902", OperatorType.IRANCELL);
            put("0903", OperatorType.IRANCELL);
            put("0930", OperatorType.IRANCELL);
            put("0933", OperatorType.IRANCELL);
            put("0935", OperatorType.IRANCELL);
            put("0936", OperatorType.IRANCELL);
            put("0937", OperatorType.IRANCELL);
            put("0938", OperatorType.IRANCELL);
            put("0939", OperatorType.IRANCELL);

            put("0920", OperatorType.RITEL);
            put("0921", OperatorType.RITEL);
            put("0922", OperatorType.RITEL);

        }
    };
    private MciInternetPackageRepository repository;
    private MciInternetPackageFilterResponse filterResponse;
    private List<InternetPackage> packageList;
    private List<InternetPackage> packageListFiltered;
    private boolean isDaily;

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

    public MutableLiveData<TypeFilter> getTypeList() {
        return typeList;
    }

    public MutableLiveData<ErrorModel> getShowRequestErrorMessage() {
        return showRequestErrorMessage;
    }

    public MutableLiveData<Boolean> getNeedUpdateGooglePlay() {
        return needUpdateGooglePlay;
    }

    public void phoneNumberTextChangeListener(String phoneNumber) {
        if (phoneNumber.length() == 11) {
            String s = phoneNumber.substring(0, 4);
            OperatorType opt = phoneMap.get(s);
            if (opt != null) {
                if (opt == OperatorType.HAMRAH_AVAL) {
                    showDetail.set(View.VISIBLE);
                } else {
                    showErrorMessage.setValue(R.string.error);
                }
            } else {
                showErrorMessage.setValue(R.string.error);
            }
        } else {
            showDetail.set(View.GONE);
            showFilterType.set(View.GONE);
            showPackageList.set(View.GONE);
            showPayButton.set(View.GONE);
        }
    }

    public void onCheckedListener(int checkedId) {
        showFilterType.set(View.VISIBLE);
        if (checkedId == R.id.timeType) {
            isDaily = true;
            typeList.setValue(new TypeFilter(filterResponse.getDaily(),true,-1));
        } else {
            isDaily = false;
            List<String> tmp = new ArrayList<>();
            tmp.addAll(filterResponse.getMb());
            tmp.addAll(filterResponse.getGb());
            typeList.setValue(new TypeFilter(tmp,false,filterResponse.getMb().size()));
        }
        showPackageList.set(View.GONE);
        showPayButton.set(View.GONE);
    }

    public void onItemSelectedTypeFilter(int position) {
        if (position != 0) {
            if (isDaily){
                getPackageListWithFilter(" " + filterResponse.getDaily().get(position) + " روزه ");
            }else{
                if (position <= filterResponse.getMb().size()){
                    getPackageListWithFilter(" " + filterResponse.getMb().get(position-1) + " مگ");
                }else{
                    getPackageListWithFilter(" " + filterResponse.getGb().get(position - filterResponse.getMb().size()) + "گیگابایت ");
                }
            }
            showPackageList.set(View.VISIBLE);
            internetPackageFiltered.setValue(packageListFiltered);
        } else {
            showPackageList.set(View.GONE);
            showPayButton.set(View.GONE);
        }
    }

    public void onItemSelectedPackageList(int position) {
        if (position != 0) {
            showPayButton.set(View.VISIBLE);
        } else {
            showPayButton.set(View.GONE);
        }
    }

    public void onBuyClick() {

    }

    public void onRetryClick() {
        getData();
    }

    private void getData() {
        showLoadingView.set(View.VISIBLE);
        showMainView.set(View.GONE);
        showRetryView.set(View.GONE);
        if (filterResponse == null) {
            repository.getFilterListData(new ResponseCallback<MciInternetPackageFilterResponse>() {
                @Override
                public void onSuccess(MciInternetPackageFilterResponse data) {
                    filterResponse = data;
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
        showMainView.set(View.GONE);
        showLoadingView.set(View.INVISIBLE);
        showRequestErrorMessage.setValue(error);
    }

    private void onFailedHandler(boolean isNeedUpdateGooglePlay) {
        showLoadingView.set(View.GONE);
        showRetryView.set(View.VISIBLE);
        showMainView.set(View.GONE);
        needUpdateGooglePlay.setValue(isNeedUpdateGooglePlay);
    }

    private void getPackageListWithFilter(String keyword){
        packageListFiltered = new ArrayList<>();
        for (int i = 0;i<packageList.size();i++){
            if (packageList.get(i).getDescription().contains(keyword)){
                packageListFiltered.add(packageList.get(i));
            }
        }
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        repository.onClear();
    }
}
