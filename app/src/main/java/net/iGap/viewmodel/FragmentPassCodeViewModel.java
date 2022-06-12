package net.iGap.viewmodel;
/*
 * This is the source code of iGap for Android
 * It is licensed under GNU AGPL v3.0
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright © 2017 , iGap - www.iGap.net
 * iGap Messenger | Free, Fast and Secure instant messaging application
 * The idea of the Kianiranian Company - www.kianiranian.com
 * All rights reserved.
 */

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.text.InputType;
import android.view.View;
import android.view.WindowManager;

import androidx.core.app.ActivityCompat;
import androidx.databinding.ObservableField;
import androidx.lifecycle.MutableLiveData;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.andrognito.patternlockview.PatternLockView;
import com.andrognito.patternlockview.listener.PatternLockViewListener;
import com.andrognito.patternlockview.utils.PatternLockUtils;

import net.iGap.messenger.theme.Theme;
import net.iGap.module.accountManager.DbManager;
import net.iGap.G;
import net.iGap.R;
import net.iGap.activities.ActivityMain;
import net.iGap.databinding.FragmentPassCodeBinding;
import net.iGap.fragments.FragmentPassCode;
import net.iGap.helper.AsyncTransaction;
import net.iGap.helper.HelperLog;
import net.iGap.helper.HelperPreferences;
import net.iGap.model.PassCode;
import net.iGap.module.AppUtils;
import net.iGap.module.DialogAnimation;
import net.iGap.module.SHP_SETTING;
import net.iGap.realm.RealmUserInfo;

import java.util.List;

import io.realm.Realm;

import static android.content.Context.MODE_PRIVATE;
import static net.iGap.G.context;

public class FragmentPassCodeViewModel {

    private final int PIN = 0;
    private final int PASSWORD = 1;
    public ObservableField<String> titlePassCode = new ObservableField<>(G.context.getResources().getString(R.string.two_step_pass_code));
    public ObservableField<String> autoLockText = new ObservableField<>("in 1 hour");
    public ObservableField<String> edtSetPasswordText = new ObservableField<>("");
    public ObservableField<String> edtSetPasswordHint = new ObservableField<>("");
    public ObservableField<String> txtSetPassword = new ObservableField<>(G.context.getResources().getString(R.string.enter_pass_code));
    public ObservableField<String> txtCreateNewPattern = new ObservableField<>();
    public ObservableField<String> txtModePassCode = new ObservableField<>(G.context.getResources().getString(R.string.PIN));
    public ObservableField<Boolean> isTogglePassCode = new ObservableField<>(false);
    public ObservableField<Boolean> isTogglePatternPassCode = new ObservableField<>(false);
    public ObservableField<Boolean> isToggleTactileFeedback = new ObservableField<>(true);
    public ObservableField<Boolean> isFingerPrint = new ObservableField<>(false);
    public ObservableField<Boolean> isAllowScreenCapture = new ObservableField<>(false);
    public ObservableField<Integer> edtSetPasswordInput = new ObservableField<>(InputType.TYPE_TEXT_VARIATION_PASSWORD);
    public ObservableField<Integer> rootSettingPassword = new ObservableField<>(View.GONE);
    public ObservableField<Integer> rootEnterPassword = new ObservableField<>(View.GONE);
    public ObservableField<Integer> visibilityChangePass = new ObservableField<>(View.GONE);
    public ObservableField<Integer> visibilityPatternSetting = new ObservableField<>(View.GONE);
    public ObservableField<Integer> visibilityTactileFeedback = new ObservableField<>(View.GONE);
    public ObservableField<Integer> vgTogglePassCodeVisibility = new ObservableField<>(View.VISIBLE);
    public ObservableField<Integer> visibilityPatternLock = new ObservableField<>(View.VISIBLE);
    public ObservableField<Integer> visibilityDescription = new ObservableField<>(View.VISIBLE);
    public ObservableField<Integer> visibilityPassCode = new ObservableField<>(View.VISIBLE);
    public ObservableField<Integer> visibilityCreateNewPattern = new ObservableField<>(View.VISIBLE);
    public ObservableField<Integer> rootPatternPassword = new ObservableField<>(View.GONE);
    public ObservableField<Integer> titlePassCodeVisibility = new ObservableField<>(View.VISIBLE);
    public ObservableField<Integer> edtSetPasswordMaxLength = new ObservableField<>(20);
    public MutableLiveData<Integer> rippleOkVisibility = new MutableLiveData<>();
    public ObservableField<Integer> layoutModePassCode = new ObservableField<>(View.GONE);
    public ObservableField<Integer> vgToggleFingerPrintVisibility = new ObservableField<>(View.GONE);
    public MutableLiveData<Boolean> passCodeStateChangeListener = new MutableLiveData<>();
    private boolean isPassCode;
    public boolean isPattern;
    private boolean isChangePattern;
    private boolean isFingerPrintCode;
    private String passCode;
    private String password;
    private FragmentPassCode fragment;
    private int page = 0;
    private net.iGap.module.NumberPicker numberPickerMinutes;
    private boolean deviceHasFingerPrint;
    private int kindPassword = 0;
    private SharedPreferences sharedPreferences;
    private boolean screenShot;
    private RealmUserInfo realmUserInfo;
    private FragmentPassCodeBinding binding;
    private final int EnterPassword = 3; // when password is set
    private final int EnterPattern = 4; // when password is set
    private boolean tactile;

