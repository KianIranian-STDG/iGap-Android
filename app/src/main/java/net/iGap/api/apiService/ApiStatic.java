package net.iGap.api.apiService;

import net.iGap.module.accountManager.AppConfig;

public class ApiStatic {
    static final String VERSION1_1 = "v1.1/";
    static final String STICKER_URL = "https://api.igap.net/sticker/" + VERSION1_1;
    private static final String BASE_URL = "https://api.igap.net/";
    private static final String SERVICES = "services/";
    private static final String VERSION = "v1.0/";
    static final String BEEP_TUNES_URL = BASE_URL + SERVICES + VERSION + "beep-tunes/";
    static final String CHANNEL_URL = BASE_URL + SERVICES + VERSION + "channel/";
    static final String ATI_URL = BASE_URL + "ati-shahr/" + VERSION;
    static final String CHARGE_URL = BASE_URL + "operator-services/" + VERSION;
    static final String CPAY_URL = BASE_URL + SERVICES + VERSION + "ati/c-pay/";
    static final String CHARITY_URL = BASE_URL + SERVICES + VERSION + "charity/";
    static final String ELECTRICITY_BILL_URL = BASE_URL + "bill/" + VERSION + "api/";
    static final String BILL_URL = BASE_URL + "bill-manager/" + VERSION;
    private static final String NEWS_BASE_URL = "https://api.cafetitle.com/";
    static final String NEWS_URL = NEWS_BASE_URL + "";
    public static final String FILE = AppConfig.servicesBaseUrl + "/files/v1.0/";
}

