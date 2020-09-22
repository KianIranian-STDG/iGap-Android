package net.iGap.helper;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

public class OkHttpClientInstance {
    private static OkHttpClient instance;

    public static OkHttpClient getInstance() {
        if (instance == null) {
            synchronized (OkHttpClientInstance.class) {
                instance = new OkHttpClient.Builder()
                        .connectTimeout(1, TimeUnit.MINUTES)
                        .writeTimeout(1, TimeUnit.MINUTES)
                        .readTimeout(1, TimeUnit.MINUTES).build();
            }
        }
        return instance;
    }
}
