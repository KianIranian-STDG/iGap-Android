package net.iGap.fragments;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.afollestad.materialdialogs.MaterialDialog;

import net.iGap.G;
import net.iGap.R;
import net.iGap.activities.ActivityMain;
import net.iGap.helper.HelperError;
import net.iGap.helper.HelperPermission;
import net.iGap.helper.HelperToolbar;
import net.iGap.helper.HelperUrl;
import net.iGap.module.WebAppInterface;
import net.iGap.observers.interfaces.IOnBackPressed;
import net.iGap.observers.interfaces.OnGetPermission;
import net.iGap.observers.interfaces.ToolbarListener;

import java.io.IOException;

public class FragmentWebView extends BaseFragment implements IOnBackPressed, ToolbarListener {

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

    private HelperToolbar mHelperToolbar;

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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_my_web_view, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        forceCloseFragment = false;
        setupToolbar(view);
        url = getArguments().getString("url");
        igRef = getArguments().getBoolean("igRef");
        String param = getArguments().getString("param");
        if (!url.startsWith("https://") && !url.startsWith("http://")) {
            url = "http://" + url;
        }
        webViewError = view.findViewById(R.id.webViewError);
        pullToRefresh = view.findViewById(R.id.pullToRefresh);
        frameLayout = view.findViewById(R.id.full);


        pullToRefresh.setOnRefreshListener(() -> {
            if (webView != null) {
                customWebViewClient.isWebViewVisible = true;
                webView.clearView();
                webView.reload();
                setWebViewVisibleWithDelay();
            } else {
                pullToRefresh.setRefreshing(false);
            }
        });

        if (igRef) {
            pullToRefresh.setEnabled(true);
        } else {
            pullToRefresh.setEnabled(false);
        }

        if (webView == null) {
            try {
                webView = new WebView(getContext());
                SwipeRefreshLayout.LayoutParams params = new SwipeRefreshLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                webView.setLayoutParams(params);
                pullToRefresh.addView(webView, params);
            } catch (Exception e) {
                webViewError.setVisibility(View.VISIBLE);
                return;
            }
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
        webView.addJavascriptInterface(new WebAppInterface(getActivity()), "Android");

        webView.setWebChromeClient(new GeoWebChromeClient());
        customWebViewClient = new CustomWebViewClient();
        webView.setWebViewClient(customWebViewClient);
        if (param != null && param.length() > 1) {
            webView.postUrl(url, param.getBytes());
        } else {
            webView.loadUrl(url);
        }
    }

    private void setupToolbar(View view) {

        mHelperToolbar = HelperToolbar.create()
                .setContext(getContext())
                .setLifecycleOwner(getViewLifecycleOwner())
                .setLeftIcon(R.string.back_icon)
                .setLogoShown(true)
                .setRoundBackground(false)
                .setListener(this);

        ViewGroup layoutToolbar = view.findViewById(R.id.fwv_layout_toolbar);
        layoutToolbar.addView(mHelperToolbar.getView());
        mHelperToolbar.setDefaultTitle(G.context.getString(R.string.igap));

    }

    @Override
    public void onLeftIconClickListener(View view) {
        if (webView == null) {
            popBackStackFragment();
            return;
        }

        webView.stopLoading();
        if (webView.canGoBack() && !forceCloseFragment) {
            webView.clearView();
            webView.goBack();
            customWebViewClient.isWebViewVisible = true;
            setWebViewVisibleWithDelay();
        } else {
            popBackStackFragment();
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
        if (webView == null) return false;
        webView.stopLoading();
        if (webView.canGoBack() && !forceCloseFragment) {
            webView.clearView();
            webView.goBack();
            customWebViewClient.isWebViewVisible = true;
            setWebViewVisibleWithDelay();
            return true;
        }
        return false;
    }

    @Override
    public void onPause() {
        super.onPause();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (webView != null) {
            webView.destroy();
        }
    }

    private class CustomWebViewClient extends WebViewClient {

        public boolean isWebViewVisible = true;

        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            super.onReceivedError(view, request, error);
            String errorDescription = (error.getDescription() == null) ? "" : error.getDescription().toString();
            if (!errorDescription.equals("net::ERR_CONNECTION_REFUSED") && !errorDescription.equals("net::ERR_ADDRESS_UNREACHABLE")) {
                //if (url.equals(FragmentWebView.this.url) && isWebViewVisible)
                if (isWebViewVisible) {
                    isWebViewVisible = false;
                    delayHandler.removeCallbacks(taskMakeVisibleWebViewWithDelay);
                    webViewError.setVisibility(View.VISIBLE);
                    webView.setVisibility(View.GONE);
                    mHelperToolbar.setDefaultTitle(G.context.getString(R.string.igap));
                    HelperError.showSnackMessage(G.context.getString(R.string.error), false);
                }
            }
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            if (url.toLowerCase().equals("igap://close")) {
                isWebViewVisible = false;
                forceCloseFragment = true;
                onLeftIconClickListener(view);
            }

        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            if (isWebViewVisible && view != null && view.getTitle() != null && !view.getTitle().contains("صفحه وب در دسترس")) {
                if (view.getTitle().length() > 27) {
                    mHelperToolbar.setDefaultTitle(view.getTitle().substring(0, 27) + "...");
                } else {
                    mHelperToolbar.setDefaultTitle(view.getTitle());
                }
            }

        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (url.toLowerCase().equals("igap://close")) {
                forceCloseFragment = true;
                onLeftIconClickListener(view);
            } else if (url.toLowerCase().contains("igap://deep_link")) {
                if (getActivity() != null && getActivity() instanceof ActivityMain) {
                    ActivityMain activityMain = (ActivityMain) getActivity();
                    activityMain.handleDeepLink(url.replace("igap://deep_link?", ""));
                    return true;
                }
            }

            return HelperUrl.handleAppUrl(getActivity(), url);
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
                    if (request.getOrigin().toString().equals("file:///")) {
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
                if (getActivity() == null) return;
                new MaterialDialog.Builder(getActivity())
                        .content(R.string.location_dialog_message)
                        .positiveText(R.string.yes)
                        .negativeText(R.string.no)
                        .onPositive((dialog, which) -> {
                            getLocation(origin, callback);
                            remember = true;
                        })
                        .onNegative((dialog, which) -> {
                            callback.invoke(origin, false, false);
                        })
                        .show();
            }
        }
    }

    private void getLocation(String origin,
                             GeolocationPermissions.Callback callback) {
        try {
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
