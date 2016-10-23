package com.iGap.activities;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.iGap.R;
import com.iGap.libs.rippleeffect.RippleView;
import com.iGap.module.MaterialDesignTextView;

public class ActivityProfileChannel extends AppCompatActivity {

    private AppBarLayout appBarLayout;
    private TextView txtNameChannel, txtDescription, txtChannelLink, txtPhoneNumber, txtNotifyAndSound, txtDeleteCache, txtLeaveChannel, txtReport;
    private MaterialDesignTextView imgPupupMenul;
    private de.hdodenhof.circleimageview.CircleImageView imgCircleImageView;
    private FloatingActionButton fab;
    private PopupWindow popupWindow;
    private Spannable wordtoSpan;
    private MaterialDesignTextView txtBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_channel);

        txtBack = (MaterialDesignTextView) findViewById(R.id.pch_txt_back);
        final RippleView rippleBack = (RippleView) findViewById(R.id.pch_ripple_back);
        rippleBack.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                finish();
            }
        });
        txtBack.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Flaticon.ttf"));

        appBarLayout = (AppBarLayout) findViewById(R.id.pch_appbar);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {

                TextView titleToolbar = (TextView) findViewById(R.id.pch_txt_titleToolbar);
                if (verticalOffset < -2) {

                    titleToolbar.animate().alpha(1).setDuration(300);
                    titleToolbar.setVisibility(View.VISIBLE);

                } else {
                    titleToolbar.animate().alpha(0).setDuration(500);
                    titleToolbar.setVisibility(View.GONE);
                }
            }
        });
        final int screenWidth = (int) (getResources().getDisplayMetrics().widthPixels / 1.7);
        imgPupupMenul = (MaterialDesignTextView) findViewById(R.id.pch_img_menuPopup);
        RippleView rippleMenu = (RippleView) findViewById(R.id.pch_ripple_menuPopup);
        rippleMenu.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                final View popupView = layoutInflater.inflate(R.layout.popup_window, null);

                popupWindow = new PopupWindow(popupView, screenWidth, ViewGroup.LayoutParams.WRAP_CONTENT, true);
                popupWindow.setBackgroundDrawable(new BitmapDrawable());
                popupWindow.setOutsideTouchable(true);

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
                popupWindow.showAtLocation(popupView, Gravity.RIGHT | Gravity.TOP, 10, 30);
                popupWindow.showAsDropDown(rippleView);
                TextView remove = (TextView) popupView.findViewById(R.id.popup_txtItem1);
                remove.setText("Remove");
                remove.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(ActivityProfileChannel.this, "Remove", Toast.LENGTH_SHORT).show();
                    }
                });

                TextView gone2 = (TextView) popupView.findViewById(R.id.popup_txtItem2);
                gone2.setVisibility(View.GONE);
                TextView gone3 = (TextView) popupView.findViewById(R.id.popup_txtItem3);
                gone3.setVisibility(View.GONE);
                TextView gone4 = (TextView) popupView.findViewById(R.id.popup_txtItem4);
                gone4.setVisibility(View.GONE);

            }
        });

        fab = (FloatingActionButton) findViewById(R.id.pch_fab_addToChannel);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(ActivityProfileChannel.this, "fab", Toast.LENGTH_SHORT).show();
            }
        });

        imgCircleImageView = (de.hdodenhof.circleimageview.CircleImageView) findViewById(R.id.pch_img_circleImage);
        imgCircleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(ActivityProfileChannel.this, "imgCircleImageView", Toast.LENGTH_SHORT).show();
            }
        });
        txtDescription = (TextView) findViewById(R.id.pch_txt_description);
        txtDescription.setMovementMethod(LinkMovementMethod.getInstance());

        String a[] = txtDescription.getText().toString().split(" ");
        SpannableStringBuilder builder = new SpannableStringBuilder();

        for (int i = 0; i < a.length; i++) {
            if (a[i].matches("\\d+")) { //check if only digits. Could also be text.matches("[0-9]+")

                wordtoSpan = new SpannableString(a[i]);
                wordtoSpan.setSpan(new ForegroundColorSpan(Color.BLUE), 0, a[i].length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                wordtoSpan.setSpan(new ClickableSpan() {
                    @Override
                    public void onClick(View v) {
                        TextView tv = (TextView) v;
                        if (tv.getText() instanceof Spannable) {

                            String valuesSpan;
                            Spanned s = (Spanned) tv.getText();
                            int start = s.getSpanStart(this);
                            int end = s.getSpanEnd(this);
                            valuesSpan = s.subSequence(start, end).toString();
                        }
                        new MaterialDialog.Builder(ActivityProfileChannel.this)
                                .items(R.array.phone_profile_chanel)
                                .negativeText("CANCEL")
                                .itemsCallback(new MaterialDialog.ListCallback() {
                                    @Override
                                    public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                        switch (which) {
                                            case 0:
                                                break;
                                            case 1:
                                                break;
                                            case 2:
                                                break;
                                        }
                                    }
                                })
                                .show();
                    }
                }, 0, a[i].length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            } else if (a[i].matches("\\@(\\w+)")) {

                wordtoSpan = new SpannableString(a[i]);
                wordtoSpan.setSpan(new ForegroundColorSpan(Color.BLUE), 0, a[i].length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                wordtoSpan.setSpan(new ClickableSpan() {
                    @Override
                    public void onClick(View v) {
                        String valuesSpan;
                        TextView tv = (TextView) v;
                        if (tv.getText() instanceof Spannable) {
                            Spanned s = (Spanned) tv.getText();
                            int start = s.getSpanStart(this);
                            int end = s.getSpanEnd(this);
                            valuesSpan = s.subSequence(start, end).toString();
                        }
                    }
                }, 0, a[i].length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            } else {
                wordtoSpan = new SpannableString(a[i]);
                wordtoSpan.setSpan(new ForegroundColorSpan(Color.BLACK), 0, a[i].length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }

            builder.append(wordtoSpan).append(" ");

        }
        txtDescription.setText(builder);

        txtChannelLink = (TextView) findViewById(R.id.st_txt_channelLink);
        txtChannelLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(ActivityProfileChannel.this, "txtChannelLink", Toast.LENGTH_SHORT).show();

            }
        });
        txtPhoneNumber = (TextView) findViewById(R.id.st_txt_phoneNumber);
        txtPhoneNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(ActivityProfileChannel.this, "txtPhoneNumber", Toast.LENGTH_SHORT).show();

            }
        });
        txtNotifyAndSound = (TextView) findViewById(R.id.pch_txt_notifyAndSound);
        txtNotifyAndSound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(ActivityProfileChannel.this, "txtNotifyAndSound", Toast.LENGTH_SHORT).show();

            }
        });
        txtDeleteCache = (TextView) findViewById(R.id.pch_txt_deleteCache);
        txtDeleteCache.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(ActivityProfileChannel.this, "txtDeleteCache", Toast.LENGTH_SHORT).show();

            }
        });
        txtLeaveChannel = (TextView) findViewById(R.id.pch_txt_leaveChannel);
        txtLeaveChannel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(ActivityProfileChannel.this, "txtLeaveChannel", Toast.LENGTH_SHORT).show();

            }
        });
        txtReport = (TextView) findViewById(R.id.pch_txt_Report);
        txtReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(ActivityProfileChannel.this, "txtReport", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
