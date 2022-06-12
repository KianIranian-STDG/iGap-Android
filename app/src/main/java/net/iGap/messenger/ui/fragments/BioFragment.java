package net.iGap.messenger.ui.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Vibrator;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.FrameLayout;
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
import net.iGap.messenger.ui.components.CodepointsLengthInputFilter;
import net.iGap.messenger.ui.components.EditTextBoldCursor;
import net.iGap.messenger.ui.toolBar.NumberTextView;
import net.iGap.messenger.ui.toolBar.Toolbar;
import net.iGap.messenger.ui.toolBar.ToolbarItems;
import net.iGap.module.accountManager.DbManager;
import net.iGap.observers.interfaces.OnUserProfileSetBioResponse;
import net.iGap.observers.interfaces.OnUserProfileUpdateBio;
import net.iGap.realm.RealmUserInfo;
import net.iGap.request.RequestUserProfileSetBio;

import java.util.ArrayList;
import java.util.List;

import static net.iGap.module.AndroidUtils.showKeyboard;

public class BioFragment extends BaseFragment{
    private final static int done_button = 1;
    private RealmUserInfo realmUserInfo;
    private EditTextBoldCursor bioField;
    private View doneButton;
    private NumberTextView checkTextView;
    private TextView helpTextView;
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
        FrameLayout fieldContainer = new FrameLayout(context);
        linearLayout.addView(fieldContainer, LayoutCreator.createLinear(LayoutCreator.MATCH_PARENT, LayoutCreator.WRAP_CONTENT, 24, 24, 20, 0));
        bioField = new EditTextBoldCursor(context);
        bioField.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
        bioField.setHintTextColor(Theme.getColor(Theme.key_title_text));
        bioField.setTypeface(ResourcesCompat.getFont(context, R.font.main_font));
        bioField.setTextColor(Theme.getColor(Theme.key_default_text));
        bioField.setBackground(Theme.createEditTextDrawable(context, false));
        bioField.setMaxLines(4);
        bioField.setPadding(LayoutCreator.dp(G.isAppRtl ? 24 : 0), 0, LayoutCreator.dp(G.isAppRtl ? 0 : 24), LayoutCreator.dp(6));
        bioField.setGravity(G.isAppRtl ? Gravity.RIGHT : Gravity.LEFT);
        bioField.setImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI);
        bioField.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        bioField.setImeOptions(EditorInfo.IME_ACTION_DONE);
        InputFilter[] inputFilters = new InputFilter[1];
        inputFilters[0] = new CodepointsLengthInputFilter(70) {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                if (source != null && TextUtils.indexOf(source, '\n') != -1) {
                    doneButton.performClick();
                    return "";
                }
                CharSequence result = super.filter(source, start, end, dest, dstart, dend);
                if (result != null && source != null && result.length() != source.length()) {
                    Vibrator v = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
                    if (v != null) {
                        v.vibrate(200);
                    }
                    LayoutCreator.shakeView(checkTextView, 2, 0);
                }
                return result;
            }
        };
        bioField.setFilters(inputFilters);
        bioField.setMinHeight(LayoutCreator.dp(36));
        bioField.setHint(context.getString(R.string.UserBio));
        bioField.setCursorColor(Theme.getColor(Theme.key_default_text));
        bioField.setCursorSize(LayoutCreator.dp(20));
        bioField.setCursorWidth(1.5f);
        bioField.setOnEditorActionListener((textView, i, keyEvent) -> {
            if (i == EditorInfo.IME_ACTION_DONE && doneButton != null) {
                doneButton.performClick();
                return true;
            }
            return false;
        });
        bioField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                checkTextView.setNumber(70 - Character.codePointCount(s, 0, s.length()), true);
            }
        });
        fieldContainer.addView(bioField, LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, LayoutCreator.WRAP_CONTENT, Gravity.LEFT | Gravity.TOP, 0, 0, 4, 0));
        checkTextView = new NumberTextView(context);
        checkTextView.setCenterAlign(true);
        checkTextView.setTextSize(15);
        checkTextView.setNumber(70, false);
        checkTextView.setTypeface(ResourcesCompat.getFont(context, R.font.main_font));
        checkTextView.setTextColor(Theme.getColor(Theme.key_icon));
        checkTextView.setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_NO);
        fieldContainer.addView(checkTextView, LayoutCreator.createFrame(20, 20, G.isAppRtl ? Gravity.LEFT : Gravity.RIGHT, 0, 4, 4, 0));
        helpTextView = new TextView(context);
        helpTextView.setFocusable(true);
        helpTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
        helpTextView.setTextColor(Theme.getColor(Theme.key_icon));
        helpTextView.setGravity(G.isAppRtl ? Gravity.RIGHT : Gravity.LEFT);
        helpTextView.setText(context.getString(R.string.UserBioInfo));
        helpTextView.setTypeface(ResourcesCompat.getFont(context, R.font.main_font));
        linearLayout.addView(helpTextView, LayoutCreator.createLinear(LayoutCreator.WRAP_CONTENT, LayoutCreator.WRAP_CONTENT, G.isAppRtl ? Gravity.RIGHT : Gravity.LEFT, 24, 10, 24, 0));
        if (realmUserInfo != null && realmUserInfo.getUserInfo().getUsername() != null && realmUserInfo.getUserInfo().getUsername().length() > 0) {
            bioField.setText(realmUserInfo.getUserInfo().getBio());
            bioField.setSelection(bioField.length());
        }
        fragmentView.setBackgroundColor(Theme.getColor(Theme.key_window_background));
        return fragmentView;
    }

    @Override
    public void onResume() {
        super.onResume();
        bioField.requestFocus();
        showKeyboard(bioField);
    }

    private void saveBio() {
        if (getActivity() == null || realmUserInfo == null) {
            return;
        }
        hideKeyboard();
        String currentBio = realmUserInfo.getUserInfo().getBio();
        if (currentBio == null) {
            currentBio = "";
        }
        final String newBio = bioField.getText().toString().replace("\n", "");
        if (currentBio.equals(newBio)) {
            finish();
            return;
        }
        progressDialog.show();
        requestSetBio();
    }

    @Override
    public List<ThemeDescriptor> getThemeDescriptor() {
        List<ThemeDescriptor> themeDescriptors = new ArrayList<>();
        themeDescriptors.add(new ThemeDescriptor(bioField, ThemeDescriptor.FLAG_PROGRESSBAR, Theme.key_default_text));
        themeDescriptors.add(new ThemeDescriptor(bioField, ThemeDescriptor.FLAG_CURSORCOLOR, Theme.key_default_text));
        themeDescriptors.add(new ThemeDescriptor(bioField, ThemeDescriptor.FLAG_HINTTEXTCOLOR, Theme.key_default_text));
        themeDescriptors.add(new ThemeDescriptor(bioField, ThemeDescriptor.FLAG_TEXTCOLOR, Theme.key_default_text));
        themeDescriptors.add(new ThemeDescriptor(checkTextView, ThemeDescriptor.FLAG_TEXTCOLOR, Theme.key_default_text));
        themeDescriptors.add(new ThemeDescriptor(helpTextView, ThemeDescriptor.FLAG_TEXTCOLOR, Theme.key_default_text));
        themeDescriptors.add(new ThemeDescriptor(helpTextView, ThemeDescriptor.FLAG_IMAGECOLOR, Theme.key_default_text));
        themeDescriptors.add(new ThemeDescriptor(toolbar, ThemeDescriptor.FLAG_BACKGROUND, Theme.key_toolbar_background));
        return themeDescriptors;
    }

    @SuppressLint("ResourceType")
    @Override
    public View createToolBar(Context context) {
        toolbar = new Toolbar(context);
        toolbar.setTitle(context.getString(R.string.UserBio));
        toolbar.setBackIcon(R.drawable.ic_ab_back);
        ToolbarItems toolbarItems = toolbar.createToolbarItems();
        doneButton = toolbarItems.addItem(done_button, R.string.icon_check_ok, Color.WHITE);
        doneButton.setContentDescription(context.getString(R.string.Done));
        toolbar.setListener(id -> {
            if (id == -1) {
                finish();
            } else if (id == done_button) {
                saveBio();
            }
        });
        return toolbar;
    }

    private void requestSetBio() {
        new RequestUserProfileSetBio().setBio(bioField.getText().toString(), new OnUserProfileSetBioResponse() {
            @Override
            public void onUserProfileBioResponse(String bioStr) {
                G.runOnUiThread(() -> {
                    progressDialog.dismiss();
                    ProfileFragment.bio.postValue(bioStr);
                    finish();
                });
            }

            @Override
            public void error(int majorCode, int minorCode) {
                G.runOnUiThread(() -> {
                    progressDialog.dismiss();
                    bioField.setText(realmUserInfo.getUserInfo().getBio());
                    bioField.requestFocus();
                    showKeyboard(bioField);
                });
            }

            @Override
            public void timeOut() {
                G.runOnUiThread(() -> {
                    progressDialog.dismiss();
                    bioField.setText(realmUserInfo.getUserInfo().getBio());
                    bioField.requestFocus();
                    showKeyboard(bioField);
                });
            }
        });
    }
}
