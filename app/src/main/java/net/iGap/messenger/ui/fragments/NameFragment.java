package net.iGap.messenger.ui.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
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
import android.widget.Toast;

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
import net.iGap.observers.interfaces.OnUserProfileSetNickNameResponse;
import net.iGap.realm.RealmUserInfo;
import net.iGap.request.RequestUserProfileSetNickname;

import java.util.ArrayList;
import java.util.List;

import static net.iGap.module.AndroidUtils.showKeyboard;

public class NameFragment extends BaseFragment {
    private final static int done_button = 1;
    private RealmUserInfo realmUserInfo;
    private EditTextBoldCursor nameField;
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
        nameField = new EditTextBoldCursor(context);
        nameField.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
        nameField.setHintTextColor(Theme.getColor(Theme.key_title_text));
        nameField.setTypeface(ResourcesCompat.getFont(context, R.font.main_font));
        nameField.setTextColor(Theme.getColor(Theme.key_default_text));
        nameField.setBackground(Theme.createEditTextDrawable(context, false));
        nameField.setMaxLines(1);
        nameField.setLines(1);
        nameField.setPadding(0, 0, 0, 0);
        nameField.setSingleLine(true);
        nameField.setGravity(G.isAppRtl ? Gravity.RIGHT : Gravity.LEFT);
        nameField.setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES | InputType.TYPE_TEXT_FLAG_MULTI_LINE | InputType.TYPE_TEXT_FLAG_AUTO_CORRECT);
        nameField.setImeOptions(EditorInfo.IME_ACTION_DONE);
        nameField.setHint(context.getString(R.string.NamePlaceholder));
        nameField.setCursorColor(Theme.getColor(Theme.key_default_text));
        nameField.setCursorSize(LayoutCreator.dp(20));
        nameField.setCursorWidth(1.5f);
        nameField.setOnEditorActionListener((textView, i, keyEvent) -> {
            if (i == EditorInfo.IME_ACTION_DONE && doneButton != null) {
                doneButton.performClick();
                return true;
            }
            return false;
        });
        nameField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                if (ignoreCheck) {
                    return;
                }
                checkName(nameField.getText().toString(), false);
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        linearLayout.addView(nameField, LayoutCreator.createLinear(LayoutCreator.MATCH_PARENT, 36, 24, 24, 24, 0));
        checkTextView = new TextView(context);
        checkTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
        checkTextView.setGravity(G.isAppRtl ? Gravity.RIGHT : Gravity.LEFT);
        checkTextView.setTypeface(ResourcesCompat.getFont(context, R.font.main_font));
        linearLayout.addView(checkTextView, LayoutCreator.createLinear(LayoutCreator.WRAP_CONTENT, LayoutCreator.WRAP_CONTENT, G.isAppRtl ? Gravity.RIGHT : Gravity.LEFT, 24, 12, 24, 0));
        helpTextView = new TextView(context);
        helpTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
        helpTextView.setTextColor(Theme.getColor(Theme.key_icon));
        helpTextView.setGravity(G.isAppRtl ? Gravity.RIGHT : Gravity.LEFT);
        helpTextView.setText("");
        helpTextView.setLinkTextColor(Theme.getColor(Theme.key_link_text));
        helpTextView.setHighlightColor(Theme.getColor(Theme.key_link_text));
        helpTextView.setTypeface(ResourcesCompat.getFont(context, R.font.main_font));
        linearLayout.addView(helpTextView, LayoutCreator.createLinear(LayoutCreator.WRAP_CONTENT, LayoutCreator.WRAP_CONTENT, G.isAppRtl ? Gravity.RIGHT : Gravity.LEFT, 24, 10, 24, 0));
        checkTextView.setVisibility(View.GONE);
        if (realmUserInfo != null && realmUserInfo.getUserInfo().getDisplayName() != null && realmUserInfo.getUserInfo().getDisplayName().length() > 0) {
            ignoreCheck = true;
            nameField.setText(realmUserInfo.getUserInfo().getDisplayName());
            nameField.setSelection(nameField.length());
            ignoreCheck = false;
        }
        fragmentView.setBackgroundColor(Theme.getColor(Theme.key_window_background));
        return fragmentView;
    }

    @Override
    public void onResume() {
        super.onResume();
        nameField.requestFocus();
        showKeyboard(nameField);
    }

    private boolean checkName(String name, boolean alert) {
        if (!TextUtils.isEmpty(name)) {
            checkTextView.setVisibility(View.VISIBLE);
        } else {
            checkTextView.setVisibility(View.GONE);
        }
        if (alert && name.length() == 0) {
            return true;
        }
        if (name.length() > 32) {
            if (alert) {
                AlertsCreator.showSimpleAlert(this, context.getString(R.string.NameInvalidLong));
            } else {
                checkTextView.setText(context.getString(R.string.NameInvalidLong));
                checkTextView.setTag(Theme.key_red);
                checkTextView.setTextColor(Theme.getColor(Theme.key_red));
            }
            return false;
        }
        if (!alert) {
            checkTextView.setText(context.getString(R.string.NameChecking));
            checkTextView.setTag(Theme.key_icon);
            checkTextView.setTextColor(Theme.getColor(Theme.key_icon));
        }
        return true;
    }

    private void saveName() {
        String newName = nameField.getText().toString();
        if (!checkName(newName, true)) {
            return;
        }
        if (getActivity() == null || realmUserInfo == null) {
            return;
        }
        hideKeyboard();
        String currentName = realmUserInfo.getUserInfo().getDisplayName();
        if (currentName == null) {
            currentName = "";
        }
        if (currentName.equals(newName)) {
            finish();
            return;
        }
        sendRequestUserProfileSetNickname(newName);
        progressDialog.show();
    }

    @Override
    public List<ThemeDescriptor> getThemeDescriptor() {
        List<ThemeDescriptor> themeDescriptors = new ArrayList<>();
        themeDescriptors.add(new ThemeDescriptor(checkTextView, ThemeDescriptor.FLAG_TEXTCOLOR, Theme.key_default_text));
        themeDescriptors.add(new ThemeDescriptor(helpTextView, ThemeDescriptor.FLAG_TEXTCOLOR, Theme.key_default_text));
        themeDescriptors.add(new ThemeDescriptor(nameField, ThemeDescriptor.FLAG_TEXTCOLOR, Theme.key_default_text));
        themeDescriptors.add(new ThemeDescriptor(nameField, ThemeDescriptor.FLAG_PROGRESSBAR, Theme.key_default_text));
        themeDescriptors.add(new ThemeDescriptor(nameField, ThemeDescriptor.FLAG_CURSORCOLOR, Theme.key_default_text));
        themeDescriptors.add(new ThemeDescriptor(nameField, ThemeDescriptor.FLAG_HINTTEXTCOLOR, Theme.key_default_text));
        themeDescriptors.add(new ThemeDescriptor(toolbar, ThemeDescriptor.FLAG_BACKGROUND, Theme.key_toolbar_background));
        return themeDescriptors;
    }


    @SuppressLint("ResourceType")
    @Override
    public View createToolBar(Context context) {
        toolbar = new Toolbar(context);
        toolbar.setTitle(context.getString(R.string.name));
        toolbar.setBackIcon(R.drawable.ic_ab_back);
        ToolbarItems toolbarItems = toolbar.createToolbarItems();
        doneButton = toolbarItems.addItem(done_button, R.string.icon_check_ok, Color.WHITE);
        doneButton.setContentDescription(context.getString(R.string.Done));
        toolbar.setListener(id -> {
            if (id == -1) {
                finish();
            } else if (id == done_button) {
                saveName();
            }
        });
        return toolbar;
    }

    private void sendRequestUserProfileSetNickname(String name) {
        new RequestUserProfileSetNickname().userProfileNickName(name, new OnUserProfileSetNickNameResponse() {
            @Override
            public void onUserProfileNickNameResponse(final String nickName, String initials) {
                G.runOnUiThread(() -> {
                    progressDialog.dismiss();
                    finish();
                });
            }

            @Override
            public void onUserProfileNickNameError(int majorCode, int minorCode) {
                G.runOnUiThread(() -> {
                    progressDialog.dismiss();
                    nameField.setText(realmUserInfo.getUserInfo().getDisplayName());
                    nameField.requestFocus();
                    showKeyboard(nameField);
                });
            }

            @Override
            public void onUserProfileNickNameTimeOut() {
                G.runOnUiThread(() -> {
                    progressDialog.dismiss();
                    nameField.setText(realmUserInfo.getUserInfo().getDisplayName());
                    nameField.requestFocus();
                    showKeyboard(nameField);
                });
            }
        });

    }
}
