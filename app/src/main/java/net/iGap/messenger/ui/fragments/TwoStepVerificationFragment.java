package net.iGap.messenger.ui.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Build;
import android.os.CountDownTimer;
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

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import net.iGap.G;
import net.iGap.R;
import net.iGap.fragments.BaseFragment;
import net.iGap.helper.HelperFragment;
import net.iGap.helper.LayoutCreator;
import net.iGap.messenger.theme.Theme;
import net.iGap.messenger.theme.ThemeDescriptor;
import net.iGap.messenger.ui.components.EditTextBoldCursor;
import net.iGap.messenger.ui.toolBar.Toolbar;
import net.iGap.messenger.ui.toolBar.ToolbarItems;
import net.iGap.observers.interfaces.CheckPasswordCallback;
import net.iGap.observers.interfaces.TwoStepVerificationGetPasswordDetail;
import net.iGap.observers.interfaces.TwoStepVerificationRecoverPasswordByAnswersCallback;
import net.iGap.request.RequestUserTwoStepVerificationCheckPassword;
import net.iGap.request.RequestUserTwoStepVerificationGetPasswordDetail;
import net.iGap.request.RequestUserTwoStepVerificationRecoverPasswordByAnswers;

import java.util.ArrayList;
import java.util.List;

import static net.iGap.messenger.ui.fragments.TwoStepVerificationStepsFragment.TYPE_EMAIL_RECOVERY;
import static net.iGap.messenger.ui.fragments.TwoStepVerificationStepsFragment.TYPE_ENTER_FIRST;
import static net.iGap.module.AndroidUtils.showKeyboard;
import static net.iGap.module.AppUtils.error;

public class TwoStepVerificationFragment extends BaseFragment {

    private final static int done_button = 1;
    private View doneButton;
    private TextView buttonTextView;
    private TextView titleTextView;
    private TextView descriptionText;
    private EditTextBoldCursor passwordEditText;
    private ScrollView scrollView;
    private String password;
    private String twoStepVerification;
    private boolean hasConfirmedRecoveryEmail;
    private String questionOne;
    private String questionTwo;

    public TwoStepVerificationFragment(String twoStepVerification) {
        this.twoStepVerification = twoStepVerification;
    }

    @Override
    public View createView(Context context) {
        getPasswordDetail();

        titleTextView = new TextView(context);
        titleTextView.setTextColor(Theme.getColor(Theme.key_default_text));
        titleTextView.setGravity(Gravity.CENTER_HORIZONTAL);
        titleTextView.setPadding(LayoutCreator.dp(32), 0, LayoutCreator.dp(32), 0);
        titleTextView.setTypeface(ResourcesCompat.getFont(context, R.font.main_font_bold));
        titleTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);

        descriptionText = new TextView(context);
        descriptionText.setTextColor(Theme.getColor(Theme.key_subtitle_text));
        descriptionText.setGravity(Gravity.CENTER_HORIZONTAL);
        descriptionText.setLineSpacing(LayoutCreator.dp(2), 1);
        descriptionText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
        descriptionText.setTypeface(ResourcesCompat.getFont(context, R.font.main_font));
        descriptionText.setPadding(LayoutCreator.dp(32), 0, LayoutCreator.dp(32), 0);

        buttonTextView = new TextView(context);
        buttonTextView.setMinWidth(LayoutCreator.dp(220));
        buttonTextView.setPadding(LayoutCreator.dp(34), 0, LayoutCreator.dp(34), 0);
        buttonTextView.setGravity(Gravity.CENTER);
        buttonTextView.setTextColor(Theme.getColor(Theme.key_button_text));
        buttonTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
        buttonTextView.setTypeface(ResourcesCompat.getFont(context, R.font.main_font));
        buttonTextView.setText(getString(R.string.set_password));
        buttonTextView.setBackground(Theme.createSimpleSelectorRoundRectDrawable(LayoutCreator.dp(4), Theme.getColor(Theme.key_button_background), Theme.getColor(Theme.key_button_background)));
        buttonTextView.setOnClickListener(v -> {
            finish();
            new HelperFragment(getActivity().getSupportFragmentManager(), new TwoStepVerificationStepsFragment(TYPE_ENTER_FIRST)).setReplace(true).load();
        });

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

