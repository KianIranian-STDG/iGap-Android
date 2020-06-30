package net.iGap.api;

import net.iGap.api.apiService.IgapRetrofitInterceptor;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Path;
import retrofit2.http.Streaming;

public interface DownloadApi {
    @Headers({IgapRetrofitInterceptor.CONNECT_TIMEOUT + ":20000",
            IgapRetrofitInterceptor.READ_TIMEOUT + ":20000",
            IgapRetrofitInterceptor.WRITE_TIMEOUT + ":20000"})
    @Streaming
    @GET("download/{token}")
    Observable<ResponseBody> downloadData(@Path("token") String token, @Header("userid") String userId);
}
