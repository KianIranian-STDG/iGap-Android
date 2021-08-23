package net.iGap.fragments;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.CookieManager;
import android.webkit.GeolocationPermissions;
import android.webkit.PermissionRequest;
import android.webkit.ValueCallback;
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
import net.iGap.helper.FileLog;
import net.iGap.helper.HelperPermission;
import net.iGap.helper.HelperUrl;
import net.iGap.messenger.ui.toolBar.BackDrawable;
import net.iGap.messenger.ui.toolBar.Toolbar;
import net.iGap.module.WebAppInterface;
import net.iGap.observers.interfaces.IOnBackPressed;
import net.iGap.observers.interfaces.OnGetPermission;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.app.Activity.RESULT_OK;

public class FragmentWebView extends BaseFragment implements IOnBackPressed {

    private final static int FILE_REQUEST_CODE = 1;
    public boolean igRef;
    Handler delayHandler = new Handler();
    Runnable taskMakeVisibleWebViewWithDelay;
    CustomWebViewClient customWebViewClient;
    private String url;
    private boolean forceCloseFragment;
    private WebView webView;
    private TextView webViewError;
    private SwipeRefreshLayout pullToRefresh;
    private FrameLayout frameLayout;
    private View customView;
    private WebChromeClient.CustomViewCallback callback;
    private Toolbar webViewToolbar;
    private ValueCallback<Uri> valueCallback;
    private ValueCallback<Uri[]> valueCallbacks;
    private String photoPath;


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

        pullToRefresh.setEnabled(igRef);

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

        if (Build.VERSION.SDK_INT >= 21) {
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
        webView.getSettings().setSupportZoom(false);
        webView.getSettings().setDatabaseEnabled(true);
        webView.getSettings().setAppCacheEnabled(true);
        webView.getSettings().setGeolocationEnabled(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setAllowFileAccessFromFileURLs(true);
        webView.getSettings().setAllowFileAccess(true);
        webView.getSettings().setAllowContentAccess(true);
        webView.getSettings().setAllowUniversalAccessFromFileURLs(true);
        webView.getSettings().setGeolocationDatabasePath(getActivity().getFilesDir().getPath());
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webView.addJavascriptInterface(new WebAppInterface(getActivity()), "Android");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            webView.getSettings().setMediaPlaybackRequiresUserGesture(false);
        }

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
        webViewToolbar = new Toolbar(getContext());
        webViewToolbar.setBackIcon(new BackDrawable(false));

        ViewGroup layoutToolbar = view.findViewById(R.id.fwv_layout_toolbar);
        layoutToolbar.addView(webViewToolbar);
        webViewToolbar.setListener(i -> {
            if (i == -1) {
                onBackClicked();
            }
        });

    }

    private void onBackClicked() {
        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (Build.VERSION.SDK_INT >= 21) {
            Uri[] results = null;
            if (resultCode == RESULT_OK) {
                if (requestCode == FILE_REQUEST_CODE) {
                    if (null == valueCallbacks) {
                        return;
                    }
                    if (data == null) {
                        if (photoPath != null) {
                            results = new Uri[]{Uri.parse(photoPath)};
                        }
                    } else {
                        String dataString = data.getDataString();
                        if (dataString != null) {
                            results = new Uri[]{Uri.parse(dataString)};
                        } else {
                            results = new Uri[]{Uri.parse(photoPath)};
                        }
                    }
                }
            }
            valueCallbacks.onReceiveValue(results);
            valueCallbacks = null;
        } else {
            if (requestCode == FILE_REQUEST_CODE) {
                if (null == valueCallback) return;
                Uri result = data == null || resultCode != RESULT_OK ? null : data.getData();
                valueCallback.onReceiveValue(result);
                valueCallback = null;
            }
        }
    }

