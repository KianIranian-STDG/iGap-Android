package net.iGap.messenger.ui.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;

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
import net.iGap.observers.interfaces.OnUserProfileSetEmailResponse;
import net.iGap.proto.ProtoResponse;
import net.iGap.realm.RealmUserInfo;
import net.iGap.request.RequestUserProfileSetEmail;

import java.util.ArrayList;
import java.util.List;

import static net.iGap.module.AndroidUtils.showKeyboard;

public class EmailFragment extends BaseFragment {
    private final static int done_button = 1;
    private RealmUserInfo realmUserInfo;
    private EditTextBoldCursor emailField;
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
        emailField = new EditTextBoldCursor(context);
        emailField.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
        emailField.setHintTextColor(Theme.getColor(Theme.key_title_text));
        emailField.setTypeface(ResourcesCompat.getFont(context, R.font.main_font));
        emailField.setTextColor(Theme.getColor(Theme.key_default_text));
        emailField.setBackground(Theme.createEditTextDrawable(context, false));
        emailField.setMaxLines(1);
        emailField.setLines(1);
        emailField.setPadding(0, 0, 0, 0);
        emailField.setSingleLine(true);
        emailField.setGravity(G.isAppRtl ? Gravity.RIGHT : Gravity.LEFT);
        emailField.setInputType( InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS | InputType.TYPE_TEXT_VARIATION_WEB_EMAIL_ADDRESS | InputType.TYPE_TEXT_FLAG_AUTO_CORRECT);
        emailField.setImeOptions(EditorInfo.IME_ACTION_DONE);
        emailField.setHint(context.getString(R.string.email));
        emailField.setCursorColor(Theme.getColor(Theme.key_default_text));
        emailField.setCursorSize(LayoutCreator.dp(20));
        emailField.setCursorWidth(1.5f);
        emailField.setOnEditorActionListener((textView, i, keyEvent) -> {
            if (i == EditorInfo.IME_ACTION_DONE && doneButton != null) {
                doneButton.performClick();
                return true;
            }
            return false;
        });
        emailField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                if (ignoreCheck) {
                    return;
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        linearLayout.addView(emailField, LayoutCreator.createLinear(LayoutCreator.MATCH_PARENT, 36, 24, 24, 24, 0));
        checkTextView = new TextView(context);
        checkTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
        checkTextView.setGravity(G.isAppRtl ? Gravity.RIGHT : Gravity.LEFT);
        checkTextView.setTypeface(ResourcesCompat.getFont(context, R.font.main_font));
        linearLayout.addView(checkTextView, LayoutCreator.createLinear(LayoutCreator.WRAP_CONTENT, LayoutCreator.WRAP_CONTENT, G.isAppRtl ? Gravity.RIGHT : Gravity.LEFT, 24, 12, 24, 0));
        helpTextView = new TextView(context);
        helpTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
        helpTextView.setTextColor(Theme.getColor(Theme.key_icon));
        helpTextView.setGravity(G.isAppRtl ? Gravity.RIGHT : Gravity.LEFT);
        helpTextView.setText(context.getString(R.string.set_Email));
        helpTextView.setLinkTextColor(Theme.getColor(Theme.key_link_text));
        helpTextView.setHighlightColor(Theme.getColor(Theme.key_link_text));
        helpTextView.setTypeface(ResourcesCompat.getFont(context, R.font.main_font));
        linearLayout.addView(helpTextView, LayoutCreator.createLinear(LayoutCreator.WRAP_CONTENT, LayoutCreator.WRAP_CONTENT, G.isAppRtl ? Gravity.RIGHT : Gravity.LEFT, 24, 10, 24, 0));
        checkTextView.setVisibility(View.GONE);
        if (realmUserInfo != null && realmUserInfo.getEmail() != null && realmUserInfo.getEmail().length() > 0) {
            ignoreCheck = true;
            emailField.setText(realmUserInfo.getEmail());
            emailField.setSelection(emailField.length());
            ignoreCheck = false;
        }
        fragmentView.setBackgroundColor(Theme.getColor(Theme.key_window_background));
        return fragmentView;
    }

