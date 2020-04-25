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

    private boolean isRefreshing;

    private TokenContainer tokenContainer = TokenContainer.getInstance();

    IgapRetrofitInterceptor() {
        lastLang = getCurrentLang();
    }

    @NotNull
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request original = chain.request();
        Request request = chain.request();

        Request.Builder builder = request.newBuilder();
        builder.header("Authorization", tokenContainer.getToken());
        builder.header("spec", getSpecifications());
        builder.header("Content-Type", "application/json");
        builder.method(original.method(), original.body());

        String token = tokenContainer.getToken();

        request = builder.build();
        Response response = chain.proceed(request);

        if (response.code() == 401) {
            synchronized (this) {
                String currentToken = tokenContainer.getToken();

                if (currentToken != null && currentToken.equals(token)) {
                    try {
                        getToken();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                if (tokenContainer.getToken() != null) {
                    builder.header("Authorization", tokenContainer.getToken());
                    request = builder.build();
                    return chain.proceed(request);
                }
            }
        }
        return response;
    }

    public synchronized void getToken() throws InterruptedException {
        if (!isRefreshing) {

            isRefreshing = true;

            tokenContainer.getRefreshToken(() -> {
                synchronized (IgapRetrofitInterceptor.this) {
                    isRefreshing = false;
                    IgapRetrofitInterceptor.this.notifyAll();
                }
            });
        }

        this.wait();
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
            jsonObject.addProperty("isDebug", BuildConfig.DEBUG);

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
