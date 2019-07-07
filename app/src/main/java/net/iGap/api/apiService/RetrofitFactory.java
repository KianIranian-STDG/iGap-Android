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
                    .header("Authorization", "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJ1c2VySWQiOjE5NTMwMDczNzc0OTg1NTQ2MywicGhvbmVOdW1iZXIiOjk4OTMzNzEyMjYxNywic2Vzc2lvbklkIjoyMDM3MDQ1NTIxNjM1ODE5MDYsImlhdCI6MTU2MDk1MzkzMSwiZXhwIjoxNjA3Nzc1MTMyfQ.YV7WVHTCOGnPlkIUfmrNmcxguBI5t1W7xrDvqNqorsIt4lpW6AiQpsT-uDiBYgSWCAFUDRPaGV7n8xFX43rhh0N7CxK5dXFA_-y1kDjf4xkovy_HQf5Gt-feTpsLTM1ETFFy8ucoySKeiHgoRGI2k6uiR63Fx7WE7boPWiNUdes")
                    .header("Content-Type", "application/json")
                    .method(original.method(), original.body())
                    .build();
            return chain.proceed(request);
        });
        httpClient = builder.build();
    }

    Retrofit getRetrofit(String clientType) {
        if (clientType.equals(ApiStatic.BEEP_TUNES)) {
            Gson gson = new GsonBuilder()
                    .setLenient()
                    .create();
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(ApiStatic.BEEP_TUNES_URL)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .client(httpClient)
                    .build();
            return retrofit;

        } else if (clientType.equals(ApiStatic.CHANNEL)) {
            Gson gson = new GsonBuilder()
                    .setLenient()
                    .create();

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(ApiStatic.CHANNEL_URL)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .client(httpClient)
                    .build();
            return retrofit;
        } else
            return null;
    }
}