    @Override
    public void onResume() {
        super.onResume();
        emailField.requestFocus();
        showKeyboard(emailField);
    }

    private void saveEmail() {
        String newEmail = emailField.getText().toString();
        if (getActivity() == null || realmUserInfo == null) {
            return;
        }
        hideKeyboard();
        String currentEmail = realmUserInfo.getEmail();
        if (currentEmail == null) {
            currentEmail = "";
        }
        if (currentEmail.equals(newEmail)) {
            finish();
            return;
        }
        sendRequestSetEmail(newEmail);
        progressDialog.show();
    }

    @Override
    public List<ThemeDescriptor> getThemeDescriptor() {
        List<ThemeDescriptor> themeDescriptors = new ArrayList<>();
        themeDescriptors.add(new ThemeDescriptor(checkTextView, ThemeDescriptor.FLAG_TEXTCOLOR, Theme.key_default_text));
        themeDescriptors.add(new ThemeDescriptor(helpTextView, ThemeDescriptor.FLAG_TEXTCOLOR, Theme.key_default_text));
        themeDescriptors.add(new ThemeDescriptor(emailField, ThemeDescriptor.FLAG_TEXTCOLOR, Theme.key_default_text));
        themeDescriptors.add(new ThemeDescriptor(emailField, ThemeDescriptor.FLAG_PROGRESSBAR, Theme.key_default_text));
        themeDescriptors.add(new ThemeDescriptor(emailField, ThemeDescriptor.FLAG_CURSORCOLOR, Theme.key_default_text));
        themeDescriptors.add(new ThemeDescriptor(emailField, ThemeDescriptor.FLAG_HINTTEXTCOLOR, Theme.key_default_text));
        themeDescriptors.add(new ThemeDescriptor(toolbar, ThemeDescriptor.FLAG_BACKGROUND, Theme.key_toolbar_background));
        return themeDescriptors;
    }


    @SuppressLint("ResourceType")
    @Override
    public View createToolBar(Context context) {
        toolbar = new Toolbar(context);
        toolbar.setTitle(context.getString(R.string.email));
        toolbar.setBackIcon(R.drawable.ic_ab_back);
        ToolbarItems toolbarItems = toolbar.createToolbarItems();
        doneButton = toolbarItems.addItem(done_button, R.string.icon_check_ok, Color.WHITE);
        doneButton.setContentDescription(context.getString(R.string.Done));
        toolbar.setListener(id -> {
            if (id == -1) {
                finish();
            } else if (id == done_button) {
                saveEmail();
            }
        });
        return toolbar;
    }

    private void sendRequestSetEmail(String email) {
        new RequestUserProfileSetEmail().setUserProfileEmail(email, new OnUserProfileSetEmailResponse() {
            @Override
            public void onUserProfileEmailResponse(final String emailText, ProtoResponse.Response response) {
                G.runOnUiThread(() -> {
                    progressDialog.dismiss();
                    ProfileFragment.email.postValue(emailText);
                    finish();
                });
            }

            @Override
            public void Error(int majorCode, int minorCode) {
                G.runOnUiThread(() -> {
                    progressDialog.dismiss();
                    emailField.setText(realmUserInfo.getEmail());
                    emailField.requestFocus();
                    showKeyboard(emailField);
                    AlertsCreator.showSimpleAlert(EmailFragment.this, context.getString(R.string.invalid_email));
                });
            }

            @Override
            public void onTimeOut() {
                G.runOnUiThread(() -> {
                    progressDialog.dismiss();
                    emailField.setText(realmUserInfo.getEmail());
                    emailField.requestFocus();
                    showKeyboard(emailField);
                    AlertsCreator.showSimpleAlert(EmailFragment.this, context.getString(R.string.invalid_email));
                });
            }
        });
    }
}