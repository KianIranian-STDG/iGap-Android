package net.iGap.api.apiService;

import net.iGap.BuildConfig;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitFactory {
    private OkHttpClient httpClient;

    RetrofitFactory() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
            httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            builder.addInterceptor(httpLoggingInterceptor);
        }
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

    Retrofit getBeepTunesRetrofit() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApiStatic.BEEP_TUNES_URL)
                .client(httpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit;

    }

    Retrofit getChannelRetrofit() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApiStatic.CHANNEL_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient)
                .build();
        return retrofit;
    }

    Retrofit getKuknosRetrofit() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApiStatic.KUKNOS_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient)
                .build();
        return retrofit;
    }

    Retrofit getIgasgtRetrofit() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApiStatic.ATI_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient)
                .build();
        return retrofit;
    }
}
