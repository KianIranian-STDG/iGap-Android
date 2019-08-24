package net.iGap.api.repository;

import android.util.Log;

import net.iGap.api.CPayApi;
import net.iGap.api.apiService.RetrofitFactory;
import net.iGap.api.errorhandler.ErrorHandler;
import net.iGap.api.errorhandler.ResponseCallback;
import net.iGap.model.cPay.RegisterPlaqueBodyModel;
import net.iGap.model.cPay.RegisterPlaqueModel;

import org.jetbrains.annotations.NotNull;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CPayRepository {

    private static CPayRepository instance ;
    private CPayApi api ;

    private CPayRepository() {
        api = new RetrofitFactory().getCPayApi();
    }

    public static CPayRepository getInstance(){
        if(instance == null){
            instance = new CPayRepository();
        }
        return instance;
    }

    public void registerNewPlaque(RegisterPlaqueBodyModel body , ResponseCallback<RegisterPlaqueModel> callback){

        api.getRegisterNewPlaque(body).enqueue(new Callback<RegisterPlaqueModel>() {
            @Override
            public void onResponse(@NotNull Call<RegisterPlaqueModel> call, @NotNull Response<RegisterPlaqueModel> response) {
                if (response.isSuccessful()){
                    callback.onSuccess(response.body());
                }else {
                    try{
                        callback.onError(new ErrorHandler().getError(response.code() , response.errorBody().string()));
                    }catch (Exception e){
                        Log.e("errorr" , "ee" , e);
                    }
                }
            }

            @Override
            public void onFailure(Call<RegisterPlaqueModel> call, Throwable t) {
                callback.onFailed(new ErrorHandler().checkHandShakeFailure(t));

            }
        });

    }
}
