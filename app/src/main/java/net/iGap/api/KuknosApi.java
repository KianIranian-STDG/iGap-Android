package net.iGap.api;

import net.iGap.kuknos.service.model.KuknosInfoM;
import net.iGap.kuknos.service.model.KuknosLoginM;
import net.iGap.kuknos.service.model.KuknosSubmitM;
import net.iGap.kuknos.service.model.KuknoscheckUserM;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface KuknosApi {

    @FormUrlEncoded
    @POST("register")
    Call<KuknosLoginM> getUserVerfication(@Field("phone_number") String phoneNum,
                                          @Field("national_id") String nID);

    @GET("info")
    Call<KuknosInfoM> getUserInfo();

    @FormUrlEncoded
    @POST("check-account")
    Call<KuknoscheckUserM> checkUser(@Field("phone_number") String phoneNum,
                                     @Field("national_id") String nID);

    @FormUrlEncoded
    @POST("register")
    Call<KuknosSubmitM> registerUser(@Field("token") String token,
                                     @Field("public_key") String publicKey,
                                     @Field("friendly_id") String friendlyID);

}
