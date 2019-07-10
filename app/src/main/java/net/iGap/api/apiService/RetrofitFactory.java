package net.iGap.api.apiService;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitFactory {
    private OkHttpClient httpClient;

    RetrofitFactory() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.addInterceptor(chain -> {
            Request original = chain.request();
            Request request = original.newBuilder()
                    .header("Authorization", ApiStatic.USER_TOKEN)
                    .header("Content-Type", "application/json")
                    .method(original.method(), original.body())
                    .build();
            return chain.proceed(request);
        });
        httpClient = builder.build();
    }

    Retrofit getRetrofit(String clientType) {
        if (clientType.equals(ApiStatic.BEEP_TUNES)) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(ApiStatic.BEEP_TUNES_URL)
                    .client(httpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            return retrofit;

        } else if (clientType.equals(ApiStatic.CHANNEL)) {

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(ApiStatic.CHANNEL_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(httpClient)
                    .build();
            return retrofit;
        } else
            return null;
    }
}
