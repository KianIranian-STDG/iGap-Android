package com.iGap.activitys;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.graphics.PorterDuff;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.v7.widget.SearchView;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.iGap.G;
import com.iGap.R;
import com.iGap.adapter.AdapterDialog;
import com.iGap.helper.HelperString;
import com.iGap.interface_package.OnInfoCountryResponse;
import com.iGap.interface_package.OnSmsReceive;
import com.iGap.interface_package.OnUserLogin;
import com.iGap.interface_package.OnUserRegistration;
import com.iGap.interface_package.OnUserVerification;
import com.iGap.module.CountryListComparator;
import com.iGap.module.CountryReader;
import com.iGap.module.IncomingSms;
import com.iGap.module.SoftKeyboard;
import com.iGap.module.StructCountry;
import com.iGap.proto.ProtoRequest;
import com.iGap.proto.ProtoUserRegister;
import com.iGap.proto.ProtoUserVerify;
import com.iGap.realm.RealmUserInfo;
import com.iGap.request.RequestInfoCountry;
import com.iGap.request.RequestQueue;
import com.iGap.request.RequestUserLogin;
import com.iGap.request.RequestWrapper;
import com.vicmikhailau.maskededittext.MaskedEditText;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.realm.Realm;

public class ActivityRegister extends ActivityEnhanced {

    private SoftKeyboard softKeyboard;

    private Button btnStart;
    public static Button btnChoseCountry;
    public static EditText edtCodeNumber;

    public static MaskedEditText edtPhoneNumber;

    private TextView txtAgreement_register, txtTitleToolbar, txtTitleRegister, txtDesc, txtTitleAgreement;
    private ProgressBar rg_prg_verify_connect, rg_prg_verify_sms, rg_prg_verify_generate, rg_prg_verify_register;
    private TextView rg_txt_verify_connect, rg_txt_verify_sms, rg_txt_verify_generate, rg_txt_verify_register, txtTimer;
    private ImageView rg_img_verify_connect, rg_img_verify_sms, rg_img_verify_generate, rg_img_verify_register;
    private ViewGroup layout_agreement;
    private ViewGroup layout_verify;

    private String phoneNumber;
    public static String isoCode = "IR";
    private String regex;
    private String userName;
    private String token;
    private String regexFetchCodeVerification;

    private long userId;

    private boolean newUser;

    ArrayList<StructCountry> structCountryArrayList = new ArrayList();  //Array List for Store List of StructCountry Object

    private ArrayList<StructCountry> items = new ArrayList<>();
    private AdapterDialog adapterDialog;

    private Dialog dialogVerifyLandScape;
    private IncomingSms smsReceiver;

    private CountDownTimer countDownTimer;
    public static Dialog dialogChooseCountry;
    private SearchView edtSearchView;

    public static int positionRadioButton = -1;

    private Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        int getHeight = G.context.getResources().getDisplayMetrics().heightPixels;

        txtTitleRegister = (TextView) findViewById(R.id.rg_txt_title_register);
        txtTitleRegister.setTypeface(G.arial);
        txtDesc = (TextView) findViewById(R.id.rg_txt_text_descRegister);
        txtDesc.setTypeface(G.arial);
        txtTitleAgreement = (TextView) findViewById(R.id.rg_txt_title_agreement);
        txtTitleAgreement.setTypeface(G.arial);

        txtTitleToolbar = (TextView) findViewById(R.id.rg_txt_titleToolbar);
        txtTitleToolbar.setTypeface(G.FONT_IGAP);

