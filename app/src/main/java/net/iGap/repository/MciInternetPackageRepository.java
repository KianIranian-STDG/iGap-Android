package net.iGap.repository;

import net.iGap.api.ChargeApi;
import net.iGap.api.apiService.ApiInitializer;
import net.iGap.api.apiService.RetrofitFactory;
import net.iGap.model.igasht.BaseIGashtResponse;
import net.iGap.model.paymentPackage.InternetPackage;
import net.iGap.model.paymentPackage.InternetPackageFilter;
import net.iGap.model.paymentPackage.MciInternetPackageFilter;
import net.iGap.model.paymentPackage.MciPurchaseResponse;
import net.iGap.observers.interfaces.HandShakeCallback;
import net.iGap.observers.interfaces.ResponseCallback;

import java.util.List;

public class MciInternetPackageRepository {

    private static MciInternetPackageRepository instance;
    private ChargeApi api;

    public static MciInternetPackageRepository getInstance() {
        if (instance == null) {
            instance = new MciInternetPackageRepository();
        }
        return instance;
    }

    private MciInternetPackageRepository() {
        api = new RetrofitFactory().getChargeRetrofit();
    }

    public void onClear() {
        instance = null;
    }

    public void getFilterListData(String operator, HandShakeCallback handShakeCallback, ResponseCallback<List<MciInternetPackageFilter>> callback) {
        new ApiInitializer<List<MciInternetPackageFilter>>().initAPI(api.getInternetPackageFilterList(operator), handShakeCallback, callback);
    }

    public void getInternetPackageList(String operator, String filter, HandShakeCallback handShakeCallback, ResponseCallback<BaseIGashtResponse<InternetPackage>> callback) {
        new ApiInitializer<BaseIGashtResponse<InternetPackage>>().initAPI(api.getInternetPackageList(operator, filter), handShakeCallback, callback);
    }

    public void getInternetPackagesFilters(HandShakeCallback handShakeCallback, ResponseCallback<BaseIGashtResponse<InternetPackageFilter>> callback) {
        new ApiInitializer<BaseIGashtResponse<InternetPackageFilter>>().initAPI(api.getInternetPackageFilters(), handShakeCallback, callback);
    }

    public void purchaseInternetPackage(String operator, String phoneNumber, String internetPackageType, HandShakeCallback handShakeCallback, ResponseCallback<MciPurchaseResponse> callback) {
        new ApiInitializer<MciPurchaseResponse>().initAPI(api.internetPackagePurchase(operator, phoneNumber, internetPackageType), handShakeCallback, callback);
    }
}
