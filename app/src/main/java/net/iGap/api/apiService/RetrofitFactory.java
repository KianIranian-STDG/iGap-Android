package net.iGap.api.apiService;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitFactory {
    private OkHttpClient httpClient;

    RetrofitFactory() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();

        // TODO for API Logging - clear ir
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        builder.addInterceptor(interceptor);

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


    // TODO clean this comment
/*
    Retrofit getKuknosHorizanRetrofit() {

        OkHttpClient.Builder builder = new OkHttpClient.Builder();

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        builder.addInterceptor(interceptor);

        builder.addInterceptor(chain -> {
            Request original = chain.request();
            Request request = original.newBuilder()
                    .header("Content-Type", "application/json")
                    .method(original.method(), original.body())
                    .build();
            return chain.proceed(request);
        });
        OkHttpClient httpClient = builder.build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApiStatic.KUKNOS_Horizan_Server)
                .addConverterFactory(GsonConverterFactory.create(*//*GsonSingleton.getInstance()*//*))
                .client(httpClient)
                .build();
        return retrofit;
    }*/
}
