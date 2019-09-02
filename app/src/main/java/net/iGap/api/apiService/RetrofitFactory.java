package net.iGap.api.apiService;

import android.os.Build;
import android.util.Log;

import net.iGap.BuildConfig;
import net.iGap.G;
import net.iGap.api.CPayApi;
import net.iGap.api.CharityApi;

import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.SSLContext;

import okhttp3.ConnectionSpec;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.TlsVersion;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitFactory {
    public OkHttpClient httpClient;

    public RetrofitFactory() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();

        if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
            httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            builder.addInterceptor(httpLoggingInterceptor);
        }
        builder.addInterceptor(chain -> {
            Request original = chain.request();
            Request request = original.newBuilder()
                    .header("Authorization", G.getApiToken())
                    .header("Content-Type", "application/json")
                    .method(original.method(), original.body())
                    .build();
            return chain.proceed(request);
        });

        httpClient = enableTls12OnPreLollipop(builder).build();
    }

    Retrofit getBeepTunesRetrofit() {
        return new Retrofit.Builder()
                .baseUrl(ApiStatic.BEEP_TUNES_URL)
                .client(httpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

    }

    Retrofit getChannelRetrofit() {
        return new Retrofit.Builder()
                .baseUrl(ApiStatic.CHANNEL_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient)
                .build();
    }

    Retrofit getKuknosRetrofit() {
        return new Retrofit.Builder()
                .baseUrl(ApiStatic.KUKNOS_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient)
                .build();
    }

    public Retrofit getPaymentRetrofit() {
        return new Retrofit.Builder()
                .baseUrl(ApiStatic.PAYMENT_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient)
                .build();
    }

    public Retrofit getIgashtRetrofit() {
        return new Retrofit.Builder()
                .baseUrl(ApiStatic.ATI_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient)
                .build();
    }

    public CPayApi getCPayApi() {
        return new Retrofit.Builder()
                .baseUrl(ApiStatic.CPAY_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient)
                .build()
                .create(CPayApi.class);
    }

    public Retrofit getMciRetrofit() {
        return new Retrofit.Builder()
                .baseUrl(ApiStatic.MCI_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient)
                .build();
    }

    public CharityApi getCharityRetrofit(){
        return new Retrofit.Builder()
                .baseUrl(ApiStatic.CHARITY_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient)
                .build().create(CharityApi.class);
    }

    private OkHttpClient.Builder enableTls12OnPreLollipop(OkHttpClient.Builder client) {
        if (Build.VERSION.SDK_INT >= 16 && Build.VERSION.SDK_INT < 22) {
            Log.wtf(this.getClass().getName(),"enableTls12OnPreLollipop");
            try {
                SSLContext sc = SSLContext.getInstance("TLSv1.2");
                sc.init(null, null, null);
                client.sslSocketFactory(new Tls12SocketFactory(sc.getSocketFactory()));

                ConnectionSpec cs = new ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
                        .tlsVersions(TlsVersion.TLS_1_2)
                        .build();

                List<ConnectionSpec> specs = new ArrayList<>();
                specs.add(cs);
                specs.add(ConnectionSpec.COMPATIBLE_TLS);
                specs.add(ConnectionSpec.CLEARTEXT);

                client.connectionSpecs(specs);
                Log.wtf(this.getClass().getName(),"enableTls12OnPreLollipop");
            } catch (Exception exc) {
                exc.printStackTrace();
                Log.e("OkHttpTLSCompat", "Error while setting TLS 1.2", exc);
            }
        }

        return client;
    }
}
