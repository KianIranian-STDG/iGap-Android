package net.iGap.messenger.ui.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.util.TypedValue;
import android.view.ActionMode;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.core.content.res.ResourcesCompat;

import net.iGap.G;
import net.iGap.R;
import net.iGap.fragments.BaseFragment;
import net.iGap.helper.HelperFragment;
import net.iGap.helper.LayoutCreator;
import net.iGap.messenger.dialog.AlertDialog;
import net.iGap.messenger.theme.Theme;
import net.iGap.messenger.theme.ThemeDescriptor;
import net.iGap.messenger.ui.components.EditTextBoldCursor;
import net.iGap.messenger.ui.toolBar.Toolbar;
import net.iGap.messenger.ui.toolBar.ToolbarItems;
import net.iGap.observers.interfaces.RecoveryEmailCallback;
import net.iGap.observers.interfaces.TwoStepVerificationRecoverPasswordByToken;
import net.iGap.request.RequestUserTwoStepVerificationRecoverPasswordByToken;
import net.iGap.request.RequestUserTwoStepVerificationRequestRecoveryToken;
import net.iGap.request.RequestUserTwoStepVerificationSetPassword;
import net.iGap.request.RequestUserTwoStepVerificationVerifyRecoveryEmail;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import static net.iGap.module.AndroidUtils.showKeyboard;
import static net.iGap.module.AppUtils.error;

public class TwoStepVerificationStepsFragment extends BaseFragment {

    private final static int done_button = 1;
    private View doneButton;
    private int step;
    private TextView buttonTextView;
    private TextView titleTextView;
    private TextView descriptionText;
    private TextView descriptionText2;
    private TextView descriptionText3;
    private EditTextBoldCursor passwordEditText;
    private ScrollView scrollView;
    private String question1;
    private String answer1;
    private String question2;
    private String answer2;

    public static final int TYPE_ENTER_FIRST = 0;
    public static final int TYPE_ENTER_SECOND = 1;
    public static final int TYPE_ENTER_HINT = 2;
    public static final int TYPE_ENTER_QUESTION = 3;
    public static final int TYPE_ENTER_EMAIL = 4;
    public static final int TYPE_EMAIL_CONFIRM = 5;
    public static final int TYPE_EMAIL_RECOVERY = 6;

    private String password;
    private String hint;
    private String oldPassword;

    public TwoStepVerificationStepsFragment(int step) {
        this.step = step;
    }

    @Override
    public View createView(Context context) {
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            oldPassword = bundle.getString("OLD_PASSWORD");
        } else {
            oldPassword = "";
        }

        titleTextView = new TextView(context);
        titleTextView.setTextColor(Theme.getColor(Theme.key_default_text));
        titleTextView.setGravity(Gravity.CENTER_HORIZONTAL);
        titleTextView.setPadding(LayoutCreator.dp(32), 0, LayoutCreator.dp(32), 0);
        titleTextView.setTypeface(ResourcesCompat.getFont(context, R.font.main_font_bold));
        titleTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
        titleTextView.setText(getString(R.string.enter_a_password));

        descriptionText = new TextView(context);
        descriptionText.setTextColor(Theme.getColor(Theme.key_subtitle_text));
        descriptionText.setGravity(Gravity.CENTER_HORIZONTAL);
        descriptionText.setLineSpacing(LayoutCreator.dp(2), 1);
        descriptionText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
        descriptionText.setVisibility(View.GONE);
        descriptionText.setTypeface(ResourcesCompat.getFont(context, R.font.main_font));
        descriptionText.setPadding(LayoutCreator.dp(32), 0, LayoutCreator.dp(32), 0);

        descriptionText2 = new TextView(context);
        descriptionText2.setTextColor(Theme.getColor(Theme.key_subtitle_text));
        descriptionText2.setGravity(Gravity.CENTER_HORIZONTAL);
        descriptionText2.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
        descriptionText2.setLineSpacing(LayoutCreator.dp(2), 1);
        descriptionText2.setTypeface(ResourcesCompat.getFont(context, R.font.main_font));
        descriptionText2.setPadding(LayoutCreator.dp(32), 0, LayoutCreator.dp(32), 0);
        descriptionText2.setVisibility(View.GONE);

