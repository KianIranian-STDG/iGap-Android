package net.iGap.fragments;



import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import io.realm.Realm;
import net.iGap.G;
import net.iGap.R;
import net.iGap.activities.ActivityMain;
import net.iGap.libs.rippleeffect.RippleView;
import net.iGap.module.SHP_SETTING;
import net.iGap.realm.RealmUserInfo;

import static android.content.Context.MODE_PRIVATE;
import static net.iGap.G.context;
import static net.iGap.R.id.numberPicker;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentPassCode extends BaseFragment implements AdapterView.OnItemSelectedListener {
    private FragmentActivity mActivity;
    private Realm realm;
    private boolean isPassCode;
    private boolean isFingerPrintCode;
    private String passCode;
    private EditText edtSetPassword;
    private TextView txtSetPassword;
    private TextView titlePassCode;
    private TextView txtAutoLock;
    private RippleView rippleOk;
    private String password;
    private int page = 0;
    private NumberPicker numberPickerMinutes;
    private boolean deviceHasFingerPrint;
    private ViewGroup vgFingerPrint;
    private Spinner staticSpinner;
    private static final String[] paths = {"PIN", "Password"};
    private final int PIN = 0;
    private final int PASSWORD = 1;
    private int kindPassword = 0;
    SharedPreferences sharedPreferences;

    public FragmentPassCode() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_pass_code, container, false);
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        checkFingerPrint();

        sharedPreferences = mActivity.getSharedPreferences(SHP_SETTING.FILE_NAME, MODE_PRIVATE);

        final ViewGroup rootSettingPassword = (ViewGroup) view.findViewById(R.id.rootSettingPassword);
        final ViewGroup rootEnterPassword = (ViewGroup) view.findViewById(R.id.rootEnterPassword);
        final ViewGroup vgTogglePassCode = (ViewGroup) view.findViewById(R.id.vgTogglePassCode);
        final ViewGroup changePassCode = (ViewGroup) view.findViewById(R.id.st_layout_ChangePassCode);
        final ViewGroup vgAutoLock = (ViewGroup) view.findViewById(R.id.st_layout_autoLock);
        vgFingerPrint = (ViewGroup) view.findViewById(R.id.vgToggleFingerPrint);

        edtSetPassword = (EditText) view.findViewById(R.id.setPassword_edtSetPassword);
        txtSetPassword = (TextView) view.findViewById(R.id.setPassword_txtSetPassword);
        titlePassCode = (TextView) view.findViewById(R.id.titlePassCode);
        txtAutoLock = (TextView) view.findViewById(R.id.st_txt_autoLock);
        rippleOk = (RippleView) view.findViewById(R.id.setPassword_rippleOk);

        final ViewGroup rootPassCode = (ViewGroup) view.findViewById(R.id.rootPassCode);
        rootPassCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        TextView txtBack = (TextView) view.findViewById(R.id.stns_txt_back);
        txtBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //mActivity.getSupportFragmentManager().popBackStack();

                closeFragment();

                closeKeyboard(v);

            }
        });

        final TextView txtPassCode = (TextView) view.findViewById(R.id.st_txt_st_toggle_passCode);
        final ToggleButton togglePassCode = (ToggleButton) view.findViewById(R.id.st_toggle_passCode);

        final TextView txtAutoFingerPrint = (TextView) view.findViewById(R.id.st_txt_st_toggle_FingerPrint);
        final ToggleButton toggleEnableFingerPrint = (ToggleButton) view.findViewById(R.id.st_toggle_FingerPrint);

        final TextView txtScreenShot = (TextView) view.findViewById(R.id.st_txt_st_tAllowScreenCapture);
        final ToggleButton toggleScreenShot = (ToggleButton) view.findViewById(R.id.st_toggle_AllowScreenCapture);

        realm = Realm.getDefaultInstance();

        final RealmUserInfo realmUserInfo = realm.where(RealmUserInfo.class).findFirst();

        if (realmUserInfo != null) {
            isPassCode = realmUserInfo.isPassCode();
            isFingerPrintCode = realmUserInfo.isFingerPrint();
            password = realmUserInfo.getPassCode();
            kindPassword = realmUserInfo.getKindPassCode();
        }


        if (kindPassword == PIN) {
            edtSetPassword.setInputType(InputType.TYPE_CLASS_NUMBER);
        } else {
            edtSetPassword.setInputType(InputType.TYPE_CLASS_TEXT);
        }


        staticSpinner = (Spinner) view.findViewById(R.id.static_spinner);

        // Create an ArrayAdapter using the string array and a default spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(mActivity, R.layout.spinner_password, paths);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        staticSpinner.setAdapter(adapter);
        staticSpinner.setOnItemSelectedListener(this);

        if (isPassCode) {

            page = 3;
            vgTogglePassCode.setVisibility(View.GONE);
            rootEnterPassword.setVisibility(View.VISIBLE);
            rootSettingPassword.setVisibility(View.GONE);
            rippleOk.setVisibility(View.VISIBLE);
            txtSetPassword.setText(getResources().getString(R.string.enter_a_password));
            togglePassCode.setChecked(true);

        } else {
            rootSettingPassword.setVisibility(View.GONE);
            togglePassCode.setChecked(false);
        }

        togglePassCode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // set password;

                if (realmUserInfo != null) isPassCode = realmUserInfo.isPassCode();

                if (!isPassCode) {
                    vgTogglePassCode.setVisibility(View.GONE);
                    rootEnterPassword.setVisibility(View.VISIBLE);
                    rootSettingPassword.setVisibility(View.GONE);
                    rippleOk.setVisibility(View.VISIBLE);
                    //txtSetPassword.setText(getResources().getString(R.string.enter_a_password));
                    //titlePassCode.setText("PIN");
                    titlePassCode.setVisibility(View.GONE);
                    staticSpinner.setVisibility(View.VISIBLE);
                    if (kindPassword == PIN) {
                        edtSetPassword.setInputType(InputType.TYPE_CLASS_NUMBER);
                    } else {
                        edtSetPassword.setInputType(InputType.TYPE_CLASS_TEXT);
                    }
                } else {
                    togglePassCode.setChecked(false);
                    vgTogglePassCode.setVisibility(View.VISIBLE);
                    rootEnterPassword.setVisibility(View.GONE);
                    rootSettingPassword.setVisibility(View.GONE);
                    rippleOk.setVisibility(View.GONE);
                    staticSpinner.setVisibility(View.GONE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean(SHP_SETTING.KEY_SCREEN_SHOT_LOCK, false);
                    editor.putLong(SHP_SETTING.KEY_TIME_LOCK, 0);
                    editor.apply();
                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            if (realmUserInfo != null) {
                                realmUserInfo.setPassCode(false);
                                realmUserInfo.setPassCode("");
                            }
                        }
                    });
                }
            }
        });

        txtPassCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                togglePassCode.setChecked(!togglePassCode.isChecked());
            }
        });

        changePassCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vgTogglePassCode.setVisibility(View.GONE);
                rootEnterPassword.setVisibility(View.VISIBLE);
                rootSettingPassword.setVisibility(View.GONE);
                rippleOk.setVisibility(View.VISIBLE);
                //titlePassCode.setText("PIN");
                titlePassCode.setVisibility(View.GONE);
                staticSpinner.setVisibility(View.VISIBLE);
                if (kindPassword == PIN) {
                    edtSetPassword.setInputType(InputType.TYPE_CLASS_NUMBER);
                } else {
                    edtSetPassword.setInputType(InputType.TYPE_CLASS_TEXT);
                }
            }
        });


        if (isFingerPrintCode) {
            toggleEnableFingerPrint.setChecked(true);
        } else {
            toggleEnableFingerPrint.setChecked(false);
        }

        toggleEnableFingerPrint.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                // set password;

                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {

                        if (realmUserInfo != null) {
                            if (isFingerPrintCode) {
                                realmUserInfo.setFingerPrint(false);
                            } else {
                                realmUserInfo.setFingerPrint(true);
                            }
                        }
                    }
                });
            }
        });

        txtAutoFingerPrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleEnableFingerPrint.setChecked(!toggleEnableFingerPrint.isChecked());
            }
        });


        final boolean screenShot = sharedPreferences.getBoolean(SHP_SETTING.KEY_SCREEN_SHOT_LOCK, false);


        if (screenShot) {
            toggleScreenShot.setChecked(true);
        } else {
            toggleScreenShot.setChecked(false);
        }

        toggleScreenShot.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                // set password;

                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {

                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        if (screenShot) {
                            editor.putBoolean(SHP_SETTING.KEY_SCREEN_SHOT_LOCK, false);
                        } else {
                            editor.putBoolean(SHP_SETTING.KEY_SCREEN_SHOT_LOCK, true);
                        }
                        editor.apply();
                    }
                });
            }
        });

        txtScreenShot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleScreenShot.setChecked(!toggleScreenShot.isChecked());
            }
        });








        rippleOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (page == 0 && edtSetPassword.getText().length() > 0) {

                    password = edtSetPassword.getText().toString();
                    edtSetPassword.setText("");
                    txtSetPassword.setText(getString(R.string.please_re_enter_your_password));

                    page = 1;

                } else if (page == 1 && edtSetPassword.getText().length() > 0) {

                    if (edtSetPassword.getText().toString().equals(password)) {
                        vgTogglePassCode.setVisibility(View.VISIBLE);
                        rootEnterPassword.setVisibility(View.GONE);
                        rootSettingPassword.setVisibility(View.VISIBLE);
                        if (deviceHasFingerPrint) {
                            vgFingerPrint.setVisibility(View.VISIBLE);
                        } else {
                            vgFingerPrint.setVisibility(View.GONE);
                        }
                        rippleOk.setVisibility(View.GONE);
                        titlePassCode.setText(getResources().getString(R.string.two_step_pass_code));
                        titlePassCode.setVisibility(View.VISIBLE);
                        staticSpinner.setVisibility(View.GONE);
                        if (ActivityMain.iconLock != null) {
                            ActivityMain.iconLock.setVisibility(View.VISIBLE);
                        }
                        closeKeyboard(v);


                        realm.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                if (realmUserInfo != null) {
                                    realmUserInfo.setPassCode(true);
                                    realmUserInfo.setPassCode(edtSetPassword.getText().toString());
                                }
                            }
                        });
                        edtSetPassword.setText("");
                    } else {
                        closeKeyboard(v);
                        error(getString(R.string.Password_dose_not_match));
                    }



                } else if (page == 3 && edtSetPassword.getText().length() > 0) {

                    if (edtSetPassword.getText().toString().contains(password)) {
                        vgTogglePassCode.setVisibility(View.VISIBLE);
                        rootEnterPassword.setVisibility(View.GONE);
                        rootSettingPassword.setVisibility(View.VISIBLE);
                        if (deviceHasFingerPrint) {
                            vgFingerPrint.setVisibility(View.VISIBLE);
                        } else {
                            vgFingerPrint.setVisibility(View.GONE);
                        }
                        rippleOk.setVisibility(View.GONE);
                        titlePassCode.setText(getResources().getString(R.string.two_step_pass_code));
                        titlePassCode.setVisibility(View.VISIBLE);
                        staticSpinner.setVisibility(View.GONE);
                        closeKeyboard(v);
                    } else {
                        closeKeyboard(v);
                        error(getString(R.string.invalid_password));
                    }

                } else {
                    closeKeyboard(v);
                    error(getString(R.string.enter_a_password));
                }


            }
        });
        long valuNumberPic = sharedPreferences.getLong(SHP_SETTING.KEY_TIME_LOCK, 0);
        if (valuNumberPic == 0) {
            txtAutoLock.setText("Disable");
        } else if (valuNumberPic == 60) {
            txtAutoLock.setText("in 1 minute");
        } else if (valuNumberPic == 60 * 5) {
            txtAutoLock.setText("in 5 minutes");
        } else if (valuNumberPic == 60 * 60) {
            txtAutoLock.setText("in 1 hour");
        } else if (valuNumberPic == 60 * 60 * 5) {
            txtAutoLock.setText("in 5 hours");
        }



        vgAutoLock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                boolean wrapInScrollView = true;
                final MaterialDialog dialog = new MaterialDialog.Builder(mActivity).title(getString(R.string.auto_lock)).customView(R.layout.dialog_auto_lock, wrapInScrollView).positiveText(R.string.B_ok).negativeText(R.string.B_cancel).build();

                View view1 = dialog.getCustomView();

                assert view1 != null;
                numberPickerMinutes = (NumberPicker) view1.findViewById(numberPicker);
                numberPickerMinutes.setMinValue(0);
                numberPickerMinutes.setMaxValue(4);
                numberPickerMinutes.setWrapSelectorWheel(true);
                //numberPickerMinutes.setDisplayedValues(new String[]{"in 1 hour", "in 5 hours", "in 1 minute", "in 5 minutes", "Disable"});


                long valueNumberPic = sharedPreferences.getLong(SHP_SETTING.KEY_TIME_LOCK, 0);
                if (valueNumberPic == 0) {
                    numberPickerMinutes.setValue(0);
                } else if (valueNumberPic == 60) {
                    numberPickerMinutes.setValue(1);
                } else if (valueNumberPic == 60 * 5) {
                    numberPickerMinutes.setValue(2);
                } else if (valueNumberPic == 60 * 60) {
                    numberPickerMinutes.setValue(3);
                } else if (valueNumberPic == 60 * 60 * 5) {
                    numberPickerMinutes.setValue(4);
                }

                numberPickerMinutes.setFormatter(new NumberPicker.Formatter() {
                    @Override
                    public String format(int value) {
                        if (value == 0) {
                            return "Disable";
                        } else if (value == 1) {
                            return "in 1 minute";
                        } else if (value == 2) {
                            return "in 5 minutes";
                        } else if (value == 3) {
                            return "in 1 hour";
                        } else if (value == 4) {
                            return "in 5 hours";
                        }
                        return "";
                    }
                });


                View btnPositive = dialog.getActionButton(DialogAction.POSITIVE);
                btnPositive.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        txtAutoLock.setText("in " + numberPickerMinutes.getDisplayedValues());
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putLong(SHP_SETTING.KEY_TIME_LOCK, numberPickerMinutes.getValue());
                        editor.apply();


                        int which = numberPickerMinutes.getValue();
                        if (which == 0) {
                            editor.putLong(SHP_SETTING.KEY_TIME_LOCK, 0);
                            txtAutoLock.setText("Disable");
                        } else if (which == 1) {
                            editor.putLong(SHP_SETTING.KEY_TIME_LOCK, 60);
                            txtAutoLock.setText("in 1 minute");
                        } else if (which == 2) {
                            editor.putLong(SHP_SETTING.KEY_TIME_LOCK, 60 * 5);
                            txtAutoLock.setText("in 5 minutes");
                        } else if (which == 3) {
                            editor.putLong(SHP_SETTING.KEY_TIME_LOCK, 60 * 60);
                            txtAutoLock.setText("in 1 hour");
                        } else if (which == 4) {
                            editor.putLong(SHP_SETTING.KEY_TIME_LOCK, 60 * 60 * 5);
                            txtAutoLock.setText("in 5 hours");
                        }
                        editor.apply();
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });

    }

    @TargetApi(Build.VERSION_CODES.M)
    private void checkFingerPrint() {


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //Fingerprint API only available on from Android 6.0 (M)
            FingerprintManager fingerprintManager = (FingerprintManager) context.getSystemService(Context.FINGERPRINT_SERVICE);
            if (ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            if (!fingerprintManager.isHardwareDetected()) {
                // Device doesn't support fingerprint authentication
                deviceHasFingerPrint = false;
            } else if (!fingerprintManager.hasEnrolledFingerprints()) {
                // User hasn't enrolled any fingerprints to authenticate with
                deviceHasFingerPrint = false;
            } else {
                deviceHasFingerPrint = true;
            }
            // Everything is ready for fingerprint authentication
        }
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (FragmentActivity) context;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        realm.close();
    }

    private void closeKeyboard(View v) {
        if (isAdded()) {
            try {
                InputMethodManager imm = (InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            } catch (IllegalStateException e) {
                e.getStackTrace();
            }
        }
    }

    private void error(String error) {
        if (isAdded()) {
            try {
                Vibrator vShort = (Vibrator) G.context.getSystemService(Context.VIBRATOR_SERVICE);
                vShort.vibrate(200);
                final Snackbar snack = Snackbar.make(mActivity.findViewById(android.R.id.content), error, Snackbar.LENGTH_LONG);
                snack.setAction(getString(R.string.cancel), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        snack.dismiss();
                    }
                });
                snack.show();
            } catch (IllegalStateException e) {
                e.getStackTrace();
            }
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (position) {
            case 0:
                // Whatever you want to happen when the first item gets selected
                edtSetPassword.setInputType(InputType.TYPE_CLASS_NUMBER);
                break;
            case 1:
                // Whatever you want to happen when the second item gets selected
                edtSetPassword.setInputType(InputType.TYPE_CLASS_TEXT);
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
