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
    private String lastLang;

    IgapRetrofitInterceptor() {
        lastLang = G.selectedLanguage;
    }

    @NotNull
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request original = chain.request();
        Request request = original.newBuilder()
                .header("Authorization", G.getApiToken())
                .header("spec", getSpecifications())
                .header("Content-Type", "application/json")
                .method(original.method(), original.body())
                .build();

        return chain.proceed(request);
    }

    private String getSpecifications() {
        boolean needToReloadSpec = !G.selectedLanguage.equals(lastLang);

        if (specifications == null || needToReloadSpec) {
            JsonObject jsonObject = new JsonObject();

            String appId = "2"; //android operating system app id in iGap core
            String appVersion = String.valueOf(BuildConfig.VERSION_CODE);

            jsonObject.addProperty("id", appId);
            jsonObject.addProperty("version", appVersion);
            jsonObject.addProperty("language", lastLang = G.selectedLanguage);

            specifications = new Gson().toJson(jsonObject);
        }

        return specifications;
    }
}
