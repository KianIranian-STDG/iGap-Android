package net.iGap.internetpackage;

import net.iGap.api.MciApi;
import net.iGap.api.apiService.RetrofitFactory;
import net.iGap.api.errorhandler.ErrorHandler;
import net.iGap.api.errorhandler.ResponseCallback;
import net.iGap.igasht.BaseIGashtResponse;

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

    public void getFilterListData(ResponseCallback<MciInternetPackageFilterResponse> callback) {
        api.getInternetPackageFilterList().enqueue(new Callback<MciInternetPackageFilterResponse>() {
            @Override
            public void onResponse(@NotNull Call<MciInternetPackageFilterResponse> call, @NotNull Response<MciInternetPackageFilterResponse> response) {
                if (response.code() == 200) {
                    callback.onSuccess(response.body());
                } else {
                    try {
                        callback.onError(new ErrorHandler().getError(response.code(), response.errorBody().string()));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(@NotNull Call<MciInternetPackageFilterResponse> call, @NotNull Throwable t) {
                t.printStackTrace();
                callback.onFailed(new ErrorHandler().checkHandShakeFailure(t));
            }
        });
    }

    public void getInternetPackageList(ResponseCallback<List<InternetPackage>> callback) {
        api.getInternetPackageList().enqueue(new Callback<BaseIGashtResponse<InternetPackage>>() {
            @Override
            public void onResponse(@NotNull Call<BaseIGashtResponse<InternetPackage>> call, @NotNull Response<BaseIGashtResponse<InternetPackage>> response) {
                if (response.code() == 200) {
                    callback.onSuccess(response.body().getData());
                } else {
                    try {
                        callback.onError(new ErrorHandler().getError(response.code(), response.errorBody().string()));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(@NotNull Call<BaseIGashtResponse<InternetPackage>> call, @NotNull Throwable t) {
                t.printStackTrace();
                callback.onFailed(new ErrorHandler().checkHandShakeFailure(t));
            }
        });
    }
}
