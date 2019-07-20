package net.iGap.igasht;

import com.google.gson.GsonBuilder;

import net.iGap.api.IgashtApi;
import net.iGap.api.apiService.ApiServiceProvider;

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

    public void getProvinceList(ResponseCallback<ProvinceListResponse> callback) {
        igashtApi.requestGetProvinceList().enqueue(new Callback<ProvinceListResponse>() {
            @Override
            public void onResponse(@NotNull Call<ProvinceListResponse> call, @NotNull Response<ProvinceListResponse> response) {
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
            public void onFailure(@NotNull Call<ProvinceListResponse> call, @NotNull Throwable t) {
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
        return new GsonBuilder().create().fromJson(error, ErrorModel.class);
    }
}
