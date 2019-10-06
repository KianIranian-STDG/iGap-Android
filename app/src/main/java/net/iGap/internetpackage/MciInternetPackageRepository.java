package net.iGap.internetpackage;

import android.util.Log;

import net.iGap.api.MciApi;
import net.iGap.api.apiService.ApiInitializer;
import net.iGap.api.apiService.RetrofitFactory;
import net.iGap.api.errorhandler.ErrorHandler;
import net.iGap.api.apiService.ResponseCallback;
import net.iGap.igasht.BaseIGashtResponse;
import net.iGap.model.MciPurchaseResponse;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
        api = new RetrofitFactory().getMciRetrofit().create(MciApi.class);
    }

    public void onClear() {
        instance = null;
    }

    public void getFilterListData(ResponseCallback<List<MciInternetPackageFilter>> callback) {

        new ApiInitializer<List<MciInternetPackageFilter>>().initAPI(api.getInternetPackageFilterList(), callback);

    }

    public void getInternetPackageList(ResponseCallback<BaseIGashtResponse<InternetPackage>> callback) {

        new ApiInitializer<BaseIGashtResponse<InternetPackage>>().initAPI(api.getInternetPackageList(), callback);

    }

    public void purchaseInternetPackage(String phoneNumber, String internetPackageType, ResponseCallback<MciPurchaseResponse> callback) {

        new ApiInitializer<MciPurchaseResponse>().initAPI(api.internetPackagePurchase(phoneNumber, internetPackageType), callback);

    }
}
