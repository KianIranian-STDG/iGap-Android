package net.iGap.activities;
/*
 * This is the source code of iGap for Android
 * It is licensed under GNU AGPL v3.0
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright © 2017 , iGap - www.iGap.net
 * iGap Messenger | Free, Fast and Secure instant messaging application
 * The idea of the Kianiranian Company - www.kianiranian.com
 * All rights reserved.
 */

import android.content.Context;
import android.content.Intent;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.andrognito.patternlockview.PatternLockView;
import com.andrognito.patternlockview.listener.PatternLockViewListener;
import com.andrognito.patternlockview.utils.PatternLockUtils;

import net.iGap.R;
import net.iGap.databinding.ActivityEnterPassCodeBinding;
import net.iGap.helper.HelperError;
import net.iGap.module.FingerprintHandler;
import net.iGap.module.SHP_SETTING;
import net.iGap.observers.interfaces.FingerPrint;
import net.iGap.viewmodel.ActivityEnterPassCodeViewModel;

import java.util.List;

public class ActivityEnterPassCode extends ActivityEnhanced {

    private ActivityEnterPassCodeViewModel viewModel;
    private ActivityEnterPassCodeBinding binding;

    private FingerprintManager fingerprintManager;
    private FingerprintHandler helper;

    private MaterialDialog fingerPrintDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewModel = ViewModelProviders.of(this, new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                return (T) new ActivityEnterPassCodeViewModel(getSharedPreferences(SHP_SETTING.FILE_NAME, MODE_PRIVATE).getBoolean(SHP_SETTING.KEY_PATTERN_TACTILE_DRAWN, true));
            }
        }).get(ActivityEnterPassCodeViewModel.class);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_enter_pass_code);
        binding.setLifecycleOwner(this);
        binding.setViewModel(viewModel);

        viewModel.getInitialPatternView().observe(this, isLinePattern -> {
            if (isLinePattern != null) {
                binding.patternLockView.setViewMode(PatternLockView.PatternViewMode.CORRECT);       // Set the current view more
                binding.patternLockView.setInStealthMode(isLinePattern);                            // Set the pattern in stealth mode (pattern drawing is hidden)
                binding.patternLockView.setTactileFeedbackEnabled(true);                            // Enables vibration feedback when the pattern is drawn
                binding.patternLockView.setInputEnabled(true);                                      // Disables any input from the pattern lock view completely

                binding.patternLockView.setDotCount(4);
                binding.patternLockView.setDotNormalSize((int) getResources().getDimension(R.dimen.dp22));
                binding.patternLockView.setDotSelectedSize((int) getResources().getDimension(R.dimen.dp32));
                binding.patternLockView.setPathWidth((int) getResources().getDimension(R.dimen.pattern_lock_path_width));
                binding.patternLockView.setAspectRatioEnabled(true);
                binding.patternLockView.setAspectRatio(PatternLockView.AspectRatio.ASPECT_RATIO_HEIGHT_BIAS);
                binding.patternLockView.setNormalStateColor(getResources().getColor(R.color.white));
                binding.patternLockView.setCorrectStateColor(getResources().getColor(R.color.white));
                binding.patternLockView.setWrongStateColor(getResources().getColor(R.color.red));
                binding.patternLockView.setDotAnimationDuration(150);
                binding.patternLockView.setPathEndAnimationDuration(100);
            }
        });

        viewModel.getShowErrorMessage().observe(this, messageResId -> {
            if (messageResId != null) {
                HelperError.showSnackMessage(getString(messageResId), true);
            }
        });

        viewModel.getGoBack().observe(this, isGoBack -> {
            if (isGoBack != null) {
                if (!isGoBack) {
                    Log.wtf(this.getClass().getName(), "getGoBack");
                    ActivityMain.finishActivity.finishActivity();
                }
                finish();
            }
        });

        viewModel.getHideKeyword().observe(this, isHide -> {
            if (isHide != null && isHide) {
                hideKeyboard();
            }
        });

        viewModel.getShowDialogForgetPassword().observe(this, showDialog -> {
            if (showDialog != null && showDialog) {
                new MaterialDialog.Builder(this).title(R.string.forgot_pin_title).content(R.string.forgot_pin_desc).positiveText(R.string.ok).onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        viewModel.forgetPassword();
                    }
                }).negativeText(R.string.cancel).build().show();
            }
        });

        viewModel.getShowDialogFingerPrint().observe(this, cryptoObject -> {
            if (cryptoObject != null) {
                if (fingerPrintDialog != null && fingerPrintDialog.isShowing()) {
                    fingerPrintDialog.dismiss();
                }
                fingerPrintDialog = new MaterialDialog.Builder(this).title(R.string.FingerPrint).customView(R.layout.dialog_finger_print, true).onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                            helper.stopListening();
                        }
                    }
                }).negativeText(R.string.B_cancel).build();

                View viewDialog = fingerPrintDialog.getView();

                AppCompatTextView iconFingerPrint = viewDialog.findViewById(R.id.iconDialogTitle);
                AppCompatTextView textFingerPrint = viewDialog.findViewById(R.id.txtDialogTitle);
                fingerPrintDialog.show();
                helper = new FingerprintHandler(this, new FingerPrint() {
                    @Override
                    public void success() {
                        viewModel.passwordCorrect();
                        fingerPrintDialog.dismiss();
                    }

                    @Override
                    public void error() {
                        if (fingerPrintDialog.isShowing()) {
                            if (iconFingerPrint != null && textFingerPrint != null) {
                                iconFingerPrint.setTextColor(getResources().getColor(R.color.red));
                                textFingerPrint.setTextColor(getResources().getColor(R.color.red));
                                textFingerPrint.setText(R.string.Fingerprint_not_recognized);
                            }
                        }
                    }
                });
                helper.startAuth(fingerprintManager, cryptoObject);
            }
        });

        viewModel.getClearPassword().observe(this, isClear -> {
            if (isClear != null && isClear) {
                binding.passwordEditText.setText("");
            }
        });

        viewModel.getGoToRegisterPage().observe(this, isGo -> {
            if (isGo != null && isGo) {
                startActivity(new Intent(ActivityEnterPassCode.this, ActivityRegistration.class));
                onBackPressed();
            }
        });

        binding.patternLockView.addPatternLockListener(new PatternLockViewListener() {
            @Override
            public void onStarted() {
            }

            @Override
            public void onProgress(List<PatternLockView.Dot> progressPattern) {
            }

            @Override
            public void onComplete(List<PatternLockView.Dot> pattern) {
                viewModel.onCheckPasswordButtonClick(PatternLockUtils.patternToString(binding.patternLockView, pattern));
                binding.patternLockView.clearPattern();
            }

            @Override
            public void onCleared() {
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            fingerprintManager = (FingerprintManager) getSystemService(FINGERPRINT_SERVICE);
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        viewModel.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            if (helper != null) {
                helper.stopListening();
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (ActivityMain.finishActivity != null) {
            ActivityMain.finishActivity.finishActivity();
        }
        finish();
    }

    private void hideKeyboard() {
        View view = getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

}