        edtPhoneNumber = (MaskedEditText) findViewById(R.id.rg_edt_PhoneNumber);
        edtPhoneNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {


            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().equals("0")) {
                    Toast.makeText(ActivityRegister.this, "نیازی به 0 اول نیست", Toast.LENGTH_SHORT).show();
                    edtPhoneNumber.setText("");
                }
            }
        });

        edtCodeNumber = (EditText) findViewById(R.id.rg_edt_CodeNumber);

        layout_agreement = (ViewGroup) findViewById(R.id.rg_layout_agreement);
        layout_verify = (ViewGroup) findViewById(R.id.rg_layout_verify_and_agreement);

        btnChoseCountry = (Button) findViewById(R.id.rg_btn_choseCountry);
        btnChoseCountry.setTypeface(G.arial);

        int portrait = getResources().getConfiguration().orientation;

        if (portrait == 1) {
            txtAgreement_register = (TextView) findViewById(R.id.txtAgreement_register);
            txtAgreement_register.setMovementMethod(new ScrollingMovementMethod());
            txtAgreement_register.setTypeface(G.arial);
        }

//==================================================================================================== read list of county from text file
//        list of country

        CountryReader countryReade = new CountryReader();
        StringBuilder fileListBuilder = countryReade.readFromAssetsTextFile("country.txt", this);

        String list = fileListBuilder.toString();
        // Split line by line Into array
        String listArray[] = list.split("\\r?\\n");
        final String countryNameList[] = new String[listArray.length];
        //Convert array
        for (int i = 0; listArray.length > i; i++) {
            StructCountry structCountry = new StructCountry();

            String listItem[] = listArray[i].split(";");
            structCountry.setCountryCode(listItem[0]);
            structCountry.setAbbreviation(listItem[1]);
            structCountry.setName(listItem[2]);

            if (listItem.length > 3) {
                structCountry.setPhonePattern(listItem[3]);
            } else {
                structCountry.setPhonePattern(" ");
            }

            structCountryArrayList.add(structCountry);
        }

        Collections.sort(structCountryArrayList, new CountryListComparator());

        for (int i = 0; i < structCountryArrayList.size(); i++) {

            countryNameList[i] = structCountryArrayList.get(i).getName();
            StructCountry item = new StructCountry();
            item.setId(i);
            item.setName(structCountryArrayList.get(i).getName());
            item.setCountryCode(structCountryArrayList.get(i).getCountryCode());
            item.setPhonePattern(structCountryArrayList.get(i).getPhonePattern());
            item.setAbbreviation(structCountryArrayList.get(i).getAbbreviation());
            items.add(item);
        }

        btnChoseCountry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialogChooseCountry = new Dialog(ActivityRegister.this);
                dialogChooseCountry.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialogChooseCountry.setContentView(R.layout.rg_dialog);

                int setWidth = (int) (getResources().getDisplayMetrics().widthPixels * 0.9);
                int setHeight = (int) (getResources().getDisplayMetrics().heightPixels * 0.9);
                dialogChooseCountry.getWindow().setLayout(setWidth, setHeight);
//
                final TextView txtTitle = (TextView) dialogChooseCountry.findViewById(R.id.rg_txt_titleToolbar);
                edtSearchView = (SearchView) dialogChooseCountry.findViewById(R.id.rg_edtSearch_toolbar);

                txtTitle.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        edtSearchView.setIconified(false);