    private String mPattern = null;

    private PatternLockViewListener mPatternLockViewListener = new PatternLockViewListener() {
        @Override
        public void onStarted() {
        }

        @Override
        public void onProgress(List<PatternLockView.Dot> progressPattern) {
        }

        @Override
        public void onComplete(List<PatternLockView.Dot> pattern) {


            if (!isPattern || isChangePattern) {
                if (mPattern != null) {
                    if (mPattern.equals(PatternLockUtils.patternToString(binding.patternLockView, pattern))) {
                        PassCode.getInstance().setPattern(true);
                        PassCode.getInstance().setPassCode(true);
                        PassCode.getInstance().setPassCode(mPattern);
                        mPattern = null;
                        isPattern = true;
                        goToSettingPattern();
                    }
                } else {

                    txtCreateNewPattern.set(G.context.getResources().getString(R.string.repeat_pattern_passCode));
                    mPattern = PatternLockUtils.patternToString(binding.patternLockView, pattern);
                }
            } else {
                if (password.equals(PatternLockUtils.patternToString(binding.patternLockView, pattern))) {
                    goToSettingPattern();
                } else {
                    errorWrongPattern();
                }
            }
            binding.patternLockView.clearPattern();
        }

        @Override
        public void onCleared() {
        }
    };

    private void errorWrongPattern() {

    }

    private void goToSettingPattern() {
        PassCode.getInstance().setPassCode(true);
        passCodeStateChangeListener.postValue(PassCode.getInstance().isPassCode());
        visibilityPatternLock.set(View.VISIBLE);
        vgTogglePassCodeVisibility.set(View.VISIBLE);
        visibilityChangePass.set(View.VISIBLE);
        visibilityTactileFeedback.set(View.VISIBLE);
        visibilityDescription.set(View.GONE);
        rootPatternPassword.set(View.GONE);
        visibilityPassCode.set(View.GONE);
        isTogglePatternPassCode.set(true);
        isToggleTactileFeedback.set(true);
    }


    public FragmentPassCodeViewModel(FragmentPassCode fragment, FragmentPassCodeBinding fragmentPassCodeBinding) {
        this.binding = fragmentPassCodeBinding;
        this.fragment = fragment;
        getInfo();
    }


