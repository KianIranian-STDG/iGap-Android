package net.iGap.messenger.ui.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import net.iGap.G;
import net.iGap.R;
import net.iGap.fragments.BaseFragment;
import net.iGap.helper.LayoutCreator;
import net.iGap.messenger.dialog.AlertDialog;
import net.iGap.messenger.theme.Theme;
import net.iGap.messenger.theme.ThemeDescriptor;
import net.iGap.messenger.ui.components.AlertsCreator;
import net.iGap.messenger.ui.components.EditTextBoldCursor;
import net.iGap.messenger.ui.toolBar.Toolbar;
import net.iGap.messenger.ui.toolBar.ToolbarItems;
import net.iGap.module.accountManager.DbManager;
import net.iGap.observers.interfaces.OnUserProfileUpdateUsername;
import net.iGap.realm.RealmUserInfo;
import net.iGap.request.RequestUserProfileUpdateUsername;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static net.iGap.module.AndroidUtils.showKeyboard;

public class UserNameFragment extends BaseFragment {
    private final static int done_button = 1;
    private RealmUserInfo realmUserInfo;
    private EditTextBoldCursor userNameField;
    private View doneButton;
    private TextView checkTextView;
    private TextView helpTextView;
    private boolean ignoreCheck;
    private AlertDialog progressDialog;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        progressDialog = new AlertDialog(context, 3);
        realmUserInfo = DbManager.getInstance().doRealmTask(realm -> {
            return realm.where(RealmUserInfo.class).findFirst();
        });
    }

    @Override
    public View createView(Context context) {
        fragmentView = new LinearLayout(context);
        LinearLayout linearLayout = (LinearLayout) fragmentView;
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        fragmentView.setOnTouchListener((v, event) -> true);
        userNameField = new EditTextBoldCursor(context);
        userNameField.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
        userNameField.setHintTextColor(Theme.getColor(Theme.key_title_text));
        userNameField.setTypeface(ResourcesCompat.getFont(context, R.font.main_font));
        userNameField.setTextColor(Theme.getColor(Theme.key_default_text));
        userNameField.setBackground(Theme.createEditTextDrawable(context, false));
        userNameField.setMaxLines(1);
        userNameField.setLines(1);
        userNameField.setPadding(0, 0, 0, 0);
        userNameField.setSingleLine(true);
        userNameField.setGravity(G.isAppRtl ? Gravity.RIGHT : Gravity.LEFT);
        userNameField.setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES | InputType.TYPE_TEXT_FLAG_MULTI_LINE | InputType.TYPE_TEXT_FLAG_AUTO_CORRECT);
        userNameField.setImeOptions(EditorInfo.IME_ACTION_DONE);
        userNameField.setHint(context.getString(R.string.UsernamePlaceholder));
        userNameField.setCursorColor(Theme.getColor(Theme.key_default_text));
        userNameField.setCursorSize(LayoutCreator.dp(20));
        userNameField.setCursorWidth(1.5f);
        userNameField.setOnEditorActionListener((textView, i, keyEvent) -> {
            if (i == EditorInfo.IME_ACTION_DONE && doneButton != null) {
                doneButton.performClick();
                return true;
            }
            return false;
        });
        userNameField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                if (ignoreCheck) {
                    return;
                }
                checkUserName(userNameField.getText().toString(), false);
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        linearLayout.addView(userNameField, LayoutCreator.createLinear(LayoutCreator.MATCH_PARENT, 36, 24, 24, 24, 0));
        checkTextView = new TextView(context);
        checkTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
        checkTextView.setGravity(G.isAppRtl ? Gravity.RIGHT : Gravity.LEFT);
        checkTextView.setTypeface(ResourcesCompat.getFont(context, R.font.main_font));
        linearLayout.addView(checkTextView, LayoutCreator.createLinear(LayoutCreator.WRAP_CONTENT, LayoutCreator.WRAP_CONTENT, G.isAppRtl ? Gravity.RIGHT : Gravity.LEFT, 24, 12, 24, 0));
        helpTextView = new TextView(context);
        helpTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
        helpTextView.setTextColor(Theme.getColor(Theme.key_icon));
        helpTextView.setGravity(G.isAppRtl ? Gravity.RIGHT : Gravity.LEFT);
        helpTextView.setText(context.getString(R.string.UsernameHelp));
        helpTextView.setLinkTextColor(Theme.getColor(Theme.key_link_text));
        helpTextView.setHighlightColor(Theme.getColor(Theme.key_link_text));
        helpTextView.setTypeface(ResourcesCompat.getFont(context, R.font.main_font));
        linearLayout.addView(helpTextView, LayoutCreator.createLinear(LayoutCreator.WRAP_CONTENT, LayoutCreator.WRAP_CONTENT, G.isAppRtl ? Gravity.RIGHT : Gravity.LEFT, 24, 10, 24, 0));
        checkTextView.setVisibility(View.GONE);
        if (realmUserInfo != null && realmUserInfo.getUserInfo().getUsername() != null && realmUserInfo.getUserInfo().getUsername().length() > 0) {
            ignoreCheck = true;
            userNameField.setText(realmUserInfo.getUserInfo().getUsername());
            userNameField.setSelection(userNameField.length());
            ignoreCheck = false;
        }
        fragmentView.setBackgroundColor(Theme.getColor(Theme.key_window_background));
        return fragmentView;
    }

    @Override
    public void onResume() {
        super.onResume();
        userNameField.requestFocus();
        showKeyboard(userNameField);
    }

    private boolean checkUserName(String name, boolean alert) {
        if (name != null && name.startsWith("@")) {
            name = name.substring(1);
        }
        if (!TextUtils.isEmpty(name)) {
            checkTextView.setVisibility(View.VISIBLE);
        } else {
            checkTextView.setVisibility(View.GONE);
        }
        if (alert && name.length() == 0) {
            return true;
        }
        if (name != null) {
            if (name.startsWith("_") || name.endsWith("_")) {
                checkTextView.setText(context.getString(R.string.UsernameInvalid));
                checkTextView.setTag(Theme.key_red);
                checkTextView.setTextColor(Theme.getColor(Theme.key_red));
                return false;
            }
            for (int a = 0; a < name.length(); a++) {
                char ch = name.charAt(a);
                if (a == 0 && ch >= '0' && ch <= '9') {
                    if (alert) {
                        AlertsCreator.showSimpleAlert(this, context.getString(R.string.UsernameInvalidStartNumber));
                    } else {
                        checkTextView.setText(context.getString(R.string.UsernameInvalidStartNumber));
                        checkTextView.setTag(Theme.key_red);
                        checkTextView.setTextColor(Theme.getColor(Theme.key_red));
                    }
                    return false;
                }
                if (!(ch >= '0' && ch <= '9' || ch >= 'a' && ch <= 'z' || ch >= 'A' && ch <= 'Z' || ch == '_')) {
                    if (alert) {
                        AlertsCreator.showSimpleAlert(this, context.getString(R.string.UsernameInvalid));
                    } else {
                        checkTextView.setText(context.getString(R.string.UsernameInvalid));
                        checkTextView.setTag(Theme.key_red);
                        checkTextView.setTextColor(Theme.getColor(Theme.key_red));
                    }
                    return false;
                }
            }
        }
        if (name == null || name.length() < 5) {
            if (alert) {
                AlertsCreator.showSimpleAlert(this, context.getString(R.string.UsernameInvalidShort));
            } else {
                checkTextView.setText(context.getString(R.string.UsernameInvalidShort));
                checkTextView.setTag(Theme.key_red);
                checkTextView.setTextColor(Theme.getColor(Theme.key_red));
            }
            return false;
        }
        if (name.length() > 32) {
            if (alert) {
                AlertsCreator.showSimpleAlert(this, context.getString(R.string.UsernameInvalidLong));
            } else {
                checkTextView.setText(context.getString(R.string.UsernameInvalidLong));
                checkTextView.setTag(Theme.key_red);
                checkTextView.setTextColor(Theme.getColor(Theme.key_red));
            }
            return false;
        }
        if (!alert) {
            String currentName = realmUserInfo.getUserInfo().getUsername();
            if (currentName == null) {
                currentName = "";
            }
            if (name.equals(currentName)) {
                checkTextView.setText(String.format(context.getString(R.string.UsernameAvailable), name));
                checkTextView.setTag(Theme.key_title_text);
                checkTextView.setTextColor(Theme.getColor(Theme.key_title_text));
                return true;
            }
            checkTextView.setText(context.getString(R.string.UsernameChecking));
            checkTextView.setTag(Theme.key_icon);
            checkTextView.setTextColor(Theme.getColor(Theme.key_icon));
        }
        return true;
    }

    private void saveUserName() {
        String newUserName = userNameField.getText().toString();
        if (newUserName.startsWith("@")) {
            newUserName = newUserName.substring(1);
        }
        if (!checkUserName(newUserName, true)) {
            return;
        }
        if (getActivity() == null || realmUserInfo == null) {
            return;
        }
        hideKeyboard();
        String currentUserName = realmUserInfo.getUserInfo().getUsername();
        if (currentUserName == null) {
            currentUserName = "";
        }
        if (currentUserName.equals(newUserName)) {
            finish();
            return;
        }
        sendRequestSetUsername(newUserName);
        progressDialog.show();
    }

    @Override
    public List<ThemeDescriptor> getThemeDescriptor() {
        List<ThemeDescriptor> themeDescriptors = new ArrayList<>();
        themeDescriptors.add(new ThemeDescriptor(checkTextView, ThemeDescriptor.FLAG_TEXTCOLOR, Theme.key_default_text));
        themeDescriptors.add(new ThemeDescriptor(helpTextView, ThemeDescriptor.FLAG_TEXTCOLOR, Theme.key_default_text));
        themeDescriptors.add(new ThemeDescriptor(userNameField, ThemeDescriptor.FLAG_TEXTCOLOR, Theme.key_default_text));
        themeDescriptors.add(new ThemeDescriptor(userNameField, ThemeDescriptor.FLAG_PROGRESSBAR, Theme.key_default_text));
        themeDescriptors.add(new ThemeDescriptor(userNameField, ThemeDescriptor.FLAG_CURSORCOLOR, Theme.key_default_text));
        themeDescriptors.add(new ThemeDescriptor(userNameField, ThemeDescriptor.FLAG_HINTTEXTCOLOR, Theme.key_default_text));
        themeDescriptors.add(new ThemeDescriptor(toolbar, ThemeDescriptor.FLAG_BACKGROUND, Theme.key_toolbar_background));
        return themeDescriptors;
    }


    @SuppressLint("ResourceType")
    @Override
    public View createToolBar(Context context) {
        toolbar = new Toolbar(context);
        toolbar.setTitle(context.getString(R.string.Username));
        toolbar.setBackIcon(R.drawable.ic_ab_back);
        ToolbarItems toolbarItems = toolbar.createToolbarItems();
        doneButton = toolbarItems.addItem(done_button, R.string.icon_check_ok, Color.WHITE);
        doneButton.setContentDescription(context.getString(R.string.Done));
        toolbar.setListener(id -> {
            if (id == -1) {
                finish();
            } else if (id == done_button) {
                saveUserName();
            }
        });
        return toolbar;
    }

    private void sendRequestSetUsername(String username) {
        new RequestUserProfileUpdateUsername().userProfileUpdateUsername(username, new OnUserProfileUpdateUsername() {
            @Override
            public void onUserProfileUpdateUsername(final String username) {
                G.handler.post(() -> {
                    progressDialog.dismiss();
                    finish();
                });
            }

            @Override
            public void Error(final int majorCode, int minorCode, final int time) {
                G.handler.post(() -> {
                    progressDialog.dismiss();
                    userNameField.setText(realmUserInfo.getUserInfo().getUsername());
                    userNameField.requestFocus();
                    showKeyboard(userNameField);
                    if (majorCode == 175) {
                        dialogWaitTime(R.string.USER_PROFILE_UPDATE_USERNAME_UPDATE_LOCK, time);
                    } else if (majorCode == 164) {
                        AlertsCreator.showSimpleAlert(UserNameFragment.this, context.getString(R.string.UsernameInUse));
                    }
                });
            }

            @Override
            public void timeOut() { }
        });
    }

    private void dialogWaitTime(int title, long time) {
        boolean wrapInScrollView = true;
        final MaterialDialog dialog = new MaterialDialog.Builder(G.fragmentActivity)
                .title(title).backgroundColor(Theme.getColor(Theme.key_popup_background))
                .customView(R.layout.dialog_remind_time, wrapInScrollView)
                .negativeColor(Theme.getColor(Theme.key_button_background))
                .positiveColor(Theme.getColor(Theme.key_button_background))
                .positiveText(R.string.B_ok).autoDismiss(false)
                .canceledOnTouchOutside(false).onPositive(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                dialog.dismiss();
            }
        }).show();
        View v = dialog.getCustomView();
        v.setBackgroundColor(Theme.getColor(Theme.key_window_background));
        final TextView remindTime = dialog.getCustomView().findViewById(R.id.remindTime);
        remindTime.setTextColor(Theme.getColor(Theme.key_title_text));
        final TextView textReason = v.findViewById(R.id.textReason);
        textReason.setTextColor(Theme.getColor(Theme.key_title_text));
        final TextView textRemindTime = v.findViewById(R.id.textRemindTime);
        textRemindTime.setTextColor(Theme.getColor(Theme.key_title_text));
        CountDownTimer countWaitTimer = new CountDownTimer(time * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                int seconds = (int) ((millisUntilFinished) / 1000);
                int minutes = seconds / 60;
                seconds = seconds % 60;
                remindTime.setText(String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds));
            }

            @Override
            public void onFinish() {
            }
        };
        countWaitTimer.start();
    }
}
