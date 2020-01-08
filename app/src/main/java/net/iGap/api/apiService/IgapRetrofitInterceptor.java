package net.iGap.api.apiService;

import net.iGap.G;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class IgapRetrofitInterceptor implements Interceptor {
    @NotNull
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request original = chain.request();
        Request request = original.newBuilder()
                .header("Authorization", G.getApiToken())
                .header("Content-Type", "application/json")
                .method(original.method(), original.body())
                .build();

        return chain.proceed(request);
    }
}
