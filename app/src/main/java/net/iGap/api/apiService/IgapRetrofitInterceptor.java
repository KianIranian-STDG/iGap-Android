package net.iGap.api.apiService;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import net.iGap.BuildConfig;
import net.iGap.G;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class IgapRetrofitInterceptor implements Interceptor {
    private String specifications;

    IgapRetrofitInterceptor() {
        JsonObject jsonObject = new JsonObject();
        String appId = "2";
        jsonObject.addProperty("appId", appId);
        String appVersion = String.valueOf(BuildConfig.VERSION_CODE);
        jsonObject.addProperty("appVersion", appVersion);
        specifications = new Gson().toJson(jsonObject);
    }

    @NotNull
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request original = chain.request();
        Request request = original.newBuilder()
                .header("Authorization", G.getApiToken())
                .header("specifications", specifications)
                .header("Content-Type", "application/json")
                .method(original.method(), original.body())
                .build();

        return chain.proceed(request);
    }
}
