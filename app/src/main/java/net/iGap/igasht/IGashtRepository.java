package net.iGap.igasht;

import com.google.gson.GsonBuilder;

import net.iGap.api.IgashtApi;
import net.iGap.api.apiService.ApiServiceProvider;
import net.iGap.igasht.locationdetail.buyticket.IGashtLocationService;
import net.iGap.igasht.provinceselect.IGashtProvince;
import net.iGap.igasht.locationlist.IGashtLocationItem;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class IGashtRepository {

    //singleton
    private static IGashtRepository instance;
    private IgashtApi igashtApi;

    public static IGashtRepository getInstance() {
        if (instance == null) {
            instance = new IGashtRepository();
        }
        return instance;
    }

    private IGashtRepository() {
        igashtApi = ApiServiceProvider.getIgashtClient();
    }

    public void getProvinceList(ResponseCallback<BaseIGashtResponse<IGashtProvince>> callback) {
        igashtApi.requestGetProvinceList().enqueue(new Callback<BaseIGashtResponse<IGashtProvince>>() {
            @Override
            public void onResponse(@NotNull Call<BaseIGashtResponse<IGashtProvince>> call, @NotNull Response<BaseIGashtResponse<IGashtProvince>> response) {
                if (response.code() == 200) {
                    callback.onSuccess(response.body());
                } else {
                    try {
                        callback.onError(getError(response.errorBody().string()));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(@NotNull Call<BaseIGashtResponse<IGashtProvince>> call, @NotNull Throwable t) {
                t.printStackTrace();
                callback.onFailed();
            }
        });
    }

    public void getLocationListWithProvince(int provinceId, ResponseCallback<BaseIGashtResponse<IGashtLocationItem>> callback) {
        igashtApi.requestGetLocationList(provinceId).enqueue(new Callback<BaseIGashtResponse<IGashtLocationItem>>() {
            @Override
            public void onResponse(@NotNull Call<BaseIGashtResponse<IGashtLocationItem>> call, @NotNull Response<BaseIGashtResponse<IGashtLocationItem>> response) {
                if (response.code() == 200) {
                    callback.onSuccess(response.body());
                } else {
                    try {
                        callback.onError(getError(response.errorBody().string()));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(@NotNull Call<BaseIGashtResponse<IGashtLocationItem>> call, @NotNull Throwable t) {
                t.printStackTrace();
                callback.onFailed();
            }
        });
    }

    public void getServiceList(int locationId, ResponseCallback<BaseIGashtResponse<IGashtLocationService>> callback) {
        igashtApi.requestGetServiceList(locationId).enqueue(new Callback<BaseIGashtResponse<IGashtLocationService>>() {
            @Override
            public void onResponse(@NotNull Call<BaseIGashtResponse<IGashtLocationService>> call, @NotNull Response<BaseIGashtResponse<IGashtLocationService>> response) {
                if (response.code() == 200) {
                    callback.onSuccess(response.body());
                } else {
                    try {
                        callback.onError(getError(response.errorBody().string()));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(@NotNull Call<BaseIGashtResponse<IGashtLocationService>> call, @NotNull Throwable t) {
                t.printStackTrace();
                callback.onFailed();
            }
        });
    }

    public interface ResponseCallback<T> {
        void onSuccess(T data);

        void onError(ErrorModel error);

        void onFailed();
    }

    private ErrorModel getError(String error) {
        if (error != null) {
            return new GsonBuilder().create().fromJson(error, ErrorModel.class);
        } else {
            return new ErrorModel("empty error", "error message is null");
        }
    }
}
