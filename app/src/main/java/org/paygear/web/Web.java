package org.paygear.web;

import net.iGap.BuildConfig;

import ir.radsense.raadcore.Raad;
import ir.radsense.raadcore.web.WebBase;
import okhttp3.Request;

/**
 * Created by Software1 on 8/12/2017.
 */

public class Web extends WebBase<WebService> {

    public static final String API_KEY = BuildConfig.WEB_BASE_API_KEY;
    public static String token;
    private static Web mInstance;

    private Web(Class<WebService> webInterfaceType) {
        super(webInterfaceType);
    }

    public synchronized static Web getInstance() {
        if (mInstance != null) {
            return mInstance;
        }
        return new Web(WebService.class);
    }

    public void release() {
        mInstance = null;
    }


    @Override
    protected void onSetHeaders(Request.Builder requestBuilder) {
        super.onSetHeaders(requestBuilder);
        if (Raad.language == null)
            Raad.language = "fa";

        requestBuilder.addHeader("Accept-Language", Raad.language);
    }
}
