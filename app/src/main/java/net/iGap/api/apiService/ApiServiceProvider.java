package net.iGap.api.apiService;

import net.iGap.api.BeepTunesApi;

public class ApiServiceProvider {
    private static BeepTunesApi beepTunesApi;
    private static RetrofitFactory factory = new RetrofitFactory();

    public static BeepTunesApi getBeepTunesClient() {
        if (beepTunesApi == null) {
            beepTunesApi = factory.getRetrofit(ApiStatic.BEEP_TUNES).create(BeepTunesApi.class);
        }
        return beepTunesApi;
    }

}
