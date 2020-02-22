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
    private static final int PERSIAN = 0;
    private static final int ENGLISH = 1;
    private static final int RUSSIAN = 2;
    private static final int FRENCH = 3;
    private static final int ARABIAN = 4;
    private static final int KURDISH = 5;
    private static final int AZERBAIJANI = 6;
    private static final int UNKNOWN = -1;

    private String specifications;
    private int lastLang;

    IgapRetrofitInterceptor() {
        lastLang = getCurrentLang();
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
        boolean needToReloadSpec = getCurrentLang() != lastLang;

        if (specifications == null || needToReloadSpec) {
            JsonObject jsonObject = new JsonObject();

            String appId = "2"; //android operating system app id in iGap core
            String appVersion = String.valueOf(BuildConfig.VERSION_CODE);

            jsonObject.addProperty("id", appId);
            jsonObject.addProperty("version", appVersion);
            jsonObject.addProperty("language", lastLang = getCurrentLang());


            specifications = new Gson().toJson(jsonObject);
        }

        return specifications;
    }

    private int getCurrentLang() {
        switch (G.selectedLanguage) {
            case "fa":
                return PERSIAN;
            case "en":
                return ENGLISH;
            case "ru":
                return RUSSIAN;
            case "fr":
                return FRENCH;
            case "ar":
                return ARABIAN;
            case "ur":
                return KURDISH;
            case "iw":
                return AZERBAIJANI;
            default:
                return UNKNOWN;
        }
    }
}
