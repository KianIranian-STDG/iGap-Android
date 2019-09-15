package net.iGap.api.repository;

import androidx.lifecycle.MutableLiveData;

import net.iGap.api.CPayApi;
import net.iGap.api.apiService.RetrofitFactory;
import net.iGap.api.errorhandler.ErrorHandler;
import net.iGap.api.errorhandler.ResponseCallback;
import net.iGap.model.cPay.CPayWalletAmountModel;
import net.iGap.model.cPay.ChargeWalletBodyModel;
import net.iGap.model.cPay.ChargeWalletModel;
import net.iGap.model.cPay.PlaqueBodyModel;
import net.iGap.model.cPay.PlaqueInfoModel;
import net.iGap.model.cPay.RegisterPlaqueBodyModel;
import net.iGap.model.cPay.RegisterPlaqueModel;
import net.iGap.model.cPay.UserPlaquesModel;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CPayRepository {

    private static CPayRepository instance;
    private CPayApi api;
    private MutableLiveData<Boolean> plaquesChangeListener = new MutableLiveData<>();

    private CPayRepository() {
        api = new RetrofitFactory().getCPayApi();
    }

    public static CPayRepository getInstance() {
        if (instance == null) {
            instance = new CPayRepository();
        }
        return instance;
    }

    public void getPlaqueInfo(PlaqueBodyModel body, ResponseCallback<PlaqueInfoModel> callback) {

        api.getPlaqueInfo(body).enqueue(new Callback<PlaqueInfoModel>() {
            @Override
            public void onResponse(Call<PlaqueInfoModel> call, Response<PlaqueInfoModel> response) {
                if (response.isSuccessful()) {
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
            public void onFailure(Call<PlaqueInfoModel> call, Throwable t) {
                callback.onFailed(new ErrorHandler().checkHandShakeFailure(t));
            }
        });

    }

    public void getAllUserPlaques(ResponseCallback<UserPlaquesModel> callback) {

        api.getUserPlaques().enqueue(new Callback<UserPlaquesModel>() {
            @Override
            public void onResponse(Call<UserPlaquesModel> call, Response<UserPlaquesModel> response) {
                if (response.isSuccessful()) {
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
            public void onFailure(Call<UserPlaquesModel> call, Throwable t) {
                callback.onFailed(new ErrorHandler().checkHandShakeFailure(t));
            }
        });

    }

    public void registerNewPlaque(RegisterPlaqueBodyModel body, ResponseCallback<RegisterPlaqueModel> callback) {

        api.getRegisterNewPlaque(body).enqueue(new Callback<RegisterPlaqueModel>() {
            @Override
            public void onResponse(@NotNull Call<RegisterPlaqueModel> call, @NotNull Response<RegisterPlaqueModel> response) {
                if (response.isSuccessful()) {
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
            public void onFailure(Call<RegisterPlaqueModel> call, Throwable t) {
                callback.onFailed(new ErrorHandler().checkHandShakeFailure(t));

            }
        });

    }

    public void getWalletAmount(PlaqueBodyModel body, ResponseCallback<CPayWalletAmountModel> callback) {

        api.getCPayWalletAmount(body).enqueue(new Callback<CPayWalletAmountModel>() {
            @Override
            public void onResponse(Call<CPayWalletAmountModel> call, Response<CPayWalletAmountModel> response) {
                if (response.isSuccessful()) {
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
            public void onFailure(Call<CPayWalletAmountModel> call, Throwable t) {
                callback.onFailed(new ErrorHandler().checkHandShakeFailure(t));
            }
        });
    }

    public void getChargeWallet(ChargeWalletBodyModel body, ResponseCallback<ChargeWalletModel> callback) {

        api.getChargeWallet(body).enqueue(new Callback<ChargeWalletModel>() {
            @Override
            public void onResponse(Call<ChargeWalletModel> call, Response<ChargeWalletModel> response) {
                if (response.isSuccessful()) {
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
            public void onFailure(Call<ChargeWalletModel> call, Throwable t) {
                callback.onFailed(new ErrorHandler().checkHandShakeFailure(t));
            }
        });
    }

    public MutableLiveData<Boolean> getPlaquesChangeListener() {
        return plaquesChangeListener;
    }

}
