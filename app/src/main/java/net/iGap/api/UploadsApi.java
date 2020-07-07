package net.iGap.api;

import net.iGap.api.apiService.IgapRetrofitInterceptor;
import net.iGap.model.UploadData;

import io.reactivex.Observable;
import io.reactivex.Single;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Streaming;

public interface UploadsApi {

    @FormUrlEncoded
    @POST("init/{token}")
    Call<UploadData> initUpload(@Path("token") String token,
                                @Field("size") String fileSize,
                                @Field("name") String fileName,
                                @Field("extension") String fileExtension,
                                @Field("room_id") String roomID,
                                @Header(IgapRetrofitInterceptor.USER_ID) String userID);

    /**
     * UPLOAD data with multipart body
     *
     * @param token
     * @param body
     * @param userID
     * @return
     */
    @POST("upload/{token}")
    @Multipart
    Single<ResponseBody> uploadData(@Path("token") String token,
                                    @Part MultipartBody.Part body,
                                    @Header(IgapRetrofitInterceptor.USER_ID) String userID);

    /**
     * upload data with Body and rx java method
     *
     * @param token
     * @param image
     * @param userID
     * @return
     */
    @POST("upload/{token}")
    Single<ResponseBody> uploadDataReqBody(@Path("token") String token,
                                           @Body RequestBody image,
                                           @Header(IgapRetrofitInterceptor.USER_ID) String userID);

    /**
     * upload data with body and call method
     *
     * @param token
     * @param image
     * @param userID
     * @return
     */
    @POST("upload/{token}")
    Call<ResponseBody> uploadDataReqBodyCall(@Path("token") String token,
                                             @Body RequestBody image,
                                             @Header(IgapRetrofitInterceptor.USER_ID) String userID);


    @POST("dec/token")
    Call<ResponseBody> test(@Body RequestBody image);

    @Streaming
    @GET("download/{token}")
    Observable<ResponseBody> downloadData(@Path("token") String token);
}
