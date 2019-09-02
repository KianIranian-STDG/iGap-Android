package net.iGap.api.apiService;

public class ApiStatic {
    private static final String BASE_URL = "http://192.168.10.134:3000/v1.0/";//todo:// revert it
//    private static final String BASE_URL = "https://api.igap.net/services/v1.0/";
//    private static final String BASE_URL = "http://192.168.10.156:7000/v1.0/";
    static final String BEEP_TUNES_URL = BASE_URL + "beep-tunes/";
    static final String CHANNEL_URL = BASE_URL + "channel/";
    static final String KUKNOS_URL = BASE_URL + "kuknos/";
    static final String PAYMENT_URL = BASE_URL + "payment/";
    static final String ATI_URL = BASE_URL + "ati/";
    static final String CPAY_URL = BASE_URL + "ati/c-pay/";
    static final String MCI_URL = BASE_URL + "mci/";
    static final String CHARITY_URL = BASE_URL + "charity/";
}

