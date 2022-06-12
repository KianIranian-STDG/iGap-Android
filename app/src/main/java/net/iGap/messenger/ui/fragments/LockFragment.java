package net.iGap.messenger.ui.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.os.Vibrator;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.ViewCompat;

import com.andrognito.patternlockview.PatternLockView;
import com.andrognito.patternlockview.listener.PatternLockViewListener;
import com.andrognito.patternlockview.utils.PatternLockUtils;

import net.iGap.G;
import net.iGap.R;
import net.iGap.activities.ActivityMain;
import net.iGap.fragments.BaseFragment;
import net.iGap.helper.HelperPreferences;
import net.iGap.helper.LayoutCreator;
import net.iGap.messenger.theme.Theme;
import net.iGap.messenger.theme.ThemeDescriptor;
import net.iGap.messenger.ui.components.CodepointsLengthInputFilter;
import net.iGap.messenger.ui.components.EditTextBoldCursor;
import net.iGap.messenger.ui.toolBar.Toolbar;
import net.iGap.model.PassCode;
import net.iGap.module.AppUtils;
import net.iGap.module.SHP_SETTING;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;
import static net.iGap.messenger.ui.fragments.LockSettingFragment.PASSCODE;
import static net.iGap.module.AndroidUtils.showKeyboard;

public class LockFragment extends BaseFragment {

    private final int kindPassword;
    private final String LockType;
    private TextView titleTextView;
    private EditTextBoldCursor passwordEditText;
    private PatternLockView patternLockView;
    private int passCodeSetStep = 0;
    private String password;
    private String strPattern;
    private boolean showLinePattern;
    private final SharedPreferences sharedPreferences = getSharedManager().getSettingSharedPreferences();

    public LockFragment(String lockType) {
        LockType = lockType;
        password = PassCode.getInstance().getPassCode();
        kindPassword = PassCode.getInstance().getKindPassCode();
    }

