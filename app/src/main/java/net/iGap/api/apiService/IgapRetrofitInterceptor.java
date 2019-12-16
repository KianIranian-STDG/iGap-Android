package net.iGap.api.apiService;

import net.iGap.G;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class IgapRetrofitInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request original = chain.request();
        Request request = original.newBuilder()
                .header("Authorization", G.getApiToken())
                .header("Content-Type", "application/json")
                .method(original.method(), original.body())
                .build();

        return chain.proceed(request);
/*
        Log.d("amini", "intercept: " + response.code() + " " + response.isSuccessful() + " " + response.message());
        if (response.isSuccessful())
            return response;
        else if (response.code() == 401) {
            final boolean[] lag = {true};
            new RequestUserRefreshToken().RefreshUserToken(new OnRefreshToken() {
                @Override
                public void onRefreshToken(String token) {
                    lag[0] = false;
                }

                @Override
                public void onError(int majorCode, int minorCode) {

                }
            });
            Log.d("amini", "intercept: start lag");
            while (lag[0]) {

            }
            Log.d("amini", "intercept: end lag");
        }
        else
            return response;
        return chain.proceed(request);*/
    }
}
