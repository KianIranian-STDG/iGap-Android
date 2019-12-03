package net.iGap.api.apiService;

import net.iGap.BuildConfig;
import net.iGap.api.CPayApi;
import net.iGap.api.CharityApi;

import java.util.Collections;

import okhttp3.ConnectionSpec;
import okhttp3.OkHttpClient;
import okhttp3.TlsVersion;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitFactory {

    private OkHttpClient getHttpClient() {
        OkHttpClient httpClient;
        OkHttpClient.Builder builder = new OkHttpClient.Builder();

        if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
            httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            builder.addInterceptor(httpLoggingInterceptor);
        }
        builder.addInterceptor(new IgapRetrofitInterceptor());

        if (BuildConfig.DEBUG) {
            httpClient = builder.build();
        } else {
            ConnectionSpec spec = new ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
                    .tlsVersions(TlsVersion.TLS_1_2)
                    .build();
            httpClient = builder.connectionSpecs(Collections.singletonList(spec)).build();
        }
        return httpClient;
    }

    Retrofit getBeepTunesRetrofit() {
        return new Retrofit.Builder()
                .baseUrl(ApiStatic.BEEP_TUNES_URL)
                .client(getHttpClient())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

    }

    Retrofit getChannelRetrofit() {
        return new Retrofit.Builder()
                .baseUrl(ApiStatic.CHANNEL_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(getHttpClient())
                .build();
    }

    Retrofit getKuknosRetrofit() {
        return new Retrofit.Builder()
                .baseUrl(ApiStatic.KUKNOS_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(getHttpClient())
                .build();
    }

    public Retrofit getPaymentRetrofit() {
        return new Retrofit.Builder()
                .baseUrl(ApiStatic.PAYMENT_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(getHttpClient())
                .build();
    }

    public Retrofit getIgashtRetrofit() {
        return new Retrofit.Builder()
                .baseUrl(ApiStatic.ATI_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(getHttpClient())
                .build();
    }

    public CPayApi getCPayApi() {
        return new Retrofit.Builder()
                .baseUrl(ApiStatic.CPAY_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(getHttpClient())
                .build()
                .create(CPayApi.class);
    }

    public Retrofit getMciRetrofit() {
        return new Retrofit.Builder()
                .baseUrl(ApiStatic.MCI_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(getHttpClient())
                .build();
    }

    public CharityApi getCharityRetrofit() {
        return new Retrofit.Builder()
                .baseUrl(ApiStatic.CHARITY_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(getHttpClient())
                .build().create(CharityApi.class);
    }

    public Retrofit getNewsRetrofit() {
        return new Retrofit.Builder()
                .baseUrl(ApiStatic.NEWS_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(getHttpClient())
                .build();
    }

    public Retrofit getElecBillRetrofit() {
        return new Retrofit.Builder()
                .baseUrl(ApiStatic.ELECTRICITY_BILL_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(getHttpClient())
                .build();
    }
}