    public void onClickTogglePatternTactileFeedback(View view) {

        boolean tactile = sharedPreferences.getBoolean(SHP_SETTING.KEY_PATTERN_TACTILE_DRAWN, true);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (tactile) {
            isToggleTactileFeedback.set(false);
            editor.putBoolean(SHP_SETTING.KEY_PATTERN_TACTILE_DRAWN, false);
        } else {
            isToggleTactileFeedback.set(true);
            editor.putBoolean(SHP_SETTING.KEY_PATTERN_TACTILE_DRAWN, true);
        }
        editor.apply();
    }

    public void onClickTogglePassCode(View v) {

        edtSetPasswordText.set("");
        if (realmUserInfo != null) isPassCode = PassCode.getInstance().isPassCode();

        if (!isPassCode) {
            page = 0;
            vgTogglePassCodeVisibility.set(View.GONE);
            rootEnterPassword.set(View.VISIBLE);
            rootSettingPassword.set(View.GONE);
            rippleOkVisibility.setValue(View.VISIBLE);
            //txtSetPassword.setText(G.fragmentActivity.getResources().getString(R.string.enter_a_password));
            //titlePassCode.set("PIN");
            txtSetPassword.set(G.fragmentActivity.getResources().getString(R.string.enter_a_password));
            titlePassCodeVisibility.set(View.GONE);
            layoutModePassCode.set(View.VISIBLE);
            if (kindPassword == PIN) {
                edtSetPasswordInput.set(InputType.TYPE_CLASS_NUMBER);
            } else {
                edtSetPasswordInput.set(InputType.TYPE_CLASS_TEXT);
            }
        } else {

            disablePassCode();

        }

    }

