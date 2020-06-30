package net.iGap.api;

import net.iGap.api.apiService.IgapRetrofitInterceptor;
import net.iGap.model.UploadData;

import io.reactivex.Single;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface UploadsApi {
//    @Multipart
//    @POST("/upload/file.jpg")
//    Single<ResponseBody> postImage(@HeaderMap Map<String, String> headers, @Part MultipartBody.Part image);

    @FormUrlEncoded
    @POST("init/{token}")
    Call<UploadData> initUpload(@Path("token") String token,
                                @Field("size") String fileSize,
                                @Field("name") String fileName,
                                @Field("extension") String fileExtension,
                                @Field("room_id") String roomID,
                                @Header(IgapRetrofitInterceptor.USER_ID) String userID);

    @Headers({IgapRetrofitInterceptor.CONNECT_TIMEOUT + ":20000",
            IgapRetrofitInterceptor.READ_TIMEOUT + ":20000",
            IgapRetrofitInterceptor.WRITE_TIMEOUT + ":20000"})
    @POST("upload/{token}")
    @Multipart
    Single<ResponseBody> uploadData(@Path("token") String token,
                                    @Part MultipartBody.Part body,
                                    @Header("Content-Extension") String content_type,
                                    @Header(IgapRetrofitInterceptor.USER_ID) String userID);

    @Headers({IgapRetrofitInterceptor.CONNECT_TIMEOUT + ":20000",
            IgapRetrofitInterceptor.READ_TIMEOUT + ":20000",
            IgapRetrofitInterceptor.WRITE_TIMEOUT + ":20000"})
    @POST("upload/{token}")
    Single<ResponseBody> uploadDataReqBody(@Path("token") String token,
                                           @Body RequestBody image,
                                           @Header("Content-Extension") String content_type,
                                           @Header(IgapRetrofitInterceptor.USER_ID) String userID);
}