        descriptionText3 = new TextView(context);
        descriptionText3.setTextColor(Theme.getColor(Theme.key_subtitle_text));
        descriptionText3.setGravity(Gravity.CENTER_HORIZONTAL);
        descriptionText3.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
        descriptionText3.setLineSpacing(LayoutCreator.dp(2), 1);
        descriptionText3.setPadding(LayoutCreator.dp(32), 0, LayoutCreator.dp(32), 0);
        descriptionText3.setText(getString(R.string.RestoreEmailTroubleNoEmail));
        descriptionText3.setTypeface(ResourcesCompat.getFont(context, R.font.main_font));
        descriptionText3.setVisibility(View.GONE);

        buttonTextView = new TextView(context);
        buttonTextView.setText(getString(R.string.Continue));
        buttonTextView.setMinWidth(LayoutCreator.dp(220));
        buttonTextView.setPadding(LayoutCreator.dp(34), 0, LayoutCreator.dp(34), 0);
        buttonTextView.setGravity(Gravity.CENTER);
        buttonTextView.setTextColor(Theme.getColor(Theme.key_button_text));
        buttonTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
        buttonTextView.setTypeface(ResourcesCompat.getFont(context, R.font.main_font));
        buttonTextView.setBackground(Theme.createSimpleSelectorRoundRectDrawable(LayoutCreator.dp(4), Theme.getColor(Theme.key_button_background), Theme.getColor(Theme.key_button_background)));
        buttonTextView.setOnClickListener(v -> {
            switch (step) {
                case TYPE_ENTER_FIRST:
                    if (passwordEditText.getText().length() >= 2) {
                        step = TYPE_ENTER_SECOND;
                        password = passwordEditText.getText().toString();
                        passwordEditText.setText("");
                        passwordEditText.requestFocus();
                        titleTextView.setText(getString(R.string.please_re_enter_your_password));
                    } else {
                        passwordEditText.setText("");
                        closeKeyboard(v);
                        error(getString(R.string.Password_has_to_mor_than_character));
                    }
                    break;
                case TYPE_ENTER_SECOND:
                    if (passwordEditText.getText().length() >= 2) {
                        if (password.equals(passwordEditText.getText().toString())) {
                            step = TYPE_ENTER_HINT;
                            password = passwordEditText.getText().toString();
                            passwordEditText.setText("");
                            passwordEditText.requestFocus();
                            toolbar.setTitle(getString(R.string.password_hint));
                            passwordEditText.setHint(getString(R.string.password_hint));
                            titleTextView.setText(getString(R.string.please_create_hint_for_your_password));

                        } else {
                            closeKeyboard(v);
                            error(getString(R.string.Password_dose_not_match));
                        }
                    } else {
                        closeKeyboard(v);
                        error(getString(R.string.Password_has_to_mor_than_character));
                    }
                    break;
                case TYPE_ENTER_HINT:
                    if (passwordEditText.length() > 0) {
                        if (!password.equals(passwordEditText.getText().toString())) {
                            hint = passwordEditText.getText().toString();
                            step = TYPE_ENTER_QUESTION;
                            new HelperFragment(getActivity().getSupportFragmentManager(), new TwoStepVerificationQuestionFragment("","",new QuestionListener() {
                                @Override
                                public void questionDetailsInfo(String q1, String a1, String q2, String a2) {
                                    step = TYPE_ENTER_EMAIL;
                                    question1 = q1;
                                    answer1 = a1;
                                    question2 = q2;
                                    answer2 = a2;
                                    toolbar.setTitle(getString(R.string.recovery_email));
                                    titleTextView.setText(getString(R.string.email));
                                    buttonTextView.setText(getString(R.string.Continue));
                                    passwordEditText.setText("");
                                    passwordEditText.setHint(getString(R.string.email));
                                    passwordEditText.requestFocus();
                                    passwordEditText.setImeOptions(EditorInfo.IME_ACTION_NEXT | EditorInfo.IME_FLAG_NO_EXTRACT_UI);
                                    passwordEditText.setInputType(EditorInfo.TYPE_CLASS_TEXT | EditorInfo.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                                    descriptionText.setText(getString(R.string.your_email_desc));
                                    descriptionText.setVisibility(View.VISIBLE);
                                    descriptionText3.setVisibility(View.VISIBLE);
                                    descriptionText3.setOnClickListener(v -> showAlertWithText(getString(R.string.RestorePasswordNoEmailTitle),getString(R.string.RestoreEmailTroubleText)));
                                }
                            })).setReplace(false).load();
                        } else {
                            closeKeyboard(v);
                            error(getString(R.string.Hint_cant_the_same_password));
                        }
                    } else {
                        closeKeyboard(v);
                        error(getString(R.string.please_set_hint));
                    }
                    break;
                case TYPE_ENTER_EMAIL:
                    if (passwordEditText.getText() != null && passwordEditText.getText().toString().length() > 0) {
                        Pattern EMAIL_ADDRESS = patternEmail();
                        if (EMAIL_ADDRESS.matcher(passwordEditText.getText().toString()).matches()) {
                            step = TYPE_EMAIL_CONFIRM;
                            new RequestUserTwoStepVerificationSetPassword().setPassword(oldPassword, password, passwordEditText.getText().toString(), question1, answer1, question2, answer2, hint);
                            toolbar.setTitle(getString(R.string.VerificationCode));
                            titleTextView.setText(getString(R.string.VerificationCode));
                            buttonTextView.setText(getString(R.string.Continue));
                            passwordEditText.setText("");
                            passwordEditText.setHint(getString(R.string.EnterCode));
                            passwordEditText.requestFocus();
                            passwordEditText.setInputType(InputType.TYPE_CLASS_PHONE);
                            passwordEditText.setImeOptions(EditorInfo.IME_ACTION_DONE | EditorInfo.IME_FLAG_NO_EXTRACT_UI);
                            descriptionText2.setText(getString(R.string.EmailPasswordConfirmText2));
                            descriptionText2.setVisibility(View.VISIBLE);
                            descriptionText.setText(getString(R.string.your_resend_email_skip));
                            descriptionText.setVisibility(View.VISIBLE);
                            descriptionText.setTextColor(Theme.getColor(Theme.key_white));
                            buttonTextView.setVisibility(View.INVISIBLE);
                            buttonTextView.setAlpha(0.0f);
                            buttonTextView.setScaleX(0.9f);
                            buttonTextView.setScaleY(0.9f);
                            ToolbarItems toolbarItems = toolbar.createToolbarItems();
                            doneButton = toolbarItems.addItem(done_button, R.string.icon_check_ok, Color.WHITE);
                            doneButton.setContentDescription(context.getString(R.string.Done));
                            descriptionText3.setVisibility(View.GONE);
                        } else {
                            closeKeyboard(v);
                            error(getString(R.string.invalid_email));
                        }
                    } else {
                        closeKeyboard(v);
                        error(getString(R.string.invalid_email));
                    }
                    break;
            }
        });
        ViewGroup container = new ViewGroup(context) {
                @Override
                protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                    int width = MeasureSpec.getSize(widthMeasureSpec);
                    int height = MeasureSpec.getSize(heightMeasureSpec);
                    scrollView.measure(MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY), heightMeasureSpec);
                    setMeasuredDimension(width, height);
                }