    @SuppressLint("WrongConstant")
    @Override
    public View createView(Context context) {
        fragmentView = new FrameLayout(context);
        FrameLayout frameLayout = (FrameLayout) fragmentView;

        fragmentView.setOnTouchListener((v, event) -> true);
        titleTextView = new TextView(context);
        titleTextView.setTextColor(Theme.getColor(Theme.key_title_text));
        if (LockType.equals(PASSCODE)) {
            titleTextView.setText(getString(R.string.enter_pass_code));
        } else {
            titleTextView.setText(getString(R.string.new_pattern_passCode));
        }
        titleTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
        titleTextView.setGravity(Gravity.CENTER_HORIZONTAL);
        frameLayout.addView(titleTextView, LayoutCreator.createFrame(LayoutCreator.WRAP_CONTENT, LayoutCreator.WRAP_CONTENT, Gravity.CENTER_HORIZONTAL, 0, 38, 0, 0));

        if (LockType.equals(PASSCODE)) {
            passwordEditText = new EditTextBoldCursor(context);
            passwordEditText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
            passwordEditText.setHintTextColor(Theme.getColor(Theme.key_default_text));
            passwordEditText.setTypeface(ResourcesCompat.getFont(context, R.font.main_font));
            passwordEditText.setTextColor(Theme.getColor(Theme.key_default_text));
            ColorStateList colorStateList = ColorStateList.valueOf(Theme.getColor(Theme.key_default_text));
            ViewCompat.setBackgroundTintList(passwordEditText, colorStateList);
            passwordEditText.setBackground(Theme.createEditTextDrawable(context, false));
            passwordEditText.setMaxLines(1);
            passwordEditText.setPadding(LayoutCreator.dp(G.isAppRtl ? 24 : 0), 0, LayoutCreator.dp(G.isAppRtl ? 0 : 24), LayoutCreator.dp(6));
            passwordEditText.setGravity(G.isAppRtl ? Gravity.RIGHT : Gravity.LEFT);
            passwordEditText.setImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI);
            passwordEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
            if (passCodeSetStep == 0) {
                passwordEditText.setImeOptions(EditorInfo.IME_ACTION_NEXT);
            } else {
                passwordEditText.setImeOptions(EditorInfo.IME_ACTION_DONE);
            }
            InputFilter[] inputFilters = new InputFilter[1];
            inputFilters[0] = new CodepointsLengthInputFilter(70) {
                @Override
                public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                    if (source != null && TextUtils.indexOf(source, '\n') != -1) {
                        return "";
                    }
                    CharSequence result = super.filter(source, start, end, dest, dstart, dend);
                    if (result != null && source != null && result.length() != source.length()) {
                        Vibrator v = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
                        if (v != null) {
                            v.vibrate(200);
                        }
                    }
                    return result;
                }
            };
            passwordEditText.setFilters(inputFilters);
            passwordEditText.setMinHeight(LayoutCreator.dp(36));
            passwordEditText.setCursorColor(Theme.getColor(Theme.key_default_text));
            passwordEditText.setCursorSize(LayoutCreator.dp(20));
            passwordEditText.setCursorWidth(1.5f);
            passwordEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
            passwordEditText.setOnEditorActionListener((textView, i, keyEvent) -> {
                if (passCodeSetStep == 0) {
                    processNextPassCode();
                    return true;
                } else if (passCodeSetStep == 1) {
                    processDonePassCode(textView);
                    return true;
                }
                return false;
            });
            passwordEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (passwordEditText.length() == 4) {
                        if (passCodeSetStep == 0) {
                            processNextPassCode();
                        } else if (passCodeSetStep == 1) {
                            processDonePassCode(passwordEditText);
                        }
                    }
                }
            });
            frameLayout.addView(passwordEditText, LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, 36, Gravity.TOP | Gravity.LEFT, 40, 90, 40, 0));
        } else {
            showLinePattern = sharedPreferences.getBoolean(SHP_SETTING.KEY_PATTERN_TACTILE_DRAWN, true);
            patternLockView = new PatternLockView(context);
            patternLockView.setViewMode(PatternLockView.PatternViewMode.CORRECT);
            patternLockView.setNormalStateColor(Theme.getColor(Theme.key_button_background));
            patternLockView.setCorrectStateColor(Theme.getColor(Theme.key_dark_theme_color));
            patternLockView.setWrongStateColor(Theme.getColor(Theme.key_dark_red));
            patternLockView.setDotAnimationDuration(150);
            patternLockView.setPathEndAnimationDuration(200);
            patternLockView.setDotCount(3);
            patternLockView.setDotNormalSize((int) getResources().getDimension(R.dimen.dp10));
            patternLockView.setDotSelectedSize((int) getResources().getDimension(R.dimen.dp20));
            patternLockView.setPathWidth((int) getResources().getDimension(R.dimen.pattern_lock_path_width));
            patternLockView.setAspectRatioEnabled(true);
            patternLockView.setAspectRatio(PatternLockView.AspectRatio.ASPECT_RATIO_SQUARE);
            patternLockView.setTactileFeedbackEnabled(true);
            patternLockView.setInputEnabled(true);
            patternLockView.setInStealthMode(!showLinePattern);
            frameLayout.addView(patternLockView, LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, LayoutCreator.WRAP_CONTENT, Gravity.CENTER, 32, 32, 32, 32));
            patternLockView.addPatternLockListener(new PatternLockViewListener() {
                @Override
                public void onStarted() {
                }

                @Override
                public void onProgress(List<PatternLockView.Dot> progressPattern) {
                }

                @Override
                public void onComplete(List<PatternLockView.Dot> pattern) {
                    if (passCodeSetStep == 0) {
                        strPattern = PatternLockUtils.patternToString(patternLockView, pattern);
                        processNextPattern();
                    } else if (passCodeSetStep == 1) {
                        processDonePattern(patternLockView, PatternLockUtils.patternToString(patternLockView, pattern));
                    }
                    patternLockView.clearPattern();
                }

                @Override
                public void onCleared() {
                }
            });
        }
        fragmentView.setBackgroundColor(Theme.getColor(Theme.key_window_background));
        return fragmentView;
    }

    @Override
    public void onResume() {
        super.onResume();
       // passwordEditText.requestFocus();
        showKeyboard(passwordEditText);
    }

    private void processDonePassCode(View view) {
        if (passwordEditText.getText().toString().equals(password)) {
            PassCode.getInstance().setPassCode(true);
            ActivityMain.isLock = false;
            HelperPreferences.getInstance().putBoolean(SHP_SETTING.FILE_NAME, SHP_SETTING.KEY_LOCK_STARTUP_STATE, false);
            AppUtils.closeKeyboard(view);
            PassCode.getInstance().setPassCode(true);
            PassCode.getInstance().setPattern(false);
            PassCode.getInstance().setPassCode(passwordEditText.getText().toString());
            PassCode.getInstance().setKindPassCode(kindPassword);
            passwordEditText.setText("");
            if (getActivity() != null) {
                ((ActivityMain) getActivity()).updatePassCodeState();
            }
            finish();
        } else {
            AppUtils.closeKeyboard(view);
            AppUtils.error(G.fragmentActivity.getResources().getString(R.string.Password_dose_not_match));
        }
    }

    private void processNextPassCode() {
        passCodeSetStep = 1;
        password = passwordEditText.getText().toString();
        passwordEditText.setText("");
        titleTextView.setText(context.getString(R.string.re_enter_pass_code));
        toolbar.setTitle(context.getString(R.string.re_enter_pass_code));
        passwordEditText.requestFocus();
        showKeyboard(passwordEditText);
    }

    private void processDonePattern(View view, String pattern) {
        if (strPattern.equals(pattern)) {
            PassCode.getInstance().setPassCode(true);
            PassCode.getInstance().setPattern(true);
            PassCode.getInstance().setPassCode(true);
            PassCode.getInstance().setPassCode(strPattern);
            ActivityMain.isLock = false;
            HelperPreferences.getInstance().putBoolean(SHP_SETTING.FILE_NAME, SHP_SETTING.KEY_LOCK_STARTUP_STATE, false);
            AppUtils.closeKeyboard(view);
            if (getActivity() != null) {
                ((ActivityMain) getActivity()).updatePassCodeState();
            }
            finish();
        } else {
            AppUtils.closeKeyboard(view);
            AppUtils.error(G.fragmentActivity.getResources().getString(R.string.invalid_password));
            patternLockView.clearPattern();
        }
    }

    private void processNextPattern() {
        passCodeSetStep = 1;
        titleTextView.setText(getResources().getString(R.string.repeat_pattern_passCode));
        toolbar.setTitle(context.getString(R.string.repeat_pattern_passCode));
        patternLockView.clearPattern();
    }

    @SuppressLint("ResourceType")
    @Override
    public View createToolBar(Context context) {
        toolbar = new Toolbar(context);
        toolbar.setBackIcon(R.drawable.ic_ab_back);
        if (LockType.equals(PASSCODE)) {
            toolbar.setTitle(context.getString(R.string.enter_pass_code));
        } else {
            toolbar.setTitle(context.getString(R.string.new_pattern_passCode));
        }
        toolbar.setListener(id -> {
            if (id == -1) {
                finish();
            }
        });
        return toolbar;
    }

    @Override
    public List<ThemeDescriptor> getThemeDescriptor() {
        List<ThemeDescriptor> themeDescriptors = new ArrayList<>();
        themeDescriptors.add(new ThemeDescriptor(fragmentView, ThemeDescriptor.FLAG_BACKGROUND, Theme.key_window_background));
        themeDescriptors.add(new ThemeDescriptor(titleTextView, ThemeDescriptor.FLAG_TEXTCOLOR, Theme.key_default_text));
        themeDescriptors.add(new ThemeDescriptor(passwordEditText, ThemeDescriptor.FLAG_TEXTCOLOR, Theme.key_default_text));
        themeDescriptors.add(new ThemeDescriptor(passwordEditText, ThemeDescriptor.FLAG_CURSORCOLOR, Theme.key_default_text));
        themeDescriptors.add(new ThemeDescriptor(passwordEditText, ThemeDescriptor.FLAG_HINTTEXTCOLOR, Theme.key_default_text));
        themeDescriptors.add(new ThemeDescriptor(passwordEditText, ThemeDescriptor.FLAG_PROGRESSBAR, Theme.key_default_text));
        themeDescriptors.add(new ThemeDescriptor(toolbar, ThemeDescriptor.FLAG_BACKGROUND, Theme.key_toolbar_background));
        return themeDescriptors;
    }
}
