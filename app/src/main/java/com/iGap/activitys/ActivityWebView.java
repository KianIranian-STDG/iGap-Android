package com.iGap.activitys;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.iGap.G;
import com.iGap.R;
import com.iGap.module.MaterialDesignTextView;

public class ActivityWebView extends ActivityEnhanced {

    private WebView webView;
    private MaterialDesignTextView txtBack, txtShare, txtPopupMenu;
    private ProgressBar progressBar;
    private TextView txtTitle;
    private PopupWindow popupWindow;
    private ViewGroup allLayoutToolbar;
    private SearchView searchView;
    private String key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_webview);

        if (getIntent().getExtras() != null) {
            Bundle bundle = getIntent().getExtras();
            key = bundle.getString("PATH");
        } else {
            key = "FAQ";
        }

        allLayoutToolbar = (ViewGroup) findViewById(R.id.stfaq_allLayout_toolbar);
        searchView = (SearchView) findViewById(R.id.stfaq_searchView);
        txtBack = (MaterialDesignTextView) findViewById(R.id.stfaq_txt_back);
        txtShare = (MaterialDesignTextView) findViewById(R.id.stfaq_txt_share);
        txtPopupMenu = (MaterialDesignTextView) findViewById(R.id.stfaq_txt_menuPopup);

        EditText searchBox = ((EditText) searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text));
        searchBox.setTextColor(getResources().getColor(R.color.white));

        txtBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
            }
        });

        txtShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String shareBody = "Here is the share content body";
                sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "Title");
                sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(sharingIntent, "Share via"));
            }
        });

        webView = (WebView) findViewById(R.id.stfaq_webView);
        if (key.equals("FAQ")) {
            webView.loadUrl(G.FAQ);
        } else {
            webView.loadUrl(G.POLICY);
        }

        webView.getSettings().getJavaScriptEnabled();
        webView.getSettings().getBuiltInZoomControls();

        progressBar = (ProgressBar) findViewById(R.id.stfaq_progressBar);
        int color = getResources().getColor(R.color.gray);
//        progressBar.getIndeterminateDrawable().setColorFilter(color, PorterDuff.Mode.SRC_IN);
        progressBar.getProgressDrawable().setColorFilter(color, PorterDuff.Mode.SRC_IN);
        webView.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                progressBar.setProgress(progress);

                if (progress == 100) {
                    progressBar.setVisibility(View.GONE);

                } else {
                    progressBar.setVisibility(View.VISIBLE);

                }
            }
        });

        final int screenWidth;
        int portrait_landscape = getResources().getConfiguration().orientation;
        if (portrait_landscape == 1) {//portrait
            screenWidth = (int) (getResources().getDisplayMetrics().widthPixels / 1.2);

        } else {
            screenWidth = (int) (getResources().getDisplayMetrics().widthPixels / 1.4);
        }

        txtPopupMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                final View popupView = layoutInflater.inflate(R.layout.popup_faq, null);
                int finalScreen;

                popupWindow = new PopupWindow(popupView, screenWidth, ViewGroup.LayoutParams.WRAP_CONTENT, true);
                popupWindow.setBackgroundDrawable(new BitmapDrawable());
                popupWindow.setOutsideTouchable(true);


                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    popupWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.shadow30, ActivityWebView.this.getTheme()));
                } else {
                    popupWindow.setBackgroundDrawable((getResources().getDrawable(R.drawable.shadow30)));
                }


                if (popupWindow.isOutsideTouchable()) {
                    popupWindow.dismiss();
                    Log.i("CCVVBB", "rr: ");
                }
                popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        //TODO do sth here on dismiss
                    }
                });

                popupWindow.setAnimationStyle(android.R.style.Animation_InputMethod);
                popupWindow.showAtLocation(popupView,
                        Gravity.RIGHT | Gravity.TOP, 0, (int) getResources().getDimension(R.dimen.dp16));

                popupWindow.showAsDropDown(v);
                TextView txtFindPage = (TextView) popupView.findViewById(R.id.popup_faq_findPage);
                txtFindPage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        popupWindow.dismiss();
                        allLayoutToolbar.setVisibility(View.GONE);
                        searchView.setVisibility(View.VISIBLE);
                        searchView.setIconified(false);
                        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                            @Override
                            public boolean onQueryTextSubmit(String query) {
                                return false;
                            }

                            @Override
                            public boolean onQueryTextChange(String newText) {

                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                    webView.findAllAsync(newText);
                                } else {
                                    webView.findAll(newText);
                                }

                                return false;
                            }
                        });
                        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
                            @Override
                            public boolean onClose() {

                                allLayoutToolbar.setVisibility(View.VISIBLE);
                                searchView.setVisibility(View.GONE);
                                return false;
                            }
                        });
                    }
                });
                TextView openChrome = (TextView) popupView.findViewById(R.id.popup_faq_openChrome);
                openChrome.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String urlString;
                        if (key.equals("FAQ")) {
                            urlString = G.FAQ;
                        } else {
                            urlString = G.POLICY;
                        }

                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(urlString));
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.setPackage("com.android.chrome");
                        try {
                            G.context.startActivity(intent);
                        } catch (ActivityNotFoundException ex) {
                            // Chrome browser presumably not installed so allow user to choose instead
                            intent.setPackage(null);
                            G.context.startActivity(intent);
                        }
                    }
                });

                MaterialDesignTextView reload = (MaterialDesignTextView) popupView.findViewById(R.id.popup_faq_reload);
                reload.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        webView.reload();
                        popupWindow.dismiss();
                    }
                });

            }

        });
        txtTitle = (TextView) findViewById(R.id.stfaq_txt_titleToolbar);
        txtTitle.setText(webView.getUrl());


    }

}