                @Override
                protected void onLayout(boolean changed, int l, int t, int r, int b) {
                    scrollView.layout(0, 0, scrollView.getMeasuredWidth(), scrollView.getMeasuredHeight());
                }
            };
            scrollView = new ScrollView(context) {
                private int[] location = new int[2];
                private Rect tempRect = new Rect();
                private boolean isLayoutDirty = true;
                private int scrollingUp;

                @Override
                protected void onScrollChanged(int l, int t, int oldl, int oldt) {
                    super.onScrollChanged(l, t, oldl, oldt);
                    if (titleTextView == null) {
                        return;
                    }
                    titleTextView.getLocationOnScreen(location);
                }

                @Override
                public void scrollToDescendant(View child) {
                    child.getDrawingRect(tempRect);
                    offsetDescendantRectToMyCoords(child, tempRect);
                    tempRect.bottom += LayoutCreator.dp(120);
                    int scrollDelta = computeScrollDeltaToGetChildRectOnScreen(tempRect);
                    if (scrollDelta < 0) {
                        scrollDelta -= (scrollingUp = (getMeasuredHeight() - child.getMeasuredHeight()) / 2);
                    } else {
                        scrollingUp = 0;
                    }
                    if (scrollDelta != 0) {
                        smoothScrollBy(0, scrollDelta);
                    }
                }

                @Override
                public void requestChildFocus(View child, View focused) {
                    if (Build.VERSION.SDK_INT < 29) {
                        if (focused != null && !isLayoutDirty) {
                            scrollToDescendant(focused);
                        }
                    }
                    super.requestChildFocus(child, focused);
                }

                @Override
                public boolean requestChildRectangleOnScreen(View child, Rect rectangle, boolean immediate) {
                    if (Build.VERSION.SDK_INT < 23) {
                        rectangle.bottom += LayoutCreator.dp(120);

                        if (scrollingUp != 0) {
                            rectangle.top -= scrollingUp;
                            rectangle.bottom -= scrollingUp;
                            scrollingUp = 0;
                        }
                    }
                    return super.requestChildRectangleOnScreen(child, rectangle, immediate);
                }

                @Override
                public void requestLayout() {
                    isLayoutDirty = true;
                    super.requestLayout();
                }

                @Override
                protected void onLayout(boolean changed, int l, int t, int r, int b) {
                    isLayoutDirty = false;
                    super.onLayout(changed, l, t, r, b);
                }
            };
            scrollView.setVerticalScrollBarEnabled(false);
            container.addView(scrollView);

            LinearLayout scrollViewLinearLayout = new LinearLayout(context);
            scrollViewLinearLayout.setOrientation(LinearLayout.VERTICAL);
            scrollView.addView(scrollViewLinearLayout, LayoutCreator.createScroll(LayoutCreator.MATCH_PARENT, LayoutCreator.MATCH_PARENT, Gravity.LEFT | Gravity.TOP));
            scrollViewLinearLayout.addView(titleTextView, LayoutCreator.createLinear(LayoutCreator.WRAP_CONTENT, LayoutCreator.WRAP_CONTENT, Gravity.CENTER_HORIZONTAL | Gravity.TOP, 0, 8, 0, 0));
            scrollViewLinearLayout.addView(descriptionText, LayoutCreator.createLinear(LayoutCreator.WRAP_CONTENT, LayoutCreator.WRAP_CONTENT, Gravity.CENTER_HORIZONTAL | Gravity.TOP, 0, 9, 0, 0));

            FrameLayout frameLayout = new FrameLayout(context);
            scrollViewLinearLayout.addView(frameLayout, LayoutCreator.createLinear(220, 36, Gravity.TOP | Gravity.CENTER_HORIZONTAL, 40, 32, 40, 0));

            passwordEditText = new EditTextBoldCursor(context);
            passwordEditText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 17);
            passwordEditText.setPadding(0, LayoutCreator.dp(2), 0, 0);
            passwordEditText.setHintTextColor(Theme.getColor(Theme.key_default_text));
            passwordEditText.setCursorColor(Theme.getColor(Theme.key_default_text));
            passwordEditText.setTextColor(Theme.getColor(Theme.key_default_text));
            passwordEditText.setHintColor(Theme.getColor(Theme.key_default_text));
            passwordEditText.setBackground(Theme.createEditTextDrawable(context, false));
            passwordEditText.setMaxLines(1);
            passwordEditText.setLines(1);
            passwordEditText.setGravity(Gravity.CENTER);
            passwordEditText.setCursorSize(LayoutCreator.dp(20));
            passwordEditText.setSingleLine(true);
            passwordEditText.setTypeface(ResourcesCompat.getFont(context, R.font.main_font));
            passwordEditText.setCursorWidth(1.5f);
            passwordEditText.setHint(getString(R.string.LoginPassword));
            passwordEditText.setImeOptions(EditorInfo.IME_ACTION_NEXT | EditorInfo.IME_FLAG_NO_EXTRACT_UI);
            passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            passwordEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
            passwordEditText.setPadding(0, LayoutCreator.dp(2), LayoutCreator.dp(36), 0);
            frameLayout.addView(passwordEditText, LayoutCreator.createFrame(400, 36, Gravity.TOP | Gravity.CENTER_HORIZONTAL));
            passwordEditText.setOnEditorActionListener((textView, i, keyEvent) -> {
                if (i == EditorInfo.IME_ACTION_NEXT || i == EditorInfo.IME_ACTION_DONE) {
                    buttonTextView.callOnClick();
                    return true;
                }
                return false;
            });
            passwordEditText.setCustomSelectionActionModeCallback(new ActionMode.Callback() {
                public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                    return false;
                }

                public void onDestroyActionMode(ActionMode mode) {
                }

                public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                    return false;
                }

                public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                    return false;
                }
            });
            FrameLayout frameLayout2 = new FrameLayout(context);
            scrollViewLinearLayout.addView(frameLayout2, LayoutCreator.createLinear(LayoutCreator.MATCH_PARENT, LayoutCreator.WRAP_CONTENT, Gravity.LEFT | Gravity.TOP, 0, 36, 0, 22));
            frameLayout2.addView(buttonTextView, LayoutCreator.createFrame(LayoutCreator.WRAP_CONTENT, 42, Gravity.CENTER_HORIZONTAL | Gravity.TOP));
            frameLayout2.addView(descriptionText2, LayoutCreator.createFrame(LayoutCreator.WRAP_CONTENT, LayoutCreator.WRAP_CONTENT, Gravity.CENTER_HORIZONTAL | Gravity.TOP));
            scrollViewLinearLayout.addView(descriptionText3, LayoutCreator.createLinear(LayoutCreator.WRAP_CONTENT, LayoutCreator.WRAP_CONTENT, Gravity.CENTER_HORIZONTAL | Gravity.TOP, 0, 0, 0, 25));
        fragmentView = container;
        fragmentView.setBackgroundColor(Theme.getColor(Theme.key_window_background));
        if (passwordEditText != null) {
            passwordEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) { }

                @Override
                public void afterTextChanged(Editable s) { }
            });
        }
        if (step == TYPE_EMAIL_RECOVERY) {
            G.onRecoverySecurityPassword = pattern -> G.handler.post(new Runnable() {
                @Override
                public void run() {
                    passwordEditText.setHint(pattern);
                }
            });
            new RequestUserTwoStepVerificationRequestRecoveryToken().requestRecoveryToken();
            toolbar.setTitle(getString(R.string.VerificationCode));
            titleTextView.setText(getString(R.string.VerificationCode));
            passwordEditText.setText("");
            passwordEditText.requestFocus();
            passwordEditText.setInputType(InputType.TYPE_CLASS_PHONE);
            passwordEditText.setImeOptions(EditorInfo.IME_ACTION_DONE | EditorInfo.IME_FLAG_NO_EXTRACT_UI);
            descriptionText2.setText(getString(R.string.EmailPasswordConfirmText2));
            descriptionText2.setVisibility(View.VISIBLE);
            descriptionText.setText(getString(R.string.your_resend_email_skip));
            descriptionText.setVisibility(View.VISIBLE);
            descriptionText.setTextColor(Theme.getColor(Theme.key_white));
            buttonTextView.setVisibility(View.INVISIBLE);
            ToolbarItems toolbarItems = toolbar.createToolbarItems();
            doneButton = toolbarItems.addItem(done_button, R.string.icon_check_ok, Color.WHITE);
            doneButton.setContentDescription(context.getString(R.string.Done));
        }
        return fragmentView;
    }

    @Override
    public void onResume() {
        super.onResume();
        passwordEditText.requestFocus();
        showKeyboard(passwordEditText);
    }

    private Pattern patternEmail() {
        return Pattern.compile("[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{2,256}" + "\\@" + "[a-zA-Z0-9][a-zA-Z0-9\\-]{1,64}" + "(" + "\\." + "[a-zA-Z0-9][a-zA-Z0-9\\-]{1,25}" + ")+");
    }

    private void showAlertWithText(String title, String text) {
        if (getActivity() == null) {
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setPositiveButton(getString(R.string.OK), null);
        builder.setTitle(title);
        builder.setMessage(text);
        showDialog(builder.create());
    }

    private void recoveryEmail(String token) {
        new RequestUserTwoStepVerificationVerifyRecoveryEmail().recoveryEmail(token, new RecoveryEmailCallback() {
            @Override
            public void confirmEmail() {
                G.handler.post(() -> {
                    finish();
                    new HelperFragment(getActivity().getSupportFragmentManager(), new TwoStepVerificationSettingFragment(password)).setReplace(true).load();
                });
            }

            @Override
            public void errorEmail(int major, int minor) { }
        });
    }

    private void recoveryPasswordByToken(String token) {
        new RequestUserTwoStepVerificationRecoverPasswordByToken().recoveryPasswordByToken(token, new TwoStepVerificationRecoverPasswordByToken() {
            @Override
            public void recoveryByEmail(String tokenR) {
                new HelperFragment(getActivity().getSupportFragmentManager(), new PrivacyAndSecurityFragment()).setReplace(true).load();
                finish();
            }

            @Override
            public void errorRecoveryByEmail(int major, int minor) {
                new HelperFragment(getActivity().getSupportFragmentManager(), new PrivacyAndSecurityFragment()).setReplace(true).load();
                finish();
            }
        });
    }

    @SuppressLint("ResourceType")
    @Override
    public View createToolBar(Context context) {
        toolbar = new Toolbar(context);
        toolbar.setTitle(context.getString(R.string.your_password));
        toolbar.setBackIcon(R.drawable.ic_ab_back);
        toolbar.setListener(id -> {
            if (id == -1) {
                finish();
            } else if (id == done_button) {
                if ( passwordEditText.getText().length() > 0){
                    if (step == TYPE_EMAIL_CONFIRM) {
                        recoveryEmail(passwordEditText.getText().toString());
                    } else if(step == TYPE_EMAIL_RECOVERY){
                        recoveryPasswordByToken(passwordEditText.getText().toString());
                    }
                }
                else {
                    error(getString(R.string.enter_verify_email_code));
                }
                closeKeyboard(toolbar);
            }
        });
        return toolbar;
    }

    @Override
    public List<ThemeDescriptor> getThemeDescriptor() {
        List<ThemeDescriptor> themeDescriptors = new ArrayList<>();
        themeDescriptors.add(new ThemeDescriptor(buttonTextView, ThemeDescriptor.FLAG_TEXTCOLOR, Theme.key_default_text));
        themeDescriptors.add(new ThemeDescriptor(titleTextView, ThemeDescriptor.FLAG_TEXTCOLOR, Theme.key_default_text));
        themeDescriptors.add(new ThemeDescriptor(descriptionText, ThemeDescriptor.FLAG_TEXTCOLOR, Theme.key_default_text));
        themeDescriptors.add(new ThemeDescriptor(descriptionText2, ThemeDescriptor.FLAG_TEXTCOLOR, Theme.key_default_text));
        themeDescriptors.add(new ThemeDescriptor(descriptionText3, ThemeDescriptor.FLAG_TEXTCOLOR, Theme.key_default_text));
        themeDescriptors.add(new ThemeDescriptor(passwordEditText, ThemeDescriptor.FLAG_TEXTCOLOR, Theme.key_default_text));
        themeDescriptors.add(new ThemeDescriptor(passwordEditText, ThemeDescriptor.FLAG_PROGRESSBAR, Theme.key_default_text));
        themeDescriptors.add(new ThemeDescriptor(passwordEditText, ThemeDescriptor.FLAG_CURSORCOLOR, Theme.key_default_text));
        themeDescriptors.add(new ThemeDescriptor(passwordEditText, ThemeDescriptor.FLAG_HINTTEXTCOLOR, Theme.key_default_text));
        themeDescriptors.add(new ThemeDescriptor(scrollView, ThemeDescriptor.FLAG_LISTGLOWCOLOR, Theme.key_default_text));
        themeDescriptors.add(new ThemeDescriptor(toolbar, ThemeDescriptor.FLAG_BACKGROUND, Theme.key_toolbar_background));
        return themeDescriptors;
    }

    public interface QuestionListener{
        void questionDetailsInfo(String question1,String answer1,String question2,String answer2);
    }
}
