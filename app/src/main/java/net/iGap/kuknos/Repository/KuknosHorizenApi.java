package net.iGap.kuknos.Repository;

import org.stellar.sdk.responses.AccountResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface KuknosHorizenApi {

    @GET("accounts/{id}")
    Call<AccountResponse> getUserAccount(@Path("id") String userID);

}
