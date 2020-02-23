package net.iGap.repository;

import net.iGap.api.MciApi;
import net.iGap.api.apiService.ApiInitializer;
import net.iGap.model.internetPackage.InternetPackage;
import net.iGap.model.internetPackage.MciInternetPackageFilter;
import net.iGap.observers.interfaces.HandShakeCallback;
import net.iGap.observers.interfaces.ResponseCallback;
import net.iGap.api.apiService.RetrofitFactory;
import net.iGap.model.igasht.BaseIGashtResponse;
import net.iGap.model.MciPurchaseResponse;

import java.util.List;

public class MciInternetPackageRepository {

    private static MciInternetPackageRepository instance;
    private MciApi api;

    public static MciInternetPackageRepository getInstance() {
        if (instance == null) {
            instance = new MciInternetPackageRepository();
        }
        return instance;
    }

    private MciInternetPackageRepository() {
        api = new RetrofitFactory().getMciRetrofit();
    }

    public void onClear() {
        instance = null;
    }

    public void getFilterListData(HandShakeCallback handShakeCallback, ResponseCallback<List<MciInternetPackageFilter>> callback) {
        new ApiInitializer<List<MciInternetPackageFilter>>().initAPI(api.getInternetPackageFilterList(), handShakeCallback, callback);
    }

    public void getInternetPackageList(HandShakeCallback handShakeCallback, ResponseCallback<BaseIGashtResponse<InternetPackage>> callback) {
        new ApiInitializer<BaseIGashtResponse<InternetPackage>>().initAPI(api.getInternetPackageList(), handShakeCallback, callback);
    }

    public void purchaseInternetPackage(String phoneNumber, String internetPackageType, HandShakeCallback handShakeCallback, ResponseCallback<MciPurchaseResponse> callback) {
        new ApiInitializer<MciPurchaseResponse>().initAPI(api.internetPackagePurchase(phoneNumber, internetPackageType), handShakeCallback, callback);
    }
}