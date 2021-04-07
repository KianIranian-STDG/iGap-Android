package net.iGap.api.apiService;

import net.iGap.BuildConfig;
import net.iGap.api.BeepTunesApi;
import net.iGap.api.BillsApi;
import net.iGap.api.CPayApi;
import net.iGap.api.ChargeApi;
import net.iGap.api.CharityApi;
import net.iGap.api.ElecBillApi;
import net.iGap.api.FavoriteChannelApi;
import net.iGap.api.IgashtApi;
import net.iGap.api.NewsApi;
import net.iGap.api.PaymentApi;
import net.iGap.api.StickerApi;

import java.util.Collections;

import okhttp3.ConnectionSpec;
import okhttp3.OkHttpClient;
import okhttp3.TlsVersion;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitFactory {

    public OkHttpClient getHttpClient() {
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

    public BeepTunesApi getBeepTunesRetrofit() {
        return new Retrofit.Builder()
                .baseUrl(ApiStatic.BEEP_TUNES_URL)
                .client(getHttpClient())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(BeepTunesApi.class);
    }

    public FavoriteChannelApi getChannelRetrofit() {
        return new Retrofit.Builder()
                .baseUrl(ApiStatic.CHANNEL_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(getHttpClient())
                .build()
                .create(FavoriteChannelApi.class);
    }

    public PaymentApi getPaymentRetrofit() {
        return new Retrofit.Builder()
                .baseUrl(ApiStatic.PAYMENT_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(getHttpClient())
                .build()
                .create(PaymentApi.class);
    }

    public IgashtApi getIgashtRetrofit() {
        return new Retrofit.Builder()
                .baseUrl(ApiStatic.ATI_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(getHttpClient())
                .build()
                .create(IgashtApi.class);
    }

    public CPayApi getCPayApi() {
        return new Retrofit.Builder()
                .baseUrl(ApiStatic.CPAY_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(getHttpClient())
                .build()
                .create(CPayApi.class);
    }

    public ChargeApi getChargeRetrofit() {
        return new Retrofit.Builder()
                .baseUrl(ApiStatic.CHARGE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(getHttpClient())
                .build()
                .create(ChargeApi.class);
    }

    public CharityApi getCharityRetrofit() {
        return new Retrofit.Builder()
                .baseUrl(ApiStatic.CHARITY_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(getHttpClient())
                .build()
                .create(CharityApi.class);
    }

    public NewsApi getNewsRetrofit() {
        return new Retrofit.Builder()
                .baseUrl(ApiStatic.NEWS_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(getHttpClient())
                .build()
                .create(NewsApi.class);
    }

    public ElecBillApi getElecBillRetrofit() {
        return new Retrofit.Builder()
                .baseUrl(ApiStatic.ELECTRICITY_BILL_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(getHttpClient())
                .build()
                .create(ElecBillApi.class);
    }

    public BillsApi getBillsRetrofit() {
        return new Retrofit.Builder()
                .baseUrl(ApiStatic.BILL_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(getHttpClient())
                .build()
                .create(BillsApi.class);
    }

    public StickerApi getStickerRetrofit() {
        return new Retrofit.Builder()
                .baseUrl(ApiStatic.STICKER_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(getHttpClient())
                .build()
                .create(StickerApi.class);
    }

}