//                        edtSearchView.onActionViewExpanded();
                        edtSearchView.setIconifiedByDefault(true);
                        txtTitle.setVisibility(View.GONE);
                    }
                });

                edtSearchView.setOnCloseListener(new SearchView.OnCloseListener() { // close SearchView and show title again
                    @Override
                    public boolean onClose() {

                        txtTitle.setVisibility(View.VISIBLE);

                        return false;
                    }
                });

                final ViewGroup root = (ViewGroup) dialogChooseCountry.findViewById(R.id.rg_layoutRoot_dialog);
                InputMethodManager im = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                SoftKeyboard softKeyboard = new SoftKeyboard(root, im);
                softKeyboard.setSoftKeyboardCallback(new SoftKeyboard.SoftKeyboardChanged() {
                    @Override
                    public void onSoftKeyboardHide() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (edtSearchView.getQuery().toString().length() > 0) {

                                    edtSearchView.setIconified(false);
//                                    edtSearchView.onActionViewExpanded();
                                    txtTitle.setVisibility(View.GONE);
                                } else {

                                    edtSearchView.setIconified(true);
//                                    edtSearchView.onActionViewCollapsed();
                                    txtTitle.setVisibility(View.VISIBLE);

                                }
                                adapterDialog.notifyDataSetChanged();
                            }
                        });
                    }

                    @Override
                    public void onSoftKeyboardShow() {

                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {

                                txtTitle.setVisibility(View.GONE);

                            }
                        });
                    }
                });

                final ListView listView = (ListView) dialogChooseCountry.findViewById(R.id.lstContent);
                adapterDialog = new AdapterDialog(ActivityRegister.this, items);
                listView.setAdapter(adapterDialog);

                final View border = (View) dialogChooseCountry.findViewById(R.id.rg_borderButton);
                listView.setOnScrollListener(new AbsListView.OnScrollListener() {
                    @Override
                    public void onScrollStateChanged(AbsListView absListView, int i) {

                    }

                    @Override
                    public void onScroll(AbsListView absListView, int i, int i1, int i2) {

                        if (i > 0) {
                            border.setVisibility(View.VISIBLE);
                        } else {
                            border.setVisibility(View.GONE);

                        }

                    }
                });

                AdapterDialog.mSelectedVariation = positionRadioButton;

                adapterDialog.notifyDataSetChanged();

                edtSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String s) {

                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String s) {

                        adapterDialog.getFilter().filter(s);
                        return false;
                    }
                });

                TextView btnCancel = (TextView) dialogChooseCountry.findViewById(R.id.rg_txt_cancelDialog);
                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        dialogChooseCountry.dismiss();

                    }
                });

                TextView btnOk = (TextView) dialogChooseCountry.findViewById(R.id.rg_txt_okDialog);
                btnOk.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        G.onInfoCountryResponse = new OnInfoCountryResponse() {
                            @Override
                            public void onInfoCountryResponse(final int callingCode, final String name, final String pattern, final String regexR) {
                                G.handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        edtCodeNumber.setText("+" + callingCode);
                                        edtPhoneNumber.setMask(pattern.replace("X", "#").replace(" ", "-"));
                                        regex = regexR;
                                        Log.i("SOC_INFO", "onInfoCountryResponse regex : " + regex);
                                        btnStart.setEnabled(true);
                                        Toast.makeText(G.context, "info country received", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        };

                        btnStart.setEnabled(false);
                        new RequestInfoCountry().infoCountry(isoCode);
                        G.handler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(G.context, "waiting for get info country", Toast.LENGTH_SHORT).show();
                            }
                        });

                        edtPhoneNumber.setText("");
                        dialogChooseCountry.dismiss();
                    }
                });

                dialogChooseCountry.show();


            }
        });

