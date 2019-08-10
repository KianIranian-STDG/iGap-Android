package net.iGap.api;

import net.iGap.kuknos.service.model.KuknosInfoM;
import net.iGap.kuknos.service.model.KuknosLoginM;

import org.stellar.sdk.responses.AccountResponse;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface KuknosHorizenApi {

    @GET("accounts/{id}")
    Call<AccountResponse> getUserAccount(@Path("id") String userID);

}