    private void getLocation(String origin, GeolocationPermissions.Callback callback) {
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
            FileLog.e(e);
        }
    }

    private class CustomWebViewClient extends WebViewClient {

        public boolean isWebViewVisible = true;

        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            super.onReceivedError(view, request, error);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            if (url.toLowerCase().equals("igap://close")) {
                isWebViewVisible = false;
                forceCloseFragment = true;
                onBackClicked();
            }

        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            if (isWebViewVisible && view != null && view.getTitle() != null && !view.getTitle().contains("صفحه وب در دسترس")) {
                if (view.getTitle().length() > 27) {
                    webViewToolbar.setTitle(view.getTitle().substring(0, 27) + "...");
                } else {
                    webViewToolbar.setTitle(view.getTitle());
                }
            }
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (url.toLowerCase().equals("igap://close")) {
                forceCloseFragment = true;
                onBackClicked();
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

    private File createImageFile() throws IOException {
        @SuppressLint("SimpleDateFormat") String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "img_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        return File.createTempFile(imageFileName, ".jpg", storageDir);
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
            pullToRefresh.setRefreshing(progress != 100);
        }

        @Override
        public void onPermissionRequest(final PermissionRequest request) {
            G.handler.post(new Runnable() {
                @TargetApi(Build.VERSION_CODES.M)
                @Override
                public void run() {
                    try {
                        if (request.getOrigin().toString().equals("file:///")) {
                            request.grant(request.getResources());
                        } else {
                            final String[] requestedResources = request.getResources();
                            for (String requestResources : requestedResources) {
                                if (requestResources.equals(PermissionRequest.RESOURCE_VIDEO_CAPTURE)) {
                                    HelperPermission.getCameraPermission(G.fragmentActivity, new OnGetPermission() {
                                        @Override
                                        public void Allow() throws IOException {
                                            HelperPermission.getMicroPhonePermission(G.fragmentActivity, new OnGetPermission() {
                                                @Override
                                                public void Allow() throws IOException {
                                                    request.grant(request.getResources());
                                                }

                                                @Override
                                                public void deny() {
                                                }
                                            });
                                        }

                                        @Override
                                        public void deny() {
                                        }
                                    });
                                }
                            }
                        }
                    } catch (IOException e) {
                        FileLog.e(e);
                    }
                }
            });
        }

        //For Android 3.0+
        public void openFileChooser(ValueCallback<Uri> uploadMsg) {
            valueCallback = uploadMsg;
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("*/*");
            getActivity().startActivityForResult(Intent.createChooser(intent, "File Chooser"), FILE_REQUEST_CODE);
        }

        // For Android 3.0+, above method not supported in some android 3+ versions, in such case we use this
        public void openFileChooser(ValueCallback uploadMsg, String acceptType) {
            valueCallback = uploadMsg;
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("*/*");
            getActivity().startActivityForResult(Intent.createChooser(intent, "File Browser"), FILE_REQUEST_CODE);
        }

        //For Android 4.1+
        public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
            valueCallback = uploadMsg;
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("*/*");
            getActivity().startActivityForResult(Intent.createChooser(intent, "File Chooser"), FILE_REQUEST_CODE);
        }

        //For Android 5.0+
        public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, WebChromeClient.FileChooserParams fileChooserParams) {
            try {
                HelperPermission.getStoragePermision(getContext(), new OnGetPermission() {
                    @Override
                    public void Allow() throws IOException {
                        HelperPermission.getCameraPermission(getContext(), new OnGetPermission() {
                            @Override
                            public void Allow() throws IOException {
                                if (valueCallbacks != null) {
                                    valueCallbacks.onReceiveValue(null);
                                }
                                valueCallbacks = filePathCallback;

                                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                                    File photoFile = null;
                                    try {
                                        photoFile = createImageFile();
                                        takePictureIntent.putExtra("PhotoPath", photoPath);
                                    } catch (IOException ex) {
                                        FileLog.e(ex);
                                    }
                                    if (photoFile != null) {
                                        photoPath = "file:" + photoFile.getAbsolutePath();
                                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                                    } else {
                                        takePictureIntent = null;
                                    }
                                }

                                Intent contentSelectionIntent = new Intent(Intent.ACTION_GET_CONTENT);
                                contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE);
                                contentSelectionIntent.setType("*/*");
                                Intent[] intentArray;
                                if (takePictureIntent != null) {
                                    intentArray = new Intent[]{takePictureIntent};
                                } else {
                                    intentArray = new Intent[0];
                                }

                                Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
                                chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent);
                                chooserIntent.putExtra(Intent.EXTRA_TITLE, "Image Chooser");
                                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray);
                                startActivityForResult(chooserIntent, FILE_REQUEST_CODE);
                            }

                            @Override
                            public void deny() {
                            }
                        });
                    }

                    @Override
                    public void deny() {
                    }
                });

            } catch (IOException e) {
                e.printStackTrace();
            }
            return true;
        }

        @Override
        public void onGeolocationPermissionsShowPrompt(final String origin, final GeolocationPermissions.Callback callback) {
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

}