//=============================================================================================================== click button for start verify

        final Animation trans_x_in = AnimationUtils.loadAnimation(G.context, R.anim.rg_tansiton_y_in);
        final Animation trans_x_out = AnimationUtils.loadAnimation(G.context, R.anim.rg_tansiton_y_out);
        btnStart = (Button) findViewById(R.id.rg_btn_start); //check phone and internet connection
        btnStart.setTypeface(G.arial);
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (edtPhoneNumber.getText().toString().length() > 0 && !regex.equals("") && edtPhoneNumber.getText().toString().replace("-", "").matches(regex)) {


                    phoneNumber = edtPhoneNumber.getText().toString();

                    MaterialDialog dialog = new MaterialDialog.Builder(ActivityRegister.this)
                            .customView(R.layout.rg_mdialog_text, true)
                            .positiveText("OK")
                            .negativeText("EDIT")
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                                    int portaret_landscope = getResources().getConfiguration().orientation;

                                    if (portaret_landscope == 1) {//portrait
                                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                                        txtAgreement_register = (TextView) findViewById(R.id.txtAgreement_register);
                                        txtAgreement_register.setMovementMethod(new ScrollingMovementMethod());

                                        layout_agreement.setVisibility(View.GONE);
                                        layout_agreement.startAnimation(trans_x_out);
                                        G.handler.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {

                                                btnChoseCountry.setEnabled(false);
                                                btnChoseCountry.setTextColor(getResources().getColor(R.color.rg_border_editText));
                                                edtPhoneNumber.setEnabled(false);
                                                edtPhoneNumber.setTextColor(getResources().getColor(R.color.rg_border_editText));

                                                edtCodeNumber.setEnabled(false);
                                                edtCodeNumber.setTextColor(getResources().getColor(R.color.rg_border_editText));

                                                layout_verify.setVisibility(View.VISIBLE);
                                                layout_verify.startAnimation(trans_x_in);

                                                checkVerify();

                                            }
                                        }, 600);
                                    } else {


                                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);


                                        dialogVerifyLandScape = new Dialog(ActivityRegister.this);

                                        btnChoseCountry.setTextColor(getResources().getColor(R.color.rg_background_editText));
                                        btnChoseCountry.setEnabled(false);
                                        edtPhoneNumber.setTextColor(getResources().getColor(R.color.rg_background_editText));
                                        edtPhoneNumber.setEnabled(false);

                                        dialogVerifyLandScape.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                        dialogVerifyLandScape.setContentView(R.layout.rg_dialog_verify_land);
                                        dialogVerifyLandScape.setCanceledOnTouchOutside(false);
                                        dialogVerifyLandScape.show();

                                        checkVerify();
                                    }

                                }
                            })
                            .build();


                    View view = dialog.getCustomView();
                    assert view != null;
                    TextView phone = (TextView) view.findViewById(R.id.rg_dialog_txt_number);
                    phone.setText(edtPhoneNumber.getText().toString());
                    dialog.show();

                } else {
                    Toast.makeText(ActivityRegister.this, "Please Enter your Phone Number", Toast.LENGTH_SHORT).show();
                }
            }
        });
        // enable scroll text view

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            isoCode = extras.getString("ISO_CODE");
            edtCodeNumber.setText("+" + extras.getInt("CALLING_CODE"));
            btnChoseCountry.setText(extras.getString("COUNTRY_NAME"));
            String pattern = extras.getString("PATTERN");
            if (!pattern.equals("")) {
                edtPhoneNumber.setMask(pattern.replace("X", "#").replace(" ", "-"));
            }
            regex = extras.getString("REGEX");
            String body = extras.getString("TERMS_BODY");
            if (body != null & txtAgreement_register != null) { //TODO [Saeed Mozaffari] [2016-09-01 9:28 AM] - txtAgreement_register !=null is wrong. change it
                txtAgreement_register.setText(Html.fromHtml(body));
            }

        }

        int portrait_landscape = getResources().getConfiguration().orientation;
        if (portrait_landscape == 1) {//portrait

            if (getHeight > 480) {

                int marginLeft = (int) getResources().getDimension(R.dimen.dp32);
                int marginRight = (int) getResources().getDimension(R.dimen.dp32);
                int marginTopStart = (int) getResources().getDimension(R.dimen.dp20);
                int marginTopChooseCountry = 0;
                int marginBottomChooseCountry = (int) getResources().getDimension(R.dimen.dp8);
                int marginBottomStart = 0;

                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) btnChoseCountry.getLayoutParams();
                params.setMargins(marginLeft, marginTopChooseCountry, marginRight, marginBottomChooseCountry); //left, top, right, bottom
                btnChoseCountry.setLayoutParams(params);
//
//                ViewGroup li = (ViewGroup) findViewById(R.id.rg_layout_center);
//                RelativeLayout.LayoutParams params3 = (RelativeLayout.LayoutParams) li.getLayoutParams();
//                params3.setMargins(marginLeft, marginTopChooseCountry, marginRight, 400); //left, top, right, bottom
//                li.setLayoutParams(params3);


                RelativeLayout.LayoutParams params2 = (RelativeLayout.LayoutParams) btnStart.getLayoutParams();
                params2.setMargins(marginLeft, marginTopStart, marginRight, marginBottomStart); //left, top, right, bottom
                btnStart.setLayoutParams(params2);
            }

        }


    }

    //======= process verify : check internet and sms
    private void checkVerify() {

        setItem(); // invoke object

        rg_prg_verify_connect.setVisibility(View.VISIBLE);

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {

                if (G.socketConnection) { //connection ok
//                        if (checkInternet()) { //connection ok
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            userRegister();
                            btnStart.setEnabled(false);
                            countDownTimer = new CountDownTimer(1000 * 60, 1000) { // wait for verify sms

                                TextView txtTimerLand;

                                public void onTick(long millisUntilFinished) {

                                    int seconds = (int) ((millisUntilFinished) / 1000);
                                    int minutes = seconds / 60;
                                    seconds = seconds % 60;

                                    int portrait_landscape = getResources().getConfiguration().orientation;
                                    if (portrait_landscape == 1) {//portrait
                                        txtTimer = (TextView) findViewById(R.id.rg_txt_verify_timer);
                                        txtTimer.setVisibility(View.VISIBLE);
                                        txtTimer.setText("" + String.format("%02d", minutes) + ":" + String.format("%02d", seconds));
                                    } else {
                                        txtTimerLand = (TextView) dialogVerifyLandScape.findViewById(R.id.rg_txt_verify_timer_DialogLand);
                                        txtTimer.setVisibility(View.VISIBLE);
                                        txtTimerLand.setText("" + String.format("%02d", minutes) + ":" + String.format("%02d", seconds));
                                    }

                                }

                                public void onFinish() {
                                    int portrait_landscape = getResources().getConfiguration().orientation;
                                    if (portrait_landscape == 1) {//portrait
                                        txtTimer.setText("00:00");
                                        txtTimer.setVisibility(View.VISIBLE);
                                    } else {
                                        txtTimerLand.setText("00:00");
                                        txtTimer.setVisibility(View.VISIBLE);
                                    }
                                    errorVerifySms(); // open rg_dialog for enter sms code
                                }
                            };

                        }
                    });

                } else { // connection error
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            edtPhoneNumber.setEnabled(true);
                            rg_prg_verify_connect.setVisibility(View.GONE);
                            rg_img_verify_connect.setImageResource(R.mipmap.alert);
                            rg_img_verify_connect.setColorFilter(getResources().getColor(R.color.rg_error_red), PorterDuff.Mode.SRC_ATOP);
                            rg_img_verify_connect.setVisibility(View.VISIBLE);
                            rg_txt_verify_connect.setTextColor(getResources().getColor(R.color.rg_error_red));
                            rg_txt_verify_connect.setText("Please check your connection");
                        }
                    });
                }
            }
        });
        thread.start();

    }

    private void setItem() { //invoke object

        int portrait_landscape = getResources().getConfiguration().orientation; //check for portrait & landScape
        if (portrait_landscape == 1) {//portrait
            rg_prg_verify_connect = (ProgressBar) findViewById(R.id.rg_prg_verify_connect);
            rg_txt_verify_connect = (TextView) findViewById(R.id.rg_txt_verify_connect);
            rg_img_verify_connect = (ImageView) findViewById(R.id.rg_img_verify_connect);

            rg_prg_verify_sms = (ProgressBar) findViewById(R.id.rg_prg_verify_sms);
            rg_txt_verify_sms = (TextView) findViewById(R.id.rg_txt_verify_sms);
            rg_img_verify_sms = (ImageView) findViewById(R.id.rg_img_verify_sms);

            rg_prg_verify_generate = (ProgressBar) findViewById(R.id.rg_prg_verify_key);
            rg_txt_verify_generate = (TextView) findViewById(R.id.rg_txt_verify_key);
            rg_img_verify_generate = (ImageView) findViewById(R.id.rg_img_verify_key);

            rg_prg_verify_register = (ProgressBar) findViewById(R.id.rg_prg_verify_server);
            rg_txt_verify_register = (TextView) findViewById(R.id.rg_txt_verify_server);
            rg_img_verify_register = (ImageView) findViewById(R.id.rg_img_verify_server);
        } else {
            rg_prg_verify_connect = (ProgressBar) dialogVerifyLandScape.findViewById(R.id.rg_prg_verify_connect_DialogLand);
            rg_txt_verify_connect = (TextView) dialogVerifyLandScape.findViewById(R.id.rg_txt_verify_connect_DialogLand);
            rg_img_verify_connect = (ImageView) dialogVerifyLandScape.findViewById(R.id.rg_img_verify_connect_DialogLand);

            rg_prg_verify_sms = (ProgressBar) dialogVerifyLandScape.findViewById(R.id.rg_prg_verify_sms_DialogLand);
            rg_txt_verify_sms = (TextView) dialogVerifyLandScape.findViewById(R.id.rg_txt_verify_sms_DialogLand);
            rg_img_verify_sms = (ImageView) dialogVerifyLandScape.findViewById(R.id.rg_img_verify_sms_DialogLand);

            rg_prg_verify_generate = (ProgressBar) findViewById(R.id.rg_prg_verify_key_DialogLand);
            rg_txt_verify_generate = (TextView) findViewById(R.id.rg_txt_verify_key_DialogLand);
            rg_img_verify_generate = (ImageView) findViewById(R.id.rg_img_verify_key_DialogLand);

            rg_prg_verify_register = (ProgressBar) findViewById(R.id.rg_prg_verify_server_DialogLand);
            rg_txt_verify_register = (TextView) findViewById(R.id.rg_txt_verify_server_DialogLand);
            rg_img_verify_register = (ImageView) findViewById(R.id.rg_img_verify_server_DialogLand);
        }
    }

    private boolean checkInternet() { //check internet //TODO [Saeed Mozaffari] [2016-08-24 10:27 AM] -this check internet method

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();

        if (networkInfo == null) {
            return true;
        } else {
            return true;
        }
    }

    //=================================================================================================== error verify sms and open rg_dialog for enter sms code
    private void errorVerifySms() { //when don't receive sms and open rg_dialog for enter code

        rg_prg_verify_sms.setVisibility(View.GONE);
        rg_img_verify_sms.setImageResource(R.mipmap.alert);
        rg_img_verify_sms.setVisibility(View.VISIBLE);
        rg_img_verify_sms.setColorFilter(getResources().getColor(R.color.rg_error_red), PorterDuff.Mode.SRC_ATOP);
        rg_txt_verify_sms.setText("Error verification SMS");
        rg_txt_verify_sms.setTextColor(getResources().getColor(R.color.rg_error_red));

        dialog = new Dialog(ActivityRegister.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.rg_dialog_verify_code);
        dialog.setCanceledOnTouchOutside(false);

        final EditText edtEnterCodeVerify = (EditText) dialog.findViewById(R.id.rg_edt_dialog_verifyCode); //EditText For Enter sms cod

        TextView btnCancel = (TextView) dialog.findViewById(R.id.rg_btn_cancelVerifyCode);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userVerify(userName, edtEnterCodeVerify.getText().toString());
                dialog.dismiss();
            }
        });

        TextView btnOk = (TextView) dialog.findViewById(R.id.rg_btn_dialog_okVerifyCode);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userRegister();
                edtPhoneNumber.setText("");
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void userRegister() {

        G.onUserRegistration = new OnUserRegistration() {

            @Override
            public void onRegister(final String userNameR, final long userIdR, final ProtoUserRegister.UserRegisterResponse.Method methodValue, final List<Long> smsNumbersR, String regex) {
                countDownTimer.start();
                regexFetchCodeVerification = regex;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        txtTimer.setVisibility(View.VISIBLE);
                        userName = userNameR;
                        userId = userIdR;
                        G.smsNumbers = smsNumbersR;

                        if (methodValue == ProtoUserRegister.UserRegisterResponse.Method.VERIFY_CODE_SMS) {//verification with sms

                        } else if (methodValue == ProtoUserRegister.UserRegisterResponse.Method.VERIFY_CODE_SOCKET) {//verification with socket

                            errorVerifySms(); // open rg_dialog for enter sms code


                        } else if (methodValue == ProtoUserRegister.UserRegisterResponse.Method.VERIFY_CODE_SMS_SOCKET) {//verification with sms and socket

                        }

                        rg_prg_verify_connect.setVisibility(View.GONE);
                        rg_img_verify_connect.setVisibility(View.VISIBLE);
                        rg_txt_verify_connect.setTextColor(getResources().getColor(R.color.rg_text_verify));

                        rg_prg_verify_sms.setVisibility(View.VISIBLE);
                        rg_txt_verify_sms.setTextAppearance(G.context, R.style.RedHUGEText);
                        //getVerificationSms();
                    }
                });
            }

            @Override
            public void onRegisterError() {
                requestRegister();
            }
        };

        requestRegister();
    }

    private void requestRegister() {

        phoneNumber = phoneNumber.replace("-", "");
        ProtoUserRegister.UserRegister.Builder builder = ProtoUserRegister.UserRegister.newBuilder();
        builder.setCountryCode(isoCode);
        builder.setPhoneNumber(Long.parseLong(phoneNumber));
        builder.setRequest(ProtoRequest.Request.newBuilder().setId(HelperString.generateKey()));
        RequestWrapper requestWrapper = new RequestWrapper(100, builder);

        try {
            RequestQueue.sendRequest(requestWrapper);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

//        G.handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//
//            }
//        }, 2000);
    }

    private void userVerify(final String userName, final String verificationCode) {
        rg_prg_verify_generate.setVisibility(View.VISIBLE);
        rg_txt_verify_generate.setTextAppearance(G.context, R.style.RedHUGEText);

        userVerifyResponse(verificationCode);

        ProtoUserVerify.UserVerify.Builder userVerify = ProtoUserVerify.UserVerify.newBuilder();
        userVerify.setCode(Integer.parseInt(verificationCode));
        userVerify.setUsername(userName);
        userVerify.setDevice(getResources().getBoolean(R.bool.isTablet) ? "Tablet" : "Mobile");
        userVerify.setOsName("android");
        userVerify.setOsVersion(Integer.toString(android.os.Build.VERSION.SDK_INT));

        RequestWrapper requestWrapper = new RequestWrapper(101, userVerify);
        try {
            RequestQueue.sendRequest(requestWrapper);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

//        G.handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//
//            }
//        }, 4000);
    }

    private void userVerifyResponse(final String verificationCode) {
        G.onUserVerification = new OnUserVerification() {
            @Override
            public void onUserVerify(final String tokenR, final boolean newUserR) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        rg_txt_verify_sms.setText("Your login code is : " + verificationCode);
                        rg_prg_verify_sms.setVisibility(View.GONE);
                        rg_img_verify_sms.setVisibility(View.VISIBLE);
                        rg_img_verify_sms.setImageResource(R.mipmap.check);
                        rg_img_verify_sms.setColorFilter(getResources().getColor(R.color.rg_text_verify), PorterDuff.Mode.SRC_ATOP);
                        rg_txt_verify_sms.setTextColor(getResources().getColor(R.color.rg_start_background));

                        newUser = newUserR;
                        token = tokenR;
                        rg_prg_verify_generate.setVisibility(View.GONE);
                        rg_img_verify_generate.setVisibility(View.VISIBLE);
                        rg_txt_verify_generate.setTextColor(getResources().getColor(R.color.rg_text_verify));

                        userLogin(token);
                    }
                });
            }

            @Override
            public void onUserVerifyError() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        errorVerifySms();
                    }
                });
            }
        };
    }

    private void userLogin(final String token) {
        rg_prg_verify_register.setVisibility(View.VISIBLE);
        rg_txt_verify_register.setTextAppearance(G.context, R.style.RedHUGEText);
        G.onUserLogin = new OnUserLogin() {
            @Override
            public void onLogin() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Realm realm = Realm.getDefaultInstance();
                        realm.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                RealmUserInfo userInfo = realm.createObject(RealmUserInfo.class);
                                userInfo.setUserId(userId);
                                userInfo.setUserName(userName);
                                userInfo.setCountryISOCode(isoCode);
                                userInfo.setPhoneNumber(phoneNumber);
                                userInfo.setToken(token);
                                userInfo.setUserRegistrationState(true);
                                G.importContact();
                            }
                        });

                        rg_prg_verify_register.setVisibility(View.GONE);
                        rg_img_verify_register.setVisibility(View.VISIBLE);
                        rg_txt_verify_register.setTextColor(getResources().getColor(R.color.rg_text_verify));

                        Class<?> className;
                        if (newUser) {
                            className = ActivityProfile.class;
                        } else {
                            className = ActivityMain.class;
                        }
                        realm.close();
                        Intent intent = new Intent(G.context, className);
                        startActivity(intent);
                        finish();
                    }
                });
            }

            @Override
            public void onLoginError() {
                requestLogin();
            }
        };
        requestLogin();
    }

    private void requestLogin() {
        new RequestUserLogin().userLogin(token);
//        G.handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//
//            }
//        }, 2000);
    }

    private void receiveVerifySms(String message) {

        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }

        String verificationCode = HelperString.regexExtractValue(message, regexFetchCodeVerification);
        countDownTimer.cancel(); //cancel method CountDown and continue process verify

        rg_prg_verify_sms.setVisibility(View.GONE);
        rg_img_verify_sms.setVisibility(View.VISIBLE);
        rg_txt_verify_sms.setTextColor(getResources().getColor(R.color.rg_start_background));
        userVerify(userName, verificationCode);
    }

    @Override
    protected void onResume() {
        final IntentFilter filter = new IntentFilter();
        filter.addAction("android.provider.Telephony.SMS_RECEIVED");

        smsReceiver = new IncomingSms(new OnSmsReceive() {

            @Override
            public void onSmsReceive(String message) {
                try {
                    if (message != null && !message.isEmpty() && !message.equals("null") && !message.equals("")) {
                        rg_txt_verify_sms.setText(message);
                        receiveVerifySms(message);
                    }
                } catch (Exception e1) {
                    e1.getStackTrace();
                }
            }
        });

        registerReceiver(smsReceiver, filter);
        super.onResume();
    }

    @Override
    protected void onPause() {
        try {
            unregisterReceiver(smsReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void getVerificationSms() { //TODO [Saeed Mozaffari] [2016-08-22 10:48 AM] - this method is fake and will be removed later
        rg_prg_verify_sms.setVisibility(View.VISIBLE);
        rg_txt_verify_generate.setTextAppearance(G.context, R.style.RedHUGEText);
        G.handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                rg_prg_verify_sms.setVisibility(View.GONE);
                rg_img_verify_sms.setVisibility(View.VISIBLE);
                rg_txt_verify_sms.setTextColor(getResources().getColor(R.color.rg_start_background));

                receiveVerifySms("Your login code is : 12345 This code can be used to login to your account.");
            }
        }, 4000);
    }
}
