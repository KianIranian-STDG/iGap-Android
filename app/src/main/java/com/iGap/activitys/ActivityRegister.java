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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.iGap.G;
import com.iGap.R;
import com.iGap.adapter.AdapterDialog;
import com.iGap.helper.HelperString;
import com.iGap.interface_package.OnComplete;
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
import com.iGap.request.RequestQueue;
import com.iGap.request.RequestUserLogin;
import com.iGap.request.RequestWrapper;
import com.vicmikhailau.maskededittext.MaskedEditText;

import java.util.ArrayList;
import java.util.Collections;

import io.realm.Realm;

public class ActivityRegister extends ActivityEnhanced {

    private SoftKeyboard softKeyboard;

    private Button btnStart;
    public static Button btnChoseCountry;
    public static EditText edtCodeNumber;

    public static MaskedEditText edtPhoneNumber;

    private TextView txtAgreement_register, txtTitleToolbar;
    private ProgressBar rg_prg_verify_connect, rg_prg_verify_sms, rg_prg_verify_generate, rg_prg_verify_register;
    private TextView rg_txt_verify_connect, rg_txt_verify_sms, rg_txt_verify_generate, rg_txt_verify_register;
    private ImageView rg_img_verify_connect, rg_img_verify_sms, rg_img_verify_generate, rg_img_verify_register;
    private ViewGroup layout_agreement;
    private ViewGroup layout_verify;

    private String codeNumber, phoneNumber;
    public static String setTextCodePhone;
    public static String setTextNameCountry;
    public static String setMaskPhoneNumber;
    public static String isoCode = "IR";
    private String code;
    private String pattern;
    private String regex;
    private String userName;
    private String token;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

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

        int portaret = getResources().getConfiguration().orientation;

        if (portaret == 1) {//portrait{

            txtAgreement_register = (TextView) findViewById(R.id.txtAgreement_register);
            txtAgreement_register.setMovementMethod(new ScrollingMovementMethod());

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
                structCountry.setPhonePattetn(listItem[3]);
            } else {
                structCountry.setPhonePattetn(" ");
            }

            structCountryArrayList.add(structCountry);
        }

        Collections.sort(structCountryArrayList, new CountryListComparator());

        for (int i = 0; i < structCountryArrayList.size(); i++) {

            countryNameList[i] = structCountryArrayList.get(i).getName();
            StructCountry item = new StructCountry();
            item.setName(structCountryArrayList.get(i).getName());
            item.setCountryCode(structCountryArrayList.get(i).getCountryCode());
            item.setPhonePattetn(structCountryArrayList.get(i).getPhonePattetn());
            item.setAbbreviation(structCountryArrayList.get(i).getAbbreviation());
            items.add(item);
        }
        //==================================================================================================== set item for adapterListView from Realm

//       list of country

        //===================================================================================================== start  alert Dialog choose country

        btnChoseCountry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialogChooseCountry = new Dialog(ActivityRegister.this);

                dialogChooseCountry.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialogChooseCountry.setContentView(R.layout.rg_dialog);

                int setWidth = (int) (getResources().getDisplayMetrics().widthPixels * 0.9);
                int setHeight = (int) (getResources().getDisplayMetrics().heightPixels * 0.9);
                dialogChooseCountry.getWindow().setLayout(setWidth, setHeight);

                final TextView txtTitle = (TextView) dialogChooseCountry.findViewById(R.id.rg_txt_titleToolbar);

                edtSearchView = (SearchView) dialogChooseCountry.findViewById(R.id.rg_edtSearch_toolbar);

                txtTitle.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        edtSearchView.onActionViewExpanded();
                        edtSearchView.setIconifiedByDefault(true);
                        txtTitle.setVisibility(View.GONE);
                    }
                });