        if (twoStepVerification.equals(getString(R.string.Enable))) {
            ToolbarItems toolbarItems = toolbar.createToolbarItems();
            doneButton = toolbarItems.addItem(done_button, R.string.icon_check_ok, Color.WHITE);
            doneButton.setContentDescription(context.getString(R.string.Done));
            descriptionText.setText(getString(R.string.forgot_password));
            descriptionText.setTextColor(Theme.getColor(Theme.key_subtitle_text));
            descriptionText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    chooseRecoveryMode();
                }
            });
            titleTextView.setText(getString(R.string.enter_a_password));
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
            FrameLayout frameLayout = new FrameLayout(context);
            scrollViewLinearLayout.addView(frameLayout, LayoutCreator.createLinear(220, 36, Gravity.TOP | Gravity.CENTER_HORIZONTAL, 40, 32, 40, 0));
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

                public void onDestroyActionMode(ActionMode mode) { }

                public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                    return false;
                }

                public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                    return false;
                }
            });
            FrameLayout frameLayout2 = new FrameLayout(context);
            scrollViewLinearLayout.addView(frameLayout2, LayoutCreator.createLinear(LayoutCreator.MATCH_PARENT, LayoutCreator.WRAP_CONTENT, Gravity.LEFT | Gravity.TOP, 0, 36, 0, 22));
            frameLayout2.addView(descriptionText, LayoutCreator.createFrame(LayoutCreator.WRAP_CONTENT, LayoutCreator.WRAP_CONTENT, Gravity.CENTER_HORIZONTAL | Gravity.TOP));
            fragmentView = container;

            fragmentView.setBackgroundColor(Theme.getColor(Theme.key_window_background));

            if (passwordEditText != null) {
                passwordEditText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                    }
                });
            }
        } else {
            titleTextView.setText(getString(R.string.two_step_verification));
            descriptionText.setText(getString(R.string.two_step_verification_description));
            ViewGroup container = new ViewGroup(context) {
                @Override
                protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                    int width = MeasureSpec.getSize(widthMeasureSpec);
                    int height = MeasureSpec.getSize(heightMeasureSpec);
                    if (width > height) {
                        titleTextView.measure(MeasureSpec.makeMeasureSpec((int) (width * 0.6f), MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(height, MeasureSpec.UNSPECIFIED));
                        descriptionText.measure(MeasureSpec.makeMeasureSpec((int) (width * 0.6f), MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(height, MeasureSpec.UNSPECIFIED));
                        buttonTextView.measure(MeasureSpec.makeMeasureSpec((int) (width * 0.6f), MeasureSpec.AT_MOST), MeasureSpec.makeMeasureSpec(LayoutCreator.dp(42), MeasureSpec.EXACTLY));
                    } else {
                        titleTextView.measure(MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(height, MeasureSpec.UNSPECIFIED));
                        descriptionText.measure(MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(height, MeasureSpec.UNSPECIFIED));
                        buttonTextView.measure(MeasureSpec.makeMeasureSpec(width, MeasureSpec.AT_MOST), MeasureSpec.makeMeasureSpec(LayoutCreator.dp(42), MeasureSpec.EXACTLY));
                    }
                    setMeasuredDimension(width, height);
                }

                @Override
                protected void onLayout(boolean changed, int l, int t, int r, int b) {
                    int width = r - l;
                    int height = b - t;

                    if (r > b) {
                        int y;
                        int x = (int) (width * 0.4f);
                        y = (int) (height * 0.22f);
                        titleTextView.layout(x, y, x + titleTextView.getMeasuredWidth(), y + titleTextView.getMeasuredHeight());
                        x = (int) (width * 0.4f);
                        y = (int) (height * 0.39f);
                        descriptionText.layout(x, y, x + descriptionText.getMeasuredWidth(), y + descriptionText.getMeasuredHeight());
                        x = (int) (width * 0.4f + (width * 0.6f - buttonTextView.getMeasuredWidth()) / 2);
                        y = (int) (height * 0.64f);
                        buttonTextView.layout(x, y, x + buttonTextView.getMeasuredWidth(), y + buttonTextView.getMeasuredHeight());
                    } else {
                        int y = (int) (height * 0.148f);
                        y = (int) (height * 0.22f);
                        titleTextView.layout(0, y, titleTextView.getMeasuredWidth(), y + titleTextView.getMeasuredHeight());
                        y += titleTextView.getMeasuredHeight() + LayoutCreator.dp(12);
                        descriptionText.layout(0, y, descriptionText.getMeasuredWidth(), y + descriptionText.getMeasuredHeight());
                        int x = (width - buttonTextView.getMeasuredWidth()) / 2;
                        y = (int) (height * 0.558f);
                        buttonTextView.layout(x, y, x + buttonTextView.getMeasuredWidth(), y + buttonTextView.getMeasuredHeight());
                    }
                }
            };
            container.setOnTouchListener((v, event) -> true);
            container.addView(titleTextView);
            container.addView(descriptionText);
            container.addView(buttonTextView);
            fragmentView = container;
            fragmentView.setBackgroundColor(Theme.getColor(Theme.key_window_background));
        }
        return fragmentView;
    }

    @Override
    public void onResume() {
        super.onResume();
        passwordEditText.requestFocus();
        showKeyboard(passwordEditText);
    }

    private void getPasswordDetail(){
        new RequestUserTwoStepVerificationGetPasswordDetail().getPasswordDetail(new TwoStepVerificationGetPasswordDetail() {

            @Override
            public void getDetailPassword(String q1, String q2, String hint, boolean hasConfirmedEmail, String unconfirmedEmailPattern) {
                questionOne = q1;
                questionTwo = q2;
                hasConfirmedRecoveryEmail = hasConfirmedEmail;
            }

            @Override
            public void errorGetPasswordDetail(int majorCode, int minorCode) { }
        });
    }

    private void chooseRecoveryMode() {
        int item;
        if (hasConfirmedRecoveryEmail) {
            item = R.array.securityRecoveryPassword;
        } else {
            item = R.array.securityRecoveryPasswordWithoutEmail;
        }
        new MaterialDialog.Builder(getActivity())
                .title(R.string.set_recovery_dialog_title)
                .items(item)
                .negativeColor(Theme.getColor(Theme.key_button_background))
                .positiveColor(Theme.getColor(Theme.key_button_background))
                .choiceWidgetColor(ColorStateList.valueOf(Theme.getColor(Theme.key_button_background)))
                .itemsCallback((dialog, view1, which, text) -> forgetPassword(text.equals(getString(R.string.recovery_by_email_dialog))))
                .show();
    }

    public void forgetPassword(boolean isRecoveryByEmail) {
        if (isRecoveryByEmail){
            finish();
            new HelperFragment(getActivity().getSupportFragmentManager(), new TwoStepVerificationStepsFragment(TYPE_EMAIL_RECOVERY)).setReplace(true).load();
        } else {
            finish();
            new HelperFragment(getActivity().getSupportFragmentManager(), new TwoStepVerificationQuestionFragment(questionOne,questionTwo,new TwoStepVerificationStepsFragment.QuestionListener() {
                @Override
                public void questionDetailsInfo(String question1, String answer1, String question2, String answer2) {
                    recoveryPasswordByAnswer(answer1,answer2);
                }
            })).setReplace(true).load();
        }
    }

    private void recoveryPasswordByAnswer(String answer1, String answer2) {
        if (answer1.length() > 0 && answer2.length() > 0) {
            new RequestUserTwoStepVerificationRecoverPasswordByAnswers().RecoveryPasswordByAnswer(answer1, answer2, new TwoStepVerificationRecoverPasswordByAnswersCallback() {
                @Override
                public void recoveryByQuestion(String tokenR) {
                    new HelperFragment(getActivity().getSupportFragmentManager(), new PrivacyAndSecurityFragment()).setReplace(true).load();
                    finish();
                }

                @Override
                public void errorRecoveryByQuestion(int major, int minor) {
                    new HelperFragment(getActivity().getSupportFragmentManager(), new PrivacyAndSecurityFragment()).setReplace(true).load();
                    finish();
                }
            });

        } else {
            error(G.fragmentActivity.getResources().getString(R.string.please_complete_all_item));
        }
    }

    private void checkPassword(String password) {
        new RequestUserTwoStepVerificationCheckPassword().checkPassword(password, new CheckPasswordCallback() {
            @Override
            public void checkPassword() {
                G.handler.post(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                        new HelperFragment(getActivity().getSupportFragmentManager(), new TwoStepVerificationSettingFragment(password)).setReplace(true).load();
                    }
                });
            }

            @Override
            public void errorCheckPassword(int major, int minor, int getWait) {
                if (major == 10106) {
                    G.handler.post(new Runnable() {
                        @Override
                        public void run() {
                            dialogWaitTime(getWait);
                        }
                    });
                } else if (major == 10105 && minor == 101) {
                    //password invalid
                }
            }
        });
    }

    private void dialogWaitTime(long time) {
        boolean wrapInScrollView = true;
        final MaterialDialog dialogWait = new MaterialDialog.Builder(G.fragmentActivity)
                .backgroundColor(Theme.getColor(Theme.key_popup_background))
                .title(R.string.error_check_password)
                .customView(R.layout.dialog_remind_time, wrapInScrollView)
                .negativeColor(Theme.getColor(Theme.key_button_background))
                .positiveColor(Theme.getColor(Theme.key_button_background))
                .positiveText(R.string.B_ok).autoDismiss(true)
                .canceledOnTouchOutside(true).onPositive(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                dialog.dismiss();
            }
        }).show();
        View v = dialogWait.getCustomView();
        v.setBackgroundColor(Theme.getColor(Theme.key_window_background));
        final TextView remindTime = v.findViewById(R.id.remindTime);
        remindTime.setTextColor(Theme.getColor(Theme.key_default_text));
        final TextView textReason = v.findViewById(R.id.textReason);
        textReason.setTextColor(Theme.getColor(Theme.key_default_text));
        final TextView textRemindTime = v.findViewById(R.id.textRemindTime);
        textRemindTime.setTextColor(Theme.getColor(Theme.key_default_text));
        CountDownTimer countWaitTimer = new CountDownTimer(time * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                int seconds = (int) ((millisUntilFinished) / 1000);
                int minutes = seconds / 60;
                seconds = seconds % 60;
                remindTime.setText("" + String.format("%02d", minutes) + ":" + String.format("%02d", seconds));
            }
            @Override
            public void onFinish() {
                remindTime.setText("00:00");
            }
        };
        countWaitTimer.start();
    }

    @SuppressLint("ResourceType")
    @Override
    public View createToolBar(Context context) {
        toolbar = new Toolbar(context);
        toolbar.setTitle(context.getString(R.string.two_step_verification));
        toolbar.setBackIcon(R.drawable.ic_ab_back);
        toolbar.setListener(id -> {
            if (id == -1) {
                finish();
            } else if (id == done_button) {
                password = passwordEditText.getText().toString();
                checkPassword(password);
            }
        });
        return toolbar;
    }

    @Override
    public List<ThemeDescriptor> getThemeDescriptor() {
        List<ThemeDescriptor> themeDescriptors = new ArrayList<>();
        themeDescriptors.add(new ThemeDescriptor(buttonTextView, ThemeDescriptor.FLAG_TEXTCOLOR, Theme.key_default_text));
        themeDescriptors.add(new ThemeDescriptor(descriptionText, ThemeDescriptor.FLAG_TEXTCOLOR, Theme.key_default_text));
        themeDescriptors.add(new ThemeDescriptor(passwordEditText, ThemeDescriptor.FLAG_TEXTCOLOR, Theme.key_default_text));
        themeDescriptors.add(new ThemeDescriptor(passwordEditText, ThemeDescriptor.FLAG_PROGRESSBAR, Theme.key_default_text));
        themeDescriptors.add(new ThemeDescriptor(passwordEditText, ThemeDescriptor.FLAG_CURSORCOLOR, Theme.key_default_text));
        themeDescriptors.add(new ThemeDescriptor(passwordEditText, ThemeDescriptor.FLAG_HINTTEXTCOLOR, Theme.key_default_text));
        themeDescriptors.add(new ThemeDescriptor(scrollView, ThemeDescriptor.FLAG_LISTGLOWCOLOR, Theme.key_default_text));
        themeDescriptors.add(new ThemeDescriptor(toolbar, ThemeDescriptor.FLAG_BACKGROUND, Theme.key_toolbar_background));
        return themeDescriptors;
    }
}
