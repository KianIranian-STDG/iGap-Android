package net.iGap;

import android.annotation.TargetApi;
import android.net.Uri;
import android.os.Build;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

@SuppressWarnings({"unused", "WeakerAccess"})
public abstract class MyWebViewClient extends WebViewClient {

    // *************************************** Constants *******************************************

    static final String TAG = "MyWebViewClient";

    // *************************************** Override ********************************************

    @SuppressWarnings("deprecation")
    @Override
    public boolean shouldOverrideUrlLoading(WebView webView, String url) {
        final Uri uri = Uri.parse(url);
        return handleUri(webView, uri);
    }

    @TargetApi(Build.VERSION_CODES.N)
    @Override
    public boolean shouldOverrideUrlLoading(WebView webView, WebResourceRequest request) {
        final Uri uri = request.getUrl();
        return handleUri(webView, uri);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onReceivedError(WebView webView, int errorCode, String description, String failingUrl) {
        super.onReceivedError(webView, errorCode, description, failingUrl);
        onReceivedError(webView, failingUrl, errorCode, description);
    }

    @TargetApi(android.os.Build.VERSION_CODES.M)
    @Override
    public void onReceivedError(WebView webView, WebResourceRequest request, WebResourceError error) {
        super.onReceivedError(webView, request, error);
        onReceivedError(webView, request.getUrl().toString(), error.getErrorCode(), error.getDescription().toString());
    }

    protected abstract void onReceivedError(WebView webView, String url, int errorCode, String description);

    @SuppressWarnings("WeakerAccess")
    protected abstract boolean handleUri(WebView webView, final Uri uri);

    // ****************************************** End **********************************************

}