//                int portaret_landscope = getResources().getConfiguration().orientation; //check for portrait & landScape
//                if (portaret_landscope == 1) {//portrait

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
                                    edtSearchView.onActionViewExpanded();
                                    txtTitle.setVisibility(View.GONE);
                                } else {

                                    edtSearchView.onActionViewCollapsed();
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
//                setiItemCountr();
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

                Button btnOk = (Button) dialogChooseCountry.findViewById(R.id.rg_btn_okDialog);
                btnOk.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

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

                if (edtPhoneNumber.getText().length() > 0) {
                    btnStart.setEnabled(false);
                    phoneNumber = edtPhoneNumber.getText().toString();
                    codeNumber = edtCodeNumber.getText().toString();

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
            pattern = extras.getString("PATTERN");
            regex = extras.getString("REGEX");
            String body = extras.getString("TERMS_BODY");
            if (body != null) {
                txtAgreement_register.setText(Html.fromHtml(body));
            }
        }
    }

    //======= process verify : check internet and sms
    private void checkVerify() {

        setItem(); // invoke object

        if (checkInternet()) { //connection ok

            userRegister();

            countDownTimer = new CountDownTimer(1000 * 60, 1000) { // wait for verify sms

                TextView txtTimer;
                TextView txtTimerLand;

                public void onTick(long millisUntilFinished) {

                    int seconds = (int) ((millisUntilFinished) / 1000);
                    int minutes = seconds / 60;
                    seconds = seconds % 60;


                    int portaret_landscope = getResources().getConfiguration().orientation;
                    if (portaret_landscope == 1) {//portrait

                        txtTimer = (TextView) findViewById(R.id.rg_txt_verify_timer);
                        txtTimer.setText("" + String.format("%02d", minutes) + ":" + String.format("%02d", seconds));

                    } else {

                        txtTimerLand = (TextView) dialogVerifyLandScape.findViewById(R.id.rg_txt_verify_timer_DialogLand);
                        txtTimerLand.setText("" + String.format("%02d", minutes) + ":" + String.format("%02d", seconds));
                    }

                    // isReciveSMS = true;
                }

                public void onFinish() {

                    int portaret_landscope = getResources().getConfiguration().orientation;
                    if (portaret_landscope == 1) {//portrait
                        txtTimer.setText("00:00");

                    } else {
                        txtTimerLand.setText("00:00");
                    }
                    errorVerifySms(); // open rg_dialog for enter sms code
                }
            };
            countDownTimer.start();


            if (generate()) {// generate is ok

                if (register()) {

                }

            } else { // problem in generate

            }
        } else { // connection error

            rg_prg_verify_connect.setVisibility(View.GONE);
            rg_img_verify_connect.setImageResource(R.mipmap.alert);
            rg_img_verify_connect.setColorFilter(getResources().getColor(R.color.rg_error_red), PorterDuff.Mode.SRC_ATOP);
            rg_img_verify_connect.setVisibility(View.VISIBLE);
            rg_txt_verify_connect.setTextColor(getResources().getColor(R.color.rg_error_red));
            rg_txt_verify_connect.setText("Please check your connection");
        }
    }

    private void setItem() { //invoke object

        int portaret_landscope = getResources().getConfiguration().orientation; //check for portrait & landScape
        if (portaret_landscope == 1) {//portrait

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
    //
    private void errorVerifySms() { //when don't receive sms and open rg_dialog for enter code

        rg_prg_verify_sms.setVisibility(View.GONE);
        rg_img_verify_sms.setImageResource(R.mipmap.alert);
        rg_img_verify_sms.setVisibility(View.VISIBLE);
        rg_img_verify_sms.setColorFilter(getResources().getColor(R.color.rg_error_red), PorterDuff.Mode.SRC_ATOP);
        rg_txt_verify_sms.setText("Error verification SMS");
        rg_txt_verify_sms.setTextColor(getResources().getColor(R.color.rg_error_red));

        final Dialog dialog = new Dialog(ActivityRegister.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.rg_dialog_verify_code);
        dialog.setCanceledOnTouchOutside(false);

        EditText edtEnterCodeVerify = (EditText) dialog.findViewById(R.id.rg_edt_dialog_verifyCode); //EditText For Enter sms cod
        // // TODO: 8/13/2016  enter code for verify

        Button btnCancel = (Button) dialog.findViewById(R.id.rg_btn_cancelVerifyCode);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        Button btnOk = (Button) dialog.findViewById(R.id.rg_btn_dialog_okVerifyCode);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                edtPhoneNumber.setText("");
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    //    sms verify is ok
    private void receiveVerifySms() { //when sms is receive

        countDownTimer.cancel(); //cancel method CountDown and continue process verify

        rg_prg_verify_sms.setVisibility(View.GONE);
        rg_img_verify_sms.setVisibility(View.VISIBLE);
        rg_txt_verify_sms.setTextColor(getResources().getColor(R.color.rg_start_background));
        userVerify(userName);
    }


    private void userRegister() {

        rg_prg_verify_connect.setVisibility(View.VISIBLE);
        rg_txt_verify_sms.setTextAppearance(G.context, R.style.RedHUGEText);
        G.onUserRegistration = new OnUserRegistration() {

            @Override
            public void onRegister(final String userNameR, final long userIdR, final ProtoUserRegister.UserRegisterResponse.Method methodValue) {

                Log.i("SOC_INFO", "userRegister is ok !");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        userName = userNameR;
                        userId = userIdR;


                        if (methodValue == ProtoUserRegister.UserRegisterResponse.Method.VERIFY_CODE_SMS) {//verification with sms

                        } else if (methodValue == ProtoUserRegister.UserRegisterResponse.Method.VERIFY_CODE_SOCKET) {//verification with socket

                        } else if (methodValue == ProtoUserRegister.UserRegisterResponse.Method.VERIFY_CODE_SMS_SOCKET) {//verification with sms and socket

                        }

                        rg_prg_verify_connect.setVisibility(View.GONE);
                        rg_img_verify_connect.setVisibility(View.VISIBLE);
                        rg_txt_verify_connect.setTextColor(getResources().getColor(R.color.rg_text_verify));
                        getVerificationSms(userName);
                    }
                });
            }
        };


        G.handler.postDelayed(new Runnable() {
            @Override
            public void run() {
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
        }, 4000);


    }

    private void getVerificationSms(final String userName) { //TODO [Saeed Mozaffari] [2016-08-22 10:48 AM] - this method is fake and will be removed later
        rg_prg_verify_sms.setVisibility(View.VISIBLE);
        rg_txt_verify_generate.setTextAppearance(G.context, R.style.RedHUGEText);
        G.handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                receiveVerifySms();
                rg_prg_verify_sms.setVisibility(View.GONE);
                rg_img_verify_sms.setVisibility(View.VISIBLE);
                rg_txt_verify_sms.setTextColor(getResources().getColor(R.color.rg_start_background));

                userVerify(userName);
            }
        }, 4000);
    }

    private void userVerify(final String userName) {
        rg_prg_verify_generate.setVisibility(View.VISIBLE);
        rg_txt_verify_register.setTextAppearance(G.context, R.style.RedHUGEText);
        G.onUserVerification = new OnUserVerification() {
            @Override
            public void onUserVerify(final String tokenR, final boolean newUserR) {
                Log.i("SOC_INFO", "userVerification is Ok !");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        newUser = newUserR;
                        token = tokenR;
                        rg_prg_verify_generate.setVisibility(View.GONE);
                        rg_img_verify_generate.setVisibility(View.VISIBLE);
                        rg_txt_verify_generate.setTextColor(getResources().getColor(R.color.rg_text_verify));
                        userLogin(token);
                    }
                });
            }
        };


        G.handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.i("SOC_INFO", "userVerification Start!");
                ProtoUserVerify.UserVerify.Builder userVerify = ProtoUserVerify.UserVerify.newBuilder();
                userVerify.setCode(12345);
                userVerify.setUsername(userName);
                userVerify.setDevice("Mobile");
                userVerify.setOsName("android");
                userVerify.setOsVersion("21"); // TODO [Saeed Mozaffari] [2016-08-21 11:37 AM] - get phone version

                RequestWrapper requestWrapper = new RequestWrapper(101, userVerify);
                try {
                    RequestQueue.sendRequest(requestWrapper);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }, 4000);

    }

    private void userLogin(final String token) {
        rg_prg_verify_register.setVisibility(View.VISIBLE);
        G.onUserLogin = new OnUserLogin() {
            @Override
            public void onLogin() {
                Log.i("SOC_INFO", "User Successfully login!");

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        G.realm.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                RealmUserInfo userInfo = realm.createObject(RealmUserInfo.class);
                                userInfo.setUserId(userId);
                                userInfo.setUserName(userName);
                                userInfo.setCountryISOCode(isoCode);
                                userInfo.setPhoneNumber(phoneNumber);
                                userInfo.setToken(token);
                                userInfo.setUserRegistrationState(true);
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
                        Intent intent = new Intent(G.context, className);
                        startActivity(intent);
                        finish();
                    }
                });


            }
        };


        G.handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                new RequestUserLogin().userLogin(token);
            }
        }, 4000);


    }

    private boolean generate() {
        return false;
    }

    private boolean register() {
        return false;
    }

    @Override
    protected void onResume() {

        final IntentFilter filter = new IntentFilter();
        filter.addAction("android.provider.Telephony.SMS_RECEIVED");


        smsReceiver = new IncomingSms("9372779537", new OnComplete() {

            @Override
            public void onComplete(Boolean result, String message) {

                code = message;
                Toast.makeText(ActivityRegister.this, "1" + message, Toast.LENGTH_SHORT).show();
                try {
                    if (message != null && !message.isEmpty() && !message.equals("null") && !message.equals("")) {
                        rg_txt_verify_sms.setText(message);
                        receiveVerifySms();
                    }
                } catch (Exception e1) {

                    e1.getStackTrace();
                }

                try {
                    unregisterReceiver(smsReceiver);
                } catch (Exception e) {
                    e.getStackTrace();
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
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
