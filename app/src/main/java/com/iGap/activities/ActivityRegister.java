package com.iGap.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.graphics.PorterDuff;
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
import com.iGap.helper.HelperLogout;
import com.iGap.helper.HelperPermision;
import com.iGap.helper.HelperString;
import com.iGap.interfaces.OnGetPermision;
import com.iGap.interfaces.OnInfoCountryResponse;
import com.iGap.interfaces.OnSmsReceive;
import com.iGap.interfaces.OnUserInfoResponse;
import com.iGap.interfaces.OnUserLogin;
import com.iGap.interfaces.OnUserRegistration;
import com.iGap.interfaces.OnUserVerification;
import com.iGap.module.CountryListComparator;
import com.iGap.module.CountryReader;
import com.iGap.module.IncomingSms;
import com.iGap.module.SoftKeyboard;
import com.iGap.module.StructCountry;
import com.iGap.proto.ProtoGlobal;
import com.iGap.proto.ProtoRequest;
import com.iGap.proto.ProtoUserRegister;
import com.iGap.proto.ProtoUserVerify;
import com.iGap.realm.RealmRegisteredInfo;
import com.iGap.realm.RealmUserInfo;
import com.iGap.realm.RealmUserInfoFields;
import com.iGap.request.RequestInfoCountry;
import com.iGap.request.RequestQueue;
import com.iGap.request.RequestUserInfo;
import com.iGap.request.RequestUserLogin;
import com.iGap.request.RequestWrapper;
import com.vicmikhailau.maskededittext.MaskedEditText;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.realm.Realm;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ActivityRegister extends ActivityEnhanced {

    static final String KEY_SAVE_CODENUMBER = "SAVE_CODENUMBER";
    static final String KEY_SAVE_PHONENUMBER_MASK = "SAVE_PHONENUMBER_MASK";
    static final String KEY_SAVE_PHONENUMBER_NUMBER = "SAVE_PHONENUMBER_NUMBER";
    static final String KEY_SAVE_NAMECOUNTRY = "SAVE_NAMECOUNTRY";
    public static Button btnChoseCountry;
    public static EditText edtCodeNumber;
    public static MaskedEditText edtPhoneNumber;
    public static String isoCode = "IR";
    public static TextView btnOk;
    public static Dialog dialogChooseCountry;
    public static int positionRadioButton = -1;
    ArrayList<StructCountry> structCountryArrayList = new ArrayList();
    private SoftKeyboard softKeyboard;
    private Button btnStart;
    private TextView txtAgreement_register, txtTitleToolbar, txtTitleRegister, txtDesc, txtTitleAgreement;
    private ProgressBar rg_prg_verify_connect, rg_prg_verify_sms, rg_prg_verify_generate, rg_prg_verify_register;
    private TextView rg_txt_verify_connect, rg_txt_verify_sms, rg_txt_verify_generate, rg_txt_verify_register, txtTimer;
    private ImageView rg_img_verify_connect, rg_img_verify_sms, rg_img_verify_generate, rg_img_verify_register;
    private ViewGroup layout_agreement;
    private ViewGroup layout_verify;
    private String phoneNumber;
    //Array List for Store List of StructCountry Object
    private String regex;
    private String userName;
    private String token;
    private String regexFetchCodeVerification;
    private long userId;
    private boolean newUser;
    private ArrayList<StructCountry> items = new ArrayList<>();
    private AdapterDialog adapterDialog;
    private Dialog dialogVerifyLandScape;
    private IncomingSms smsReceiver;
    private CountDownTimer countDownTimer;
    private SearchView edtSearchView;
    private Dialog dialog;
    private int digitCount;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        final IntentFilter filter = new IntentFilter();
        filter.addAction("android.provider.Telephony.SMS_RECEIVED");

        smsReceiver = new IncomingSms(new OnSmsReceive() {

            @Override
            public void onSmsReceive(String message) {
                Log.i("UUU", "onSmsReceive 1 message : " + message);
                try {
                    if (message != null && !message.isEmpty() && !message.equals("null") && !message.equals("")) {
                        Log.i("UUU", "onSmsReceive 2");
                        rg_txt_verify_sms.setText(message);
                        receiveVerifySms(message);
                    }
                } catch (Exception e1) {
                    e1.getStackTrace();
                }
            }
        });

        Log.e("dddd", "ddddddddddddddddddddddddddddddddddddddd");

        HelperPermision.getSmsPermision(ActivityRegister.this, new OnGetPermision() {
            @Override
            public void Allow() {
                registerReceiver(smsReceiver, filter);
            }
        });


        edtCodeNumber = (EditText) findViewById(R.id.rg_edt_CodeNumber);
        btnChoseCountry = (Button) findViewById(R.id.rg_btn_choseCountry);

        edtPhoneNumber = (MaskedEditText) findViewById(R.id.rg_edt_PhoneNumber);

        int portrait = getResources().getConfiguration().orientation;
        if (portrait == 1) {
            txtAgreement_register = (TextView) findViewById(R.id.txtAgreement_register);
            txtAgreement_register.setMovementMethod(new ScrollingMovementMethod());
        }
        if (savedInstanceState != null) {
            // Restore value of members from saved state
            edtCodeNumber.setText(savedInstanceState.getString(KEY_SAVE_CODENUMBER));
            edtPhoneNumber.setMask(savedInstanceState.getString(KEY_SAVE_PHONENUMBER_MASK));
            edtPhoneNumber.setText(savedInstanceState.getString(KEY_SAVE_PHONENUMBER_NUMBER));
            btnChoseCountry.setText(savedInstanceState.getString(KEY_SAVE_NAMECOUNTRY));
        } else {
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
                if (body != null & txtAgreement_register != null) { //TODO [Saeed Mozaffari] [2016-09-01 9:28 AM] -
                    // txtAgreement_register !=null is wrong. change it
                    txtAgreement_register.setText(Html.fromHtml(body));
                }
            }
        }

        int getHeight = G.context.getResources().getDisplayMetrics().heightPixels;
        Log.i("AZAZ", "onCreate: " + getHeight);

        txtTitleRegister = (TextView) findViewById(R.id.rg_txt_title_register);
        txtDesc = (TextView) findViewById(R.id.rg_txt_text_descRegister);
        txtTitleAgreement = (TextView) findViewById(R.id.rg_txt_title_agreement);

        txtTitleToolbar = (TextView) findViewById(R.id.rg_txt_titleToolbar);
        txtTitleToolbar.setTypeface(G.FONT_IGAP);

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
                    Toast.makeText(ActivityRegister.this, getResources().getString(R.string.Toast_First_0), Toast.LENGTH_SHORT).show();
                    edtPhoneNumber.setText("");
                }
            }
        });

        layout_agreement = (ViewGroup) findViewById(R.id.rg_layout_agreement);
        layout_verify = (ViewGroup) findViewById(R.id.rg_layout_verify_and_agreement);

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

                final ViewGroup root = (ViewGroup) dialogChooseCountry.findViewById(android.R.id.content);
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
                                    edtSearchView.clearFocus();
                                    txtTitle.setVisibility(View.GONE);
                                } else {
                                    edtSearchView.setIconified(true);
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

                //                TextView btnCancel = (TextView) dialogChooseCountry
                // .findViewById(R.id.rg_txt_cancelDialog);
                //                btnCancel.setOnClickListener(new View.OnClickListener() {
                //                    @Override
                //                    public void onClick(View view) {
                //
                //                        dialogChooseCountry.dismiss();
                //
                //                    }
                //                });

                btnOk = (TextView) dialogChooseCountry.findViewById(R.id.rg_txt_okDialog);
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

                            @Override
                            public void onError(int majorCode, int minorCode) {
                                if (majorCode == 501 && minorCode == 1) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            // TODO: 9/25/2016 Error 501 - INFO_COUNTRY_BAD_PAYLOAD
                                        }
                                    });
                                }
                            }
                        };

                        new RequestInfoCountry().infoCountry(isoCode);

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
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (edtPhoneNumber.getText().toString().length() > 0 && !regex.equals("") && edtPhoneNumber.getText().toString().replace("-", "").matches(regex)) {

                    phoneNumber = edtPhoneNumber.getText().toString();

                    MaterialDialog dialog = new MaterialDialog.Builder(ActivityRegister.this)
                            .customView(R.layout.rg_mdialog_text, true)
                            .positiveText(getResources().getString(R.string.B_ok))
                            .negativeText(getResources().getString(R.string.B_edit))
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
                    phone.setText(edtCodeNumber.getText().toString() + "" + edtPhoneNumber.getText().toString());
                    dialog.show();
                } else {

                    new MaterialDialog.Builder(ActivityRegister.this).title(R.string.phone_number).content(R.string.Toast_Enter_Phone_Number).positiveText(R.string.B_ok).show();
                }
            }
        });
        // enable scroll text view

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
                //                RelativeLayout.LayoutParams params3 = (RelativeLayout
                // .LayoutParams) li.getLayoutParams();
                //                params3.setMargins(marginLeft, marginTopChooseCountry,
                // marginRight, 400); //left, top, right, bottom
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

    // error verify sms and open rg_dialog for enter sms code
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

                if (!edtEnterCodeVerify.getText().toString().equals("")) {
                    userVerify(userName, edtEnterCodeVerify.getText().toString());
                    dialog.dismiss();
                } else {

                    new MaterialDialog.Builder(ActivityRegister.this).title(R.string.Enter_Code).content(R.string.Toast_Enter_Code).positiveText(R.string.B_ok).show();
                }
            }
        });

        TextView btnOk = (TextView) dialog.findViewById(R.id.rg_btn_dialog_okVerifyCode);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userRegister();
                dialog.dismiss();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
        });
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        if (dialog.isShowing()) {
            countDownTimer.cancel();
        }
    }

    private void userRegister() {

        G.onUserRegistration = new OnUserRegistration() {

            @Override
            public void onRegister(final String userNameR, final long userIdR, final ProtoUserRegister.UserRegisterResponse.Method methodValue, final List<Long> smsNumbersR, String regex,
                                   int verifyCodeDigitCount) {
                digitCount = verifyCodeDigitCount;
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
                            countDownTimer.cancel();
                        } else if (methodValue == ProtoUserRegister.UserRegisterResponse.Method.VERIFY_CODE_SMS_SOCKET) {//verification with sms and socket

                        }

                        rg_prg_verify_connect.setVisibility(View.GONE);
                        rg_img_verify_connect.setVisibility(View.VISIBLE);
                        rg_txt_verify_connect.setTextAppearance(G.context, R.style.RedHUGEText);
                        rg_txt_verify_connect.setTextColor(getResources().getColor(R.color.rg_text_verify));

                        rg_prg_verify_sms.setVisibility(View.VISIBLE);
                        rg_txt_verify_sms.setTextAppearance(G.context, R.style.RedHUGEText);
                        //getVerificationSms();
                    }
                });
            }

            @Override
            public void onRegisterError(int majorCode, int minorCode) {

                if (majorCode == 100 && minorCode == 1) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // TODO: 9/25/2016  Error 100 - USER_REGISTER_BAD_PAYLOAD
                            //Invalid countryCode
                        }
                    });
                } else if (majorCode == 100 && minorCode == 2) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // TODO: 9/25/2016  Error 100 - USER_REGISTER_BAD_PAYLOAD
                            //Invalid phoneNumber
                            requestRegister();
                        }
                    });
                } else if (majorCode == 101) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // TODO: 9/25/2016  Error 101 - USER_REGISTER_INTERNAL_SERVER_ERROR
                            //Invalid phoneNumber
                            requestRegister();
                        }
                    });
                } else if (majorCode == 135) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // TODO: 9/25/2016  Error 135 - USER_REGISTER_BLOCKED_USER
                        }
                    });
                } else if (majorCode == 136) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // TODO: 9/25/2016  Error 136 - USER_REGISTER_MAX_TRY_LOCK
                        }
                    });
                } else if (majorCode == 137) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // TODO: 9/25/2016  Error 137 - USER_REGISTER_MAX_SEND_LOCK
                        }
                    });
                }
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
    }

    private void userVerify(final String userName, final String verificationCode) {
        rg_prg_verify_generate.setVisibility(View.VISIBLE);
        rg_txt_verify_generate.setTextAppearance(G.context, R.style.RedHUGEText);

        userVerifyResponse(verificationCode);
        ProtoUserVerify.UserVerify.Builder userVerify = ProtoUserVerify.UserVerify.newBuilder();
        userVerify.setCode(Integer.parseInt(verificationCode));
        userVerify.setUsername(userName);

        RequestWrapper requestWrapper = new RequestWrapper(101, userVerify);
        try {
            RequestQueue.sendRequest(requestWrapper);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
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
                        rg_txt_verify_sms.setTextColor(getResources().getColor(R.color.rg_text_verify));

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
            public void onUserVerifyError(int majorCode, int minorCode) {

                if (majorCode == 102 && minorCode == 1) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            errorVerifySms();
                            countDownTimer.cancel();
                        }
                    });
                } else if (majorCode == 102 && minorCode == 2) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // TODO: 9/25/2016 Invalid username
                        }
                    });
                } else if (majorCode == 102 && minorCode == 3) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // TODO: 9/25/2016 Invalid device
                        }
                    });
                } else if (majorCode == 102 && minorCode == 4) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // TODO: 9/25/2016 Invalid osName
                        }
                    });
                } else if (majorCode == 102 && minorCode == 5) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // TODO: 9/25/2016 Invalid osVersion
                        }
                    });
                } else if (majorCode == 103) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // TODO: 9/25/2016 Error 103 - USER_VERIFY_INTERNAL_SERVER_ERROR
                        }
                    });
                } else if (majorCode == 104) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // There is no registered user with given username
                            // TODO: 9/25/2016 Error 104 - USER_VERIFY_USER_NOT_FOUND

                        }
                    });
                } else if (majorCode == 105) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // User is blocked , You cannot verify the user
                            // TODO: 9/25/2016 Error 105 - USER_VERIFY_BLOCKED_USER

                            new MaterialDialog.Builder(ActivityRegister.this).title(R.string.USER_VERIFY_BLOCKED_USER).content(R.string.Toast_Number_Block).positiveText(R.string.B_ok).show();
                        }
                    });
                } else if (majorCode == 106) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // Verification code is invalid
                            // TODO: 9/25/2016 Error 106 - USER_VERIFY_INVALID_CODE
                            errorVerifySms();
                        }
                    });
                } else if (majorCode == 107) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // There is no registered user with given username
                            // TODO: 9/25/2016 Error 107 - USER_VERIFY_EXPIRED_CODE

                        }
                    });
                } else if (majorCode == 108) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // Verification code is locked for a while due to too many tries
                            // TODO: 9/25/2016 Error 108 - USER_VERIFY_MAX_TRY_LOCK

                        }
                    });
                }
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
                                RealmUserInfo userInfo = realm.where(RealmUserInfo.class).equalTo(RealmUserInfoFields.USER_INFO.ID, userId).findFirst();
                                if (userInfo == null) {
                                    userInfo = realm.createObject(RealmUserInfo.class);
                                    RealmRegisteredInfo registeredInfo = realm.createObject(RealmRegisteredInfo.class);
                                    registeredInfo.setId(userId);
                                    userInfo.setUserInfo(registeredInfo);
                                }
                                userInfo.getUserInfo().setUsername(userName);
                                userInfo.getUserInfo().setPhoneNumber(phoneNumber);
                                userInfo.setToken(token);
                                if (!newUser) {
                                    userInfo.setUserRegistrationState(true);
                                }
                                G.importContact();
                                G.getUserInfo();
                            }
                        });

                        rg_prg_verify_register.setVisibility(View.GONE);
                        rg_img_verify_register.setVisibility(View.VISIBLE);
                        rg_txt_verify_register.setTextColor(getResources().getColor(R.color.rg_text_verify));

                        if (newUser) {
                            Intent intent = new Intent(G.context, ActivityProfile.class);
                            intent.putExtra(ActivityProfile.ARG_USER_ID, userId);
                            startActivity(intent);
                            finish();
                        } else {
                            // get user info for set nick name and after from that go to
                            // ActivityMain
                            getUserInfo();
                        }
                        realm.close();
                    }
                });
            }

            @Override
            public void onLoginError(int majorCode, int minorCode) {
                if (majorCode == 109 && minorCode == 1) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            // TODO: 9/25/2016 Error 109 - USER_LOGIN_BAD_PAYLOAD
                        }
                    });
                } else if (majorCode == 110) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            // TODO: 9/25/2016 Error 110 - USER_LOGIN_INTERNAL_SERVER_ERROR
                        }
                    });
                } else if (majorCode == 111 && minorCode == 4) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            HelperLogout.logout();
                        }
                    });
                } else if (majorCode == 111) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            requestLogin();
                        }
                    });
                }
            }
        };

        requestLogin();
    }

    private void getUserInfo() {

        G.onUserInfoResponse = new OnUserInfoResponse() {
            @Override
            public void onUserInfo(final ProtoGlobal.RegisteredUser user, String identity) {

                Realm realm = Realm.getDefaultInstance();
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        RealmUserInfo realmUserInfo = realm.where(RealmUserInfo.class).findFirst();
                        realmUserInfo.getUserInfo().setDisplayName(user.getDisplayName());
                        realmUserInfo.getUserInfo().setInitials(user.getInitials());
                        realmUserInfo.getUserInfo().setColor(user.getColor());
                        realmUserInfo.setUserRegistrationState(true);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent = new Intent(G.context, ActivityMain.class);
                                intent.putExtra(ActivityProfile.ARG_USER_ID, userId);
                                startActivity(intent);
                                finish();
                            }
                        });
                    }
                });
                realm.close();
            }

            @Override
            public void onUserInfoTimeOut() {

            }

            @Override
            public void onUserInfoError(int majorCode, int minorCode) {

            }
        };

        new RequestUserInfo().userInfo(userId);
    }

    private void requestLogin() {
        new RequestUserLogin().userLogin(token);
    }

    private void receiveVerifySms(String message) {

        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }

        String verificationCode = HelperString.regexExtractValue(message, regexFetchCodeVerification);
        countDownTimer.cancel(); //cancel method CountDown and continue process verify

        rg_prg_verify_sms.setVisibility(View.GONE);
        rg_img_verify_sms.setVisibility(View.VISIBLE);
        rg_txt_verify_sms.setTextColor(getResources().getColor(R.color.rg_text_verify));
        userVerify(userName, verificationCode);
    }


    @Override
    protected void onStop() {

        try {
            unregisterReceiver(smsReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void getVerificationSms() { //TODO [Saeed Mozaffari] [2016-08-22 10:48 AM] - this
        // method is fake and will be removed later
        rg_prg_verify_sms.setVisibility(View.VISIBLE);
        rg_txt_verify_generate.setTextAppearance(G.context, R.style.RedHUGEText);
        G.handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                rg_prg_verify_sms.setVisibility(View.GONE);
                rg_img_verify_sms.setVisibility(View.VISIBLE);
                rg_txt_verify_sms.setTextColor(getResources().getColor(R.color.rg_text_verify));

                receiveVerifySms("Your login code is : 12345 This code can be used to login to your account.");
            }
        }, 4000);
    }

    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save the user's current game state
        savedInstanceState.putString(KEY_SAVE_CODENUMBER, edtCodeNumber.getText().toString());
        savedInstanceState.putString(KEY_SAVE_PHONENUMBER_MASK, edtPhoneNumber.getMask());
        savedInstanceState.putString(KEY_SAVE_PHONENUMBER_NUMBER, edtPhoneNumber.getText().toString());
        savedInstanceState.putString(KEY_SAVE_NAMECOUNTRY, btnChoseCountry.getText().toString());
        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }
}
