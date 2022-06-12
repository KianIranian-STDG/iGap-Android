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
import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.ViewCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.andrognito.patternlockview.PatternLockView;
import com.andrognito.patternlockview.listener.PatternLockViewListener;
import com.andrognito.patternlockview.utils.PatternLockUtils;

import net.iGap.helper.HelperAnimation;
import net.iGap.messenger.theme.Theme;
import net.iGap.R;
import net.iGap.databinding.ActivityEnterPassCodeBinding;
import net.iGap.helper.HelperError;
import net.iGap.module.SHP_SETTING;
import net.iGap.module.accountManager.AccountManager;
import net.iGap.observers.eventbus.EventManager;
import net.iGap.viewmodel.ActivityEnterPassCodeViewModel;

import java.util.List;

public class ActivityEnterPassCode extends ActivityEnhanced implements EventManager.EventDelegate {

    private ActivityEnterPassCodeViewModel viewModel;
    private ActivityEnterPassCodeBinding binding;

    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewModel = ViewModelProviders.of(this, new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                return (T) new ActivityEnterPassCodeViewModel(ActivityEnterPassCode.this, getSharedPreferences(SHP_SETTING.FILE_NAME, MODE_PRIVATE).getBoolean(SHP_SETTING.KEY_PATTERN_TACTILE_DRAWN, true));
            }
        }).get(ActivityEnterPassCodeViewModel.class);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_enter_pass_code);
        binding.setLifecycleOwner(this);
        binding.setViewModel(viewModel);
        binding.mainRootEnterPassword.setBackgroundColor(Theme.getColor(Theme.key_window_background));
        binding.passwordEditText.setTextColor(Theme.getColor(Theme.key_default_text));
        binding.passwordEditText.setHintTextColor(Theme.getColor(Theme.key_default_text));
        ColorStateList colorStateList = ColorStateList.valueOf(Theme.getColor(Theme.key_default_text));
        ViewCompat.setBackgroundTintList(binding.passwordEditText, colorStateList);
        binding.lockIcon.setTextColor(Theme.getColor(Theme.key_theme_color));
        binding.lockIcon.setTypeface(ResourcesCompat.getFont(getBaseContext(), R.font.font_icons));
        binding.unlockTitle.setTextColor(Theme.getColor(Theme.key_title_text));
        binding.forgotPasswordButton.setTextColor(Theme.getColor(Theme.key_theme_color));
        binding.biometricPasswordIcon.setTextColor(Theme.getColor(Theme.key_theme_color));
        binding.biometricPasswordIcon.setTypeface(ResourcesCompat.getFont(getBaseContext(), R.font.font_icons));
        binding.fingerprintIcon.setTextColor(Theme.getColor(Theme.key_theme_color));
        binding.patternLockView.setNormalStateColor(Theme.getColor(Theme.key_theme_color));
        binding.v.setBackgroundColor(Theme.getColor(Theme.key_default_text));
        binding.icon.setOutlineSpotShadowColor(Theme.getColor(Theme.key_dark_theme_color));
        binding.icon.setOutlineAmbientShadowColor(Theme.getColor(Theme.key_dark_theme_color));

        viewModel.getInitialPatternView().observe(this, isLinePattern -> {
            if (isLinePattern != null) {

                binding.patternLockView.setViewMode(PatternLockView.PatternViewMode.CORRECT);       // Set the current view more
                binding.patternLockView.setInStealthMode(isLinePattern);                            // Set the pattern in stealth mode (pattern drawing is hidden)
                binding.patternLockView.setTactileFeedbackEnabled(true);                            // Enables vibration feedback when the pattern is drawn
                binding.patternLockView.setInputEnabled(true);                                      // Disables any input from the pattern lock view completely

                binding.patternLockView.setDotCount(3);
                binding.patternLockView.setDotNormalSize((int) getResources().getDimension(R.dimen.dp10));
                binding.patternLockView.setDotSelectedSize((int) getResources().getDimension(R.dimen.dp20));
                binding.patternLockView.setPathWidth((int) getResources().getDimension(R.dimen.pattern_lock_path_width));
                binding.patternLockView.setAspectRatioEnabled(true);
                binding.patternLockView.setAspectRatio(PatternLockView.AspectRatio.ASPECT_RATIO_HEIGHT_BIAS);
                binding.patternLockView.setCorrectStateColor(Theme.getColor(Theme.key_dark_theme_color));
                binding.patternLockView.setWrongStateColor(Theme.getColor(Theme.key_dark_red));
                binding.patternLockView.setDotAnimationDuration(150);
                binding.patternLockView.setPathEndAnimationDuration(200);
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

    }

    @Override
    protected void onStart() {
        super.onStart();
        EventManager.getInstance(AccountManager.selectedAccount).addObserver(EventManager.ON_INPUT_PASS_CODE_INCORRECT, ActivityEnterPassCode.this);
    }

    @Override
    public void onResume() {
        super.onResume();
        viewModel.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventManager.getInstance(AccountManager.selectedAccount).removeObserver(EventManager.ON_INPUT_PASS_CODE_INCORRECT, ActivityEnterPassCode.this);
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

    @Override
    public void receivedEvent(int id, int account, Object... args) {
        if (id == EventManager.ON_INPUT_PASS_CODE_INCORRECT) {
            HelperAnimation.bigAndSmall(binding.lockIcon, 1.5f, 1f);
        }
    }
}
