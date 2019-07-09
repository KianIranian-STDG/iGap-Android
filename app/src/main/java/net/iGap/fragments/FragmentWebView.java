package net.iGap.fragments;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.webkit.CookieManager;
import android.webkit.GeolocationPermissions;
import android.webkit.PermissionRequest;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.iGap.G;
import net.iGap.R;
import net.iGap.helper.HelperError;
import net.iGap.helper.HelperPermission;
import net.iGap.helper.HelperUrl;
import net.iGap.interfaces.IOnBackPressed;
import net.iGap.interfaces.OnGetPermission;

import java.io.IOException;

public class FragmentWebView extends FragmentToolBarBack implements IOnBackPressed {

    private String url;
    private boolean forceCloseFragment;
    public boolean igRef;
    private WebView webView;
    private TextView webViewError;
    private SwipeRefreshLayout pullToRefresh;
    private FrameLayout frameLayout;
    Handler delayHandler = new Handler();
    Runnable taskMakeVisibleWebViewWithDelay;
    CustomWebViewClient customWebViewClient;
    private View customView;
    private WebChromeClient.CustomViewCallback callback;

    public static FragmentWebView newInstance(String url, boolean igRef, String param) {
        FragmentWebView discoveryFragment = new FragmentWebView();
        Bundle bundle = new Bundle();
        bundle.putString("url", url);
        bundle.putBoolean("igRef", igRef);
        bundle.putString("param", param);
        discoveryFragment.setArguments(bundle);
        return discoveryFragment;
    }

    public static FragmentWebView newInstance(String url) {
        return newInstance(url, true, "");
    }

    @Override
    public void onCreateViewBody(LayoutInflater inflater, LinearLayout root, @Nullable Bundle savedInstanceState) {
        inflater.inflate(R.layout.fragment_my_web_view, root, true);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        forceCloseFragment = false;
        url = getArguments().getString("url");
        igRef = getArguments().getBoolean("igRef");
        String param = getArguments().getString("param");
        if (!url.startsWith("https://") && !url.startsWith("http://")) {
            url = "http://" + url;
        }

        frameLayout = view.findViewById(R.id.full);
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

        if (igRef) {
            pullToRefresh.setEnabled(true);
        } else {
            pullToRefresh.setEnabled(false);
        }

        webViewError.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customWebViewClient.isWebViewVisible = true;
                webView.clearView();
                webView.reload();
                setWebViewVisibleWithDelay();
            }
        });

        if (android.os.Build.VERSION.SDK_INT >= 21) {
            CookieManager.getInstance().setAcceptThirdPartyCookies(webView, true);
        } else {
            CookieManager.getInstance().setAcceptCookie(true);
        }

        titleTextView.setText(G.context.getString(R.string.igap));
        webView.getSettings().setLoadsImagesAutomatically(true);
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        webView.clearCache(true);
        webView.clearHistory();
        webView.clearView();
        webView.clearFormData();
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setDatabaseEnabled(true);
        webView.getSettings().setAppCacheEnabled(true);
        webView.getSettings().setGeolocationEnabled(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setAllowFileAccessFromFileURLs(true);
        webView.getSettings().setAllowUniversalAccessFromFileURLs(true);
        webView.getSettings().setGeolocationDatabasePath(getActivity().getFilesDir().getPath());
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);

        webView.setWebChromeClient(new GeoWebChromeClient());
        customWebViewClient = new CustomWebViewClient();
        webView.setWebViewClient(customWebViewClient);
        if (param != null && param.length() > 1) {
            webView.postUrl(url, param.getBytes());
        } else {
            webView.loadUrl(url);
        }
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
        if (webView.canGoBack() && !forceCloseFragment) {
            webView.clearView();
            webView.goBack();
            customWebViewClient.isWebViewVisible = true;
            setWebViewVisibleWithDelay();
        } else {
            super.onBackButtonClicked(view);
        }
    }

    private class CustomWebViewClient extends WebViewClient {

        public boolean isWebViewVisible = true;

        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            super.onReceivedError(view, request, error);
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
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            if (url.toLowerCase().equals("igap://close")) {
                isWebViewVisible = false;
                forceCloseFragment = true;
                FragmentWebView.this.onBackButtonClicked(view);
            }

        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            if (isWebViewVisible && view != null && view.getTitle() != null && !view.getTitle().contains("صفحه وب در دسترس")) {
                titleTextView.setText(view.getTitle());
            }

        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            boolean a = HelperUrl.handleAppUrl(url);
            if (a) {
                // onBackButtonClicked(view);
            }
            if (url.toLowerCase().equals("igap://close")) {
                forceCloseFragment = true;
                onBackButtonClicked(view);
            }
            return a;
        }
    }

    public class GeoWebChromeClient extends android.webkit.WebChromeClient {
        private boolean remember;
        public GeoWebChromeClient() {
            remember = false;
        }

        @Override
        public void onShowCustomView(View view, CustomViewCallback callback) {
            customView = view;
            FragmentWebView.this.callback = callback;
            getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            frameLayout.addView(view);
            appBarLayout.setVisibility(View.GONE);
            pullToRefresh.setVisibility(View.GONE);
            frameLayout.setVisibility(View.VISIBLE);
            frameLayout.bringToFront();
        }

        @Override
        public void onHideCustomView() {
            if (customView == null)
                return;

            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            customView.setVisibility(View.GONE);
            frameLayout.removeView(customView);
            customView = null;
            frameLayout.setVisibility(View.GONE);
            callback.onCustomViewHidden();
            pullToRefresh.setVisibility(View.VISIBLE);
            appBarLayout.setVisibility(View.VISIBLE);
        }

        @Override
        public void onProgressChanged(WebView view, int progress) {
            if (progress == 100) {
                pullToRefresh.setRefreshing(false);
            } else {
                pullToRefresh.setRefreshing(true);
            }
        }

        @Override
        public void onPermissionRequest(final PermissionRequest request) {
            G.handler.post(new Runnable() {
                @TargetApi(Build.VERSION_CODES.M)
                @Override
                public void run() {
                    if(request.getOrigin().toString().equals("file:///")) {
                        request.grant(request.getResources());
                    } else {
                        request.deny();
                    }
                }
            });
        }

        @Override
        public void onGeolocationPermissionsShowPrompt(final String origin,
                                                       final GeolocationPermissions.Callback callback) {
            super.onGeolocationPermissionsShowPrompt(origin, callback);
            if (remember) {
                getLocation(origin, callback);
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage(getActivity().getString(R.string.location_dialog_message))
                        .setCancelable(true).setPositiveButton(getActivity().getString(R.string.yes), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        getLocation(origin, callback);
                        remember = true;

                    }
                }).setNegativeButton(getActivity().getString(R.string.no), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        callback.invoke(origin, false, false);
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
            }
        }
    }

    private void getLocation(String origin,
                             GeolocationPermissions.Callback callback) {
        try{
            HelperPermission.getLocationPermission(getActivity(), new OnGetPermission() {
                @Override
                public void Allow() throws IOException {
                    callback.invoke(origin, true, false);
                }

                @Override
                public void deny() {
                    callback.invoke(origin, false, false);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
