package net.iGap.fragments;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ProgressBar;

import net.iGap.R;
import net.iGap.libs.MyWebViewClient;

public class FragmentWebView extends FragmentToolBarBack {

    private String url;
    private WebView webView;
    private ProgressBar progressWebView;

    public static FragmentWebView newInstance(String url) {
        FragmentWebView discoveryFragment = new FragmentWebView();
        Bundle bundle = new Bundle();
        bundle.putString("url", url);
        discoveryFragment.setArguments(bundle);
        return discoveryFragment;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        url = getArguments().getString("url");
        return attachToSwipeBack(inflater.inflate(R.layout.fragment_my_web_view, container, false));
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        webView = view.findViewById(R.id.webView);
        progressWebView = view.findViewById(R.id.progressWebView);
        webView.loadUrl("");
        webView.getSettings().setLoadsImagesAutomatically(true);
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        webView.clearCache(true);
        webView.clearHistory();
        webView.clearView();
        webView.clearFormData();
        webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);

        webView.setWebChromeClient(new WebChromeClient() {

            @Override
            public void onProgressChanged(WebView view, int progress) {
                if (progress == 100) {
                    progressWebView.setVisibility(View.GONE);
                } else {
                    progressWebView.setVisibility(View.VISIBLE);
                }
            }
        });

        webView.setWebViewClient(new MyWebViewClient() {

            @Override
            protected void onReceivedError(WebView webView, String url, int errorCode, String description) {
            }

            @Override
            protected boolean handleUri(WebView webView, Uri uri) {
                final String host = uri.getHost();
                final String scheme = uri.getScheme();
                // Returning false means that you are going to load this url in the webView itself
                // Returning true means that you need to handle what to do with the url e.g. open web page in a Browser

                // final Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                // startActivity(intent);
                return false;

            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
//                if (url.toLowerCase().equals("igap://close")) {
//                    makeWebViewGone();
//                }
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);

            }
        });

        webView.loadUrl(url);
    }
}
