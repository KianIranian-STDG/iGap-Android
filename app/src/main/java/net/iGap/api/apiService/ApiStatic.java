package net.iGap.api.apiService;

import net.iGap.module.accountManager.AppConfig;

public class ApiStatic {
    static final String SHAHKAR_URL = "https://api.igap.net/external/v1.0/shahkar/";
    static final String MOBILE_BANK = "http://192.168.8.109:3000/v1.0/";
    static final String WEATHER_URL = "http://192.168.8.66:7000/v1.0/";
    static final String VERSION1_1 = "v1.1/";
    static final String STICKER_URL = "https://api.igap.net/sticker/" + VERSION1_1;
    private static final String BASE_URL = "https://api.igap.net/";
    private static final String SERVICES = "services/";
    private static final String VERSION = "v1.0/";
    static final String BEEP_TUNES_URL = BASE_URL + SERVICES + VERSION + "beep-tunes/";
    static final String CHANNEL_URL = BASE_URL + SERVICES + VERSION + "channel/";
    static final String KUKNOS_URL = BASE_URL + "kuknos/" + VERSION;
    static final String PAYMENT_URL = BASE_URL + SERVICES + VERSION + "payment/";
    static final String ATI_URL = BASE_URL + SERVICES + VERSION + "ati/";
    static final String CHARGE_URL = BASE_URL + "operator-services/" + VERSION;
    static final String CPAY_URL = BASE_URL + SERVICES + VERSION + "ati/c-pay/";
    static final String CHARITY_URL = BASE_URL + SERVICES + VERSION + "charity/";
    static final String ELECTRICITY_BILL_URL = BASE_URL + "bill/" + VERSION + "api/";
    static final String BILL_URL = BASE_URL + "bill-manager/" + VERSION;
    static final String MOBILE_BANK_OTP = BASE_URL + "external/" + VERSION;
    private static final String NEWS_BASE_URL = "https://api.cafetitle.com/";
    static final String NEWS_URL = NEWS_BASE_URL + "";
    public static final String FILE = AppConfig.servicesBaseUrl + "/files/v1.0/";
}