    private void disablePassCode() {
        isTogglePassCode.set(false);
        vgTogglePassCodeVisibility.set(View.VISIBLE);
        visibilityDescription.set(View.VISIBLE);
        visibilityPatternLock.set(View.VISIBLE);
        visibilityChangePass.set(View.GONE);
        rootEnterPassword.set(View.GONE);
        rootSettingPassword.set(View.GONE);
        rippleOkVisibility.setValue(View.GONE);
        layoutModePassCode.set(View.GONE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(SHP_SETTING.KEY_SCREEN_SHOT_LOCK, false);
        editor.putLong(SHP_SETTING.KEY_TIME_LOCK, 0);
        editor.apply();

        PassCode.getInstance().setPassCode(false);
        passCodeStateChangeListener.postValue(PassCode.getInstance().isPassCode());
        HelperPreferences.getInstance().putBoolean(SHP_SETTING.FILE_NAME, SHP_SETTING.KEY_LOCK_STARTUP_STATE, false);
        edtSetPasswordText.set("");

        PassCode.getInstance().setPassCode(false);
        PassCode.getInstance().setPattern(false);
        PassCode.getInstance().setPassCode("");

    }

    public void onClickTogglePatternPassCode(View view) {

        if (!isPattern) {
            vgTogglePassCodeVisibility.set(View.GONE);
            txtCreateNewPattern.set(G.context.getResources().getString(R.string.new_pattern_passCode));
            rootPatternPassword.set(View.VISIBLE);
            visibilityCreateNewPattern.set(View.VISIBLE);
        } else {
            PassCode.getInstance().setPassCode(false);
            PassCode.getInstance().setPattern(false);
            PassCode.getInstance().setPassCode("");
            disablePattern();
        }
    }

    private void disablePattern() {

        vgTogglePassCodeVisibility.set(View.VISIBLE);
        visibilityPassCode.set(View.VISIBLE);
        visibilityDescription.set(View.VISIBLE);
        isTogglePatternPassCode.set(false);
        visibilityChangePass.set(View.GONE);
        visibilityTactileFeedback.set(View.GONE);
        isPattern = false;
        PassCode.getInstance().setPassCode(false);
        passCodeStateChangeListener.postValue(PassCode.getInstance().isPassCode());
        HelperPreferences.getInstance().putBoolean(SHP_SETTING.FILE_NAME, SHP_SETTING.KEY_LOCK_STARTUP_STATE, false);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(SHP_SETTING.KEY_PATTERN_TACTILE_DRAWN, true);
        editor.putLong(SHP_SETTING.KEY_TIME_LOCK, 0);
        editor.apply();
    }

    public void onClickChangePassCode(View v) {
        if (realmUserInfo != null) {
            isPassCode = PassCode.getInstance().isPassCode();
            isPattern = PassCode.getInstance().isPattern();
        }
        if (isPassCode) {
            if (isPattern) {
                isChangePattern = true;
                vgTogglePassCodeVisibility.set(View.GONE);
                rootPatternPassword.set(View.VISIBLE);
                visibilityCreateNewPattern.set(View.VISIBLE);
                txtCreateNewPattern.set(G.context.getResources().getString(R.string.new_pattern_passCode));
            } else {
                edtSetPasswordHint.set(G.context.getString(R.string.PIN));
                page = 0;
                edtSetPasswordText.set("");
                vgTogglePassCodeVisibility.set(View.GONE);
                rootEnterPassword.set(View.VISIBLE);
                rootSettingPassword.set(View.GONE);
                rippleOkVisibility.setValue(View.VISIBLE);
                //titlePassCode.setText("PIN");
                titlePassCodeVisibility.set(View.GONE);
                layoutModePassCode.set(View.VISIBLE);
                txtSetPassword.set(G.fragmentActivity.getResources().getString(R.string.enter_change_pass_code));
                if (kindPassword == PIN) {
                    edtSetPasswordInput.set(InputType.TYPE_CLASS_NUMBER);
                } else {
                    edtSetPasswordInput.set(InputType.TYPE_CLASS_TEXT);
                }
            }
        }
    }

    public void onClickChangeVgToggleFingerPrint(View v) {
        DbManager.getInstance().doRealmTask(realm -> {
            AsyncTransaction.executeTransactionWithLoading(fragment.getActivity(), realm, new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    RealmUserInfo realmUserInfo = realm.where(RealmUserInfo.class).findFirst();
                    if (realmUserInfo != null) {
                        if (isFingerPrintCode) {
                            PassCode.getInstance().setFingerPrint(false);
                            isFingerPrint.set(false);
                            isFingerPrintCode = false;
                        } else {
                            PassCode.getInstance().setFingerPrint(true);
                            isFingerPrint.set(true);
                            isFingerPrintCode = true;
                        }
                    }
                }
            });
        });
    }


    public void onClickPatternSetting(View v) {


    }


    public void onClickRippleOk(View v) {

        if (page == 0 && edtSetPasswordText.get().length() > 0) {

            if (edtSetPasswordText.get().length() >= 4) {
                password = edtSetPasswordText.get();
                edtSetPasswordText.set("");
                txtSetPassword.set(G.fragmentActivity.getResources().getString(R.string.re_enter_pass_code));
                page = 1;
            } else {
                AppUtils.closeKeyboard(v);
                AppUtils.error(G.fragmentActivity.getResources().getString(R.string.limit_passcode));
            }

        } else if (page == 1 && edtSetPasswordText.get().length() > 0) {

            if (edtSetPasswordText.get().equals(password)) {
                vgTogglePassCodeVisibility.set(View.VISIBLE);
                rootEnterPassword.set(View.GONE);
                rootSettingPassword.set(View.VISIBLE);
                visibilityPatternLock.set(View.GONE);
                visibilityChangePass.set(View.VISIBLE);
                visibilityDescription.set(View.GONE);

                if (deviceHasFingerPrint) {
                    vgToggleFingerPrintVisibility.set(View.VISIBLE);
                } else {
                    vgToggleFingerPrintVisibility.set(View.GONE);
                }
                rippleOkVisibility.setValue(View.GONE);
                titlePassCode.set(G.fragmentActivity.getResources().getString(R.string.two_step_pass_code));
                titlePassCodeVisibility.set(View.VISIBLE);
                layoutModePassCode.set(View.GONE);

                PassCode.getInstance().setPassCode(true);
                ActivityMain.isLock = false;
                passCodeStateChangeListener.setValue(PassCode.getInstance().isPassCode());

                HelperPreferences.getInstance().putBoolean(SHP_SETTING.FILE_NAME, SHP_SETTING.KEY_LOCK_STARTUP_STATE, false);

                AppUtils.closeKeyboard(v);
                PassCode.getInstance().setPassCode(true);
                PassCode.getInstance().setPattern(false);
                PassCode.getInstance().setPassCode(edtSetPasswordText.get());
                PassCode.getInstance().setKindPassCode(kindPassword);

                isTogglePassCode.set(true);
                edtSetPasswordText.set("");

            } else {
                AppUtils.closeKeyboard(v);
                AppUtils.error(G.fragmentActivity.getResources().getString(R.string.Password_dose_not_match));
            }

        } else if (page == EnterPassword && edtSetPasswordText.get().length() > 0) {

            if (edtSetPasswordText.get().equals(password)) {
                vgTogglePassCodeVisibility.set(View.VISIBLE);
                rootSettingPassword.set(View.VISIBLE);
                visibilityChangePass.set(View.VISIBLE);
                rootEnterPassword.set(View.GONE);
                visibilityPatternLock.set(View.GONE);
                if (deviceHasFingerPrint) {
                    vgToggleFingerPrintVisibility.set(View.VISIBLE);
                } else {
                    vgToggleFingerPrintVisibility.set(View.GONE);
                }
                rippleOkVisibility.setValue(View.GONE);
                titlePassCode.set(G.fragmentActivity.getResources().getString(R.string.two_step_pass_code));
                titlePassCodeVisibility.set(View.VISIBLE);
                layoutModePassCode.set(View.GONE);

                AppUtils.closeKeyboard(v);
            } else {
                AppUtils.closeKeyboard(v);
                AppUtils.error(G.fragmentActivity.getResources().getString(R.string.invalid_password));
                edtSetPasswordText.set("");
            }

        } else {
            AppUtils.closeKeyboard(v);
            AppUtils.error(G.fragmentActivity.getResources().getString(R.string.enter_pass_code));
            edtSetPasswordText.set("");
        }

    }

    public void onClickChangeAutoLock(View v) {

        boolean wrapInScrollView = true;
        final MaterialDialog dialog = new MaterialDialog.Builder(G.fragmentActivity)
                .backgroundColor(Theme.getColor(Theme.key_popup_background))
                .title(G.fragmentActivity.getResources().getString(R.string.auto_lock))
                .customView(R.layout.dialog_auto_lock, wrapInScrollView)
                .negativeColor(Theme.getColor(Theme.key_button_background))
                .positiveColor(Theme.getColor(Theme.key_button_background))
                .positiveText(R.string.B_ok).negativeText(R.string.B_cancel).build();

        View view1 = dialog.getCustomView();

        assert view1 != null;
        numberPickerMinutes = view1.findViewById(R.id.numberPicker);
        numberPickerMinutes.setMinValue(0);
        numberPickerMinutes.setMaxValue(4);
        //numberPickerMinutes.setWrapSelectorWheel(true);
        //numberPickerMinutes.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);

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

        numberPickerMinutes.setFormatter(new net.iGap.module.NumberPicker.Formatter() {
            @Override
            public String format(int value) {
                if (value == 0) {
                    return G.fragmentActivity.getResources().getString(R.string.Disable);
                } else if (value == 1) {
                    return G.fragmentActivity.getResources().getString(R.string.in_1_minutes);
                } else if (value == 2) {
                    return G.fragmentActivity.getResources().getString(R.string.in_5_minutes);
                } else if (value == 3) {
                    return G.fragmentActivity.getResources().getString(R.string.in_1_hours);
                } else if (value == 4) {
                    return G.fragmentActivity.getResources().getString(R.string.in_5_hours);
                }
                return "";
            }
        });

        View btnPositive = dialog.getActionButton(DialogAction.POSITIVE);
        btnPositive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putLong(SHP_SETTING.KEY_TIME_LOCK, numberPickerMinutes.getValue());
                editor.apply();

                int which = numberPickerMinutes.getValue();
                if (which == 0) {
                    editor.putLong(SHP_SETTING.KEY_TIME_LOCK, 0);
                    autoLockText.set(G.fragmentActivity.getResources().getString(R.string.Disable));
                } else if (which == 1) {
                    editor.putLong(SHP_SETTING.KEY_TIME_LOCK, 60);
                    autoLockText.set(G.fragmentActivity.getResources().getString(R.string.in_1_minutes));
                } else if (which == 2) {
                    editor.putLong(SHP_SETTING.KEY_TIME_LOCK, 60 * 5);
                    autoLockText.set(G.fragmentActivity.getResources().getString(R.string.in_5_minutes));
                } else if (which == 3) {
                    editor.putLong(SHP_SETTING.KEY_TIME_LOCK, 60 * 60);
                    autoLockText.set(G.fragmentActivity.getResources().getString(R.string.in_1_hours));
                } else if (which == 4) {
                    editor.putLong(SHP_SETTING.KEY_TIME_LOCK, 60 * 60 * 5);
                    autoLockText.set(G.fragmentActivity.getResources().getString(R.string.in_5_hours));
                }
                editor.apply();
                dialog.dismiss();
            }
        });
        dialog.show();

    }

    public void onClickAllowScreenCapture(View v) {

        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (screenShot) {
            editor.putBoolean(SHP_SETTING.KEY_SCREEN_SHOT_LOCK, false);
            isAllowScreenCapture.set(false);
            screenShot = false;

            try {
                if (G.currentActivity != null) {
                    G.currentActivity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
                }
            } catch (Exception e) {
                HelperLog.getInstance().setErrorLog(e);
            }


        } else {
            editor.putBoolean(SHP_SETTING.KEY_SCREEN_SHOT_LOCK, true);
            isAllowScreenCapture.set(true);
            screenShot = true;
            try {
                if (G.currentActivity != null) {
                    G.currentActivity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_SECURE);
                }
            } catch (Exception e) {
                HelperLog.getInstance().setErrorLog(e);
            }

        }
        editor.apply();

    }

    public void onClickModePassCode(View v) {


        final MaterialDialog dialog = new MaterialDialog.Builder(G.fragmentActivity)
                .backgroundColor(Theme.getColor(Theme.key_popup_background))
                .negativeColor(Theme.getColor(Theme.key_button_background))
                .positiveColor(Theme.getColor(Theme.key_button_background))
                .choiceWidgetColor(ColorStateList.valueOf(Theme.getColor(Theme.key_button_background)))
                .items(R.array.modePassCode).itemsCallback(new MaterialDialog.ListCallback() {
            @Override
            public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {

                switch (which) {
                    case 0:
                        // Whatever you want to happen when the first item gets selected
                        edtSetPasswordInput.set(InputType.TYPE_CLASS_NUMBER);
                        maxLengthEditText(4);
                        kindPassword = PIN;
                        edtSetPasswordHint.set(G.context.getString(R.string.PIN));
                        txtModePassCode.set(G.context.getString(R.string.PIN));
                        break;
                    case 1:
                        // Whatever you want to happen when the second item gets selected
                        edtSetPasswordInput.set(InputType.TYPE_CLASS_TEXT);
                        maxLengthEditText(20);
                        kindPassword = PASSWORD;
                        edtSetPasswordHint.set(G.context.getString(R.string.password));
                        txtModePassCode.set(G.context.getString(R.string.password));
                        break;
                }

            }
        }).build();

        DialogAnimation.animationUp(dialog);
        dialog.show();


    }

    private void getInfo() {

        checkFingerPrint();
        sharedPreferences = G.fragmentActivity.getSharedPreferences(SHP_SETTING.FILE_NAME, MODE_PRIVATE);

        realmUserInfo = DbManager.getInstance().doRealmTask(realm -> {
            return realm.where(RealmUserInfo.class).findFirst();
        });

        binding.patternLockView.addPatternLockListener(mPatternLockViewListener);

        if (realmUserInfo != null) {
            isPassCode = PassCode.getInstance().isPassCode();
            isPattern = PassCode.getInstance().isPattern();
            isFingerPrintCode = PassCode.getInstance().isFingerPrint();
            password = PassCode.getInstance().getPassCode();
            kindPassword = PassCode.getInstance().getKindPassCode();
        }


        if (kindPassword == PIN) {
            edtSetPasswordInput.set(InputType.TYPE_CLASS_NUMBER);
            maxLengthEditText(4);
        } else {
            edtSetPasswordInput.set(InputType.TYPE_CLASS_TEXT);
            maxLengthEditText(20);
        }

        if (isPassCode) {

            if (isPattern) {
                visibilityCreateNewPattern.set(View.GONE);
                isTogglePatternPassCode.set(true);
                visibilityPassCode.set(View.GONE);
                visibilityDescription.set(View.GONE);
                page = EnterPattern;
                vgTogglePassCodeVisibility.set(View.GONE);
                rootPatternPassword.set(View.VISIBLE);
                visibilityTactileFeedback.set(View.VISIBLE);
                txtCreateNewPattern.set("");
                tactile = sharedPreferences.getBoolean(SHP_SETTING.KEY_PATTERN_TACTILE_DRAWN, true);
                isToggleTactileFeedback.set(tactile);

            } else {
                page = EnterPassword;
                vgTogglePassCodeVisibility.set(View.GONE);
                visibilityPatternLock.set(View.GONE);
                visibilityDescription.set(View.GONE);
                rootEnterPassword.set(View.VISIBLE);
                rootSettingPassword.set(View.GONE);
                rippleOkVisibility.setValue(View.VISIBLE);
                txtSetPassword.set(G.fragmentActivity.getResources().getString(R.string.enter_pass_code));
                isTogglePassCode.set(true);
            }
        } else {
            rootSettingPassword.set(View.GONE);
            isTogglePassCode.set(false);
        }


        isFingerPrint.set(isFingerPrintCode);

        screenShot = sharedPreferences.getBoolean(SHP_SETTING.KEY_SCREEN_SHOT_LOCK, true);
        isAllowScreenCapture.set(screenShot);


        long valueNumberPic = sharedPreferences.getLong(SHP_SETTING.KEY_TIME_LOCK, 0);
        if (valueNumberPic == 0) {
            autoLockText.set(G.fragmentActivity.getResources().getString(R.string.Disable));
        } else if (valueNumberPic == 60) {
            autoLockText.set(G.fragmentActivity.getResources().getString(R.string.in_1_minutes));
        } else if (valueNumberPic == 60 * 5) {
            autoLockText.set(G.fragmentActivity.getResources().getString(R.string.in_5_minutes));
        } else if (valueNumberPic == 60 * 60) {
            autoLockText.set(G.fragmentActivity.getResources().getString(R.string.in_1_hours));
        } else if (valueNumberPic == 60 * 60 * 5) {
            autoLockText.set(G.fragmentActivity.getResources().getString(R.string.in_5_hours));
        }

        edtSetPasswordText.set("");

        if (kindPassword == PIN) {
            if (edtSetPasswordText.get().length() == 4) {
                //rippleOk.performClick();
                buttonOk();

            }
        }

    }

    @TargetApi(Build.VERSION_CODES.M)
    private void checkFingerPrint() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            FingerprintManager fingerprintManager = (FingerprintManager) context.getSystemService(Context.FINGERPRINT_SERVICE);
            if (ActivityCompat.checkSelfPermission(G.fragmentActivity, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            if (fingerprintManager != null) {
                if (!fingerprintManager.isHardwareDetected()) {
                    deviceHasFingerPrint = false;
                } else deviceHasFingerPrint = fingerprintManager.hasEnrolledFingerprints();
            }
        }
    }

    private void maxLengthEditText(int numberOfLength) {
        edtSetPasswordMaxLength.set(numberOfLength);
    }

    public void buttonOk() {

        if (page == 0 && edtSetPasswordText.get().length() > 0) {

            if (edtSetPasswordText.get().length() >= 4) {
                password = edtSetPasswordText.get();
                edtSetPasswordText.set("");
                txtSetPassword.set(G.fragmentActivity.getResources().getString(R.string.re_enter_pass_code));
                page = 1;
            } else {
                AppUtils.error(G.fragmentActivity.getResources().getString(R.string.limit_passcode));
            }

        } else if (page == 1 && edtSetPasswordText.get().length() > 0) {

            if (edtSetPasswordText.get().equals(password)) {
                vgTogglePassCodeVisibility.set(View.VISIBLE);
                rootEnterPassword.set(View.GONE);
                rootSettingPassword.set(View.VISIBLE);
                visibilityChangePass.set(View.VISIBLE);
                if (deviceHasFingerPrint) {
                    vgToggleFingerPrintVisibility.set(View.VISIBLE);
                } else {
                    vgToggleFingerPrintVisibility.set(View.GONE);
                }
                rippleOkVisibility.setValue(View.GONE);
                titlePassCode.set(G.fragmentActivity.getResources().getString(R.string.two_step_pass_code));
                titlePassCodeVisibility.set(View.VISIBLE);
                layoutModePassCode.set(View.GONE);

                PassCode.getInstance().setPassCode(true);
                ActivityMain.isLock = false;
                passCodeStateChangeListener.postValue(PassCode.getInstance().isPassCode());
                HelperPreferences.getInstance().putBoolean(SHP_SETTING.FILE_NAME, SHP_SETTING.KEY_LOCK_STARTUP_STATE, false);

                PassCode.getInstance().setPassCode(true);
                PassCode.getInstance().setPattern(false);
                PassCode.getInstance().setPassCode(edtSetPasswordText.get());
                PassCode.getInstance().setKindPassCode(kindPassword);
                edtSetPasswordText.set("");
            } else {
                AppUtils.error(G.fragmentActivity.getResources().getString(R.string.Password_dose_not_match));
            }

        } else if (page == EnterPassword && edtSetPasswordText.get().length() > 0) {

            if (edtSetPasswordText.get().equals(password)) {
                vgTogglePassCodeVisibility.set(View.VISIBLE);
                rootEnterPassword.set(View.GONE);
                rootSettingPassword.set(View.VISIBLE);
                visibilityChangePass.set(View.VISIBLE);
                if (deviceHasFingerPrint) {
                    vgToggleFingerPrintVisibility.set(View.VISIBLE);
                } else {
                    vgToggleFingerPrintVisibility.set(View.GONE);
                }
                rippleOkVisibility.setValue(View.GONE);
                titlePassCode.set(G.fragmentActivity.getResources().getString(R.string.two_step_pass_code));
                titlePassCodeVisibility.set(View.VISIBLE);
                layoutModePassCode.set(View.GONE);

//
            } else {
                AppUtils.error(G.fragmentActivity.getResources().getString(R.string.invalid_password));
                edtSetPasswordText.set("");
            }

        } else {
            AppUtils.error(G.fragmentActivity.getResources().getString(R.string.enter_pass_code));
            edtSetPasswordText.set("");
        }
    }
}
