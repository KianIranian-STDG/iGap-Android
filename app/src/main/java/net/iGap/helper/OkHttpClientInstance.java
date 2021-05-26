package net.iGap.helper;

import net.iGap.BuildConfig;

import java.util.concurrent.TimeUnit;

import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

public class OkHttpClientInstance {
    private static OkHttpClient instance;

    public static OkHttpClient getInstance() {
        if (instance == null) {
            synchronized (OkHttpClientInstance.class) {
                OkHttpClient.Builder builder = new OkHttpClient.Builder()
                        .connectTimeout(1, TimeUnit.MINUTES)
                        .writeTimeout(1, TimeUnit.MINUTES)
                        .readTimeout(1, TimeUnit.MINUTES)
                        .connectionPool(new ConnectionPool(5, 25, TimeUnit.SECONDS));

                if (BuildConfig.DEBUG) {
                    HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
                    httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.HEADERS);
                    builder.addInterceptor(httpLoggingInterceptor);
                }

                instance = builder.build();
            }
        }
        return instance;
    }
}
