package net.iGap.fragments;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.iGap.G;
import net.iGap.R;
import net.iGap.helper.HelperError;
import net.iGap.interfaces.IOnBackPressed;
import net.iGap.libs.MyWebViewClient;

public class FragmentWebView extends FragmentToolBarBack implements IOnBackPressed {

    private String url;
    private WebView webView;
    private TextView webViewError;
    private SwipeRefreshLayout pullToRefresh;
    Handler delayHandler = new Handler();
    Runnable taskMakeVisibleWebViewWithDelay;
    CustomWebViewClient customWebViewClient;

    public static FragmentWebView newInstance(String url) {
        FragmentWebView discoveryFragment = new FragmentWebView();
        Bundle bundle = new Bundle();
        bundle.putString("url", url);
        discoveryFragment.setArguments(bundle);
        return discoveryFragment;
    }

    @Override
    public void onCreateViewBody(LayoutInflater inflater, LinearLayout root, @Nullable Bundle savedInstanceState) {
        inflater.inflate(R.layout.fragment_my_web_view, root, true);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        url = getArguments().getString("url");
        if (!url.startsWith("https://") && !url.startsWith("http://")) {
            url = "http://" + url;
        }

        webView = view.findViewById(R.id.webView);
        webViewError = view.findViewById(R.id.webViewError);

        pullToRefresh = view.findViewById(R.id.pullToRefresh);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                customWebViewClient.isWebViewVisible = true;
                webView.clearView();
                webView.reload();
                setWebViewVisibleWithDelay();
            }
        });

        webViewError.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customWebViewClient.isWebViewVisible = true;
                webView.clearView();
                webView.reload();
                setWebViewVisibleWithDelay();
            }
        });

        titleTextView.setText(G.context.getString(R.string.igap));
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
                    pullToRefresh.setRefreshing(false);
                } else {
                    pullToRefresh.setRefreshing(true);
                }
            }
        });
        customWebViewClient = new CustomWebViewClient();
        webView.setWebViewClient(customWebViewClient);
        webView.loadUrl(url);
    }

    private void setWebViewVisibleWithDelay() {
        delayHandler.removeCallbacks(taskMakeVisibleWebViewWithDelay);
        taskMakeVisibleWebViewWithDelay = new Runnable() {
            @Override
            public void run() {
                webViewError.setVisibility(View.GONE);
                webView.setVisibility(View.VISIBLE);
            }
        };
        delayHandler.postDelayed(taskMakeVisibleWebViewWithDelay, 500);
    }

    @Override
    public void onStart() {
        super.onStart();
        G.onBackPressedWebView = this;
    }

    @Override
    public void onStop() {
        super.onStop();
        G.onBackPressedWebView = null;
    }

    @Override
    public boolean onBack() {
        webView.stopLoading();
        if (webView.canGoBack()) {
            webView.clearView();
            webView.goBack();
            customWebViewClient.isWebViewVisible = true;
            setWebViewVisibleWithDelay();
            return true;
        }
        return false;
    }

    @Override
    protected void onBackButtonClicked(View view) {
        webView.stopLoading();
        if (webView.canGoBack()) {
            webView.clearView();
            webView.goBack();
            customWebViewClient.isWebViewVisible = true;
            setWebViewVisibleWithDelay();
        } else {
            super.onBackButtonClicked(view);
        }
    }

    private class CustomWebViewClient extends MyWebViewClient {

        public boolean isWebViewVisible = true;

        @Override
        protected void onReceivedError(WebView webView, String url, int errorCode, String description) {
//            if (url.equals(FragmentWebView.this.url) && isWebViewVisible) {
            if (isWebViewVisible) {
                isWebViewVisible = false;
                delayHandler.removeCallbacks(taskMakeVisibleWebViewWithDelay);
                webViewError.setVisibility(View.VISIBLE);
                webView.setVisibility(View.GONE);
                titleTextView.setText(G.context.getString(R.string.igap));
                HelperError.showSnackMessage(G.context.getString(R.string.wallet_error_server), false);
            }
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

        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            if (isWebViewVisible && view != null && view.getTitle() != null && !view.getTitle().contains("صفحه وب در دسترس")) {
                titleTextView.setText(view.getTitle());
            }

        }
    }
}
