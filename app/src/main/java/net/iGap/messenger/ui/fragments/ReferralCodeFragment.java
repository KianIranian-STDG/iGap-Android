
package net.iGap.messenger.ui.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.res.ResourcesCompat;

import net.iGap.G;
import net.iGap.R;
import net.iGap.adapter.AdapterDialog;
import net.iGap.fragments.BaseFragment;
import net.iGap.helper.LayoutCreator;
import net.iGap.messenger.dialog.AlertDialog;
import net.iGap.messenger.theme.Theme;
import net.iGap.messenger.theme.ThemeDescriptor;
import net.iGap.messenger.ui.components.AlertsCreator;
import net.iGap.messenger.ui.components.CodepointsLengthInputFilter;
import net.iGap.messenger.ui.components.EditTextBoldCursor;
import net.iGap.messenger.ui.toolBar.NumberTextView;
import net.iGap.messenger.ui.toolBar.Toolbar;
import net.iGap.messenger.ui.toolBar.ToolbarItems;
import net.iGap.module.CountryListComparator;
import net.iGap.module.CountryReader;
import net.iGap.module.SoftKeyboard;
import net.iGap.module.structs.StructCountry;
import net.iGap.observers.interfaces.OnUserProfileSetRepresentative;
import net.iGap.request.RequestUserProfileSetRepresentative;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static net.iGap.module.AndroidUtils.showKeyboard;

public class ReferralCodeFragment extends BaseFragment {
    private final static int done_button = 1;
    private EditTextBoldCursor referralCodeField;
    private View doneButton;
    private NumberTextView checkTextView;
    private TextView helpTextView;
    private AppCompatTextView countryCode;
    private AlertDialog progressDialog;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        progressDialog = new AlertDialog(context, 3);
    }

    @Override
    public View createView(Context context) {
        fragmentView = new LinearLayout(context);
        LinearLayout linearLayout = (LinearLayout) fragmentView;
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        fragmentView.setOnTouchListener((v, event) -> true);
        FrameLayout fieldContainer = new FrameLayout(context);
        linearLayout.addView(fieldContainer, LayoutCreator.createLinear(LayoutCreator.MATCH_PARENT, LayoutCreator.WRAP_CONTENT, 24, 24, 20, 0));
        referralCodeField = new EditTextBoldCursor(context);
        referralCodeField.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
        referralCodeField.setHintTextColor(Theme.getColor(Theme.key_title_text));
        referralCodeField.setTypeface(ResourcesCompat.getFont(context, R.font.main_font));
        referralCodeField.setTextColor(Theme.getColor(Theme.key_default_text));
        referralCodeField.setBackground(Theme.createEditTextDrawable(context, false));
        referralCodeField.setMaxLines(1);
        referralCodeField.setPadding(LayoutCreator.dp(24), 0, LayoutCreator.dp(24), LayoutCreator.dp(6));
        referralCodeField.setGravity(G.isAppRtl ? Gravity.RIGHT : Gravity.LEFT);
        referralCodeField.setImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI);
        referralCodeField.setInputType(InputType.TYPE_CLASS_PHONE);
        referralCodeField.setImeOptions(EditorInfo.IME_ACTION_DONE);
        InputFilter[] inputFilters = new InputFilter[1];
        inputFilters[0] = new CodepointsLengthInputFilter(11) {
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
        referralCodeField.setFilters(inputFilters);
        referralCodeField.setMinHeight(LayoutCreator.dp(36));
        referralCodeField.setHint(context.getString(R.string.referral_code));
        referralCodeField.setCursorColor(Theme.getColor(Theme.key_default_text));
        referralCodeField.setCursorSize(LayoutCreator.dp(20));
        referralCodeField.setCursorWidth(1.5f);
        referralCodeField.setOnEditorActionListener((textView, i, keyEvent) -> {
            if (i == EditorInfo.IME_ACTION_DONE && doneButton != null) {
                doneButton.performClick();
                return true;
            }
            return false;
        });
        referralCodeField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                checkTextView.setNumber(11 - Character.codePointCount(s, 0, s.length()), true);
            }
        });
        fieldContainer.addView(referralCodeField, LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, LayoutCreator.WRAP_CONTENT, Gravity.LEFT | Gravity.TOP, 0, 0, 4, 0));
        checkTextView = new NumberTextView(context);
        checkTextView.setCenterAlign(true);
        checkTextView.setTextSize(15);
        checkTextView.setNumber(11, false);
        checkTextView.setTypeface(ResourcesCompat.getFont(context, R.font.main_font));
        checkTextView.setTextColor(Theme.getColor(Theme.key_icon));
        checkTextView.setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_NO);
        fieldContainer.addView(checkTextView, LayoutCreator.createFrame(20, 20, G.isAppRtl ? Gravity.LEFT : Gravity.RIGHT, 0, 4, 4, 0));
        countryCode = new AppCompatTextView(context);
        countryCode.setTextColor(Theme.getColor(Theme.key_default_text));
        countryCode.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
        countryCode.setTypeface(ResourcesCompat.getFont(context, R.font.main_font_bold));
        countryCode.setText("98");
        countryCode.setOnClickListener(v -> showDialogSelectCountry());
        fieldContainer.addView(countryCode, LayoutCreator.createFrame(20, 20, G.isAppRtl ? Gravity.RIGHT : Gravity.LEFT, 4, 4, 0, 0));
        helpTextView = new TextView(context);
        helpTextView.setFocusable(true);
        helpTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
        helpTextView.setTextColor(Theme.getColor(Theme.key_icon));
        helpTextView.setGravity(G.isAppRtl ? Gravity.RIGHT : Gravity.LEFT);
        helpTextView.setText(context.getString(R.string.set_referral_code));
        helpTextView.setTypeface(ResourcesCompat.getFont(context, R.font.main_font));
        linearLayout.addView(helpTextView, LayoutCreator.createLinear(LayoutCreator.WRAP_CONTENT, LayoutCreator.WRAP_CONTENT, G.isAppRtl ? Gravity.RIGHT : Gravity.LEFT, 24, 10, 24, 0));
        referralCodeField.setSelection(referralCodeField.length());
        fragmentView.setBackgroundColor(Theme.getColor(Theme.key_window_background));
        return fragmentView;
    }

    private void showDialogSelectCountry() {
        if (getActivity() != null) {
            Dialog dialogChooseCountry = new Dialog(getActivity());
            dialogChooseCountry.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialogChooseCountry.setContentView(R.layout.rg_dialog);

            dialogChooseCountry.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            int setWidth = (int) (getResources().getDisplayMetrics().widthPixels * 0.9);
            int setHeight = (int) (getResources().getDisplayMetrics().heightPixels * 0.9);
            dialogChooseCountry.getWindow().setLayout(setWidth, setHeight);

            final TextView txtTitle = dialogChooseCountry.findViewById(R.id.rg_txt_titleToolbar);
            SearchView edtSearchView = dialogChooseCountry.findViewById(R.id.rg_edtSearch_toolbar);
            AppCompatTextView rg_txt_titleToolbar = dialogChooseCountry.findViewById(R.id.rg_txt_titleToolbar);
            rg_txt_titleToolbar.setTextColor(Theme.getColor(Theme.key_icon));
            LinearLayout rootView = dialogChooseCountry.findViewById(R.id.country_root);
            rootView.setBackground(Theme.tintDrawable(getResources().getDrawable(R.drawable.dialog_background), getContext(), Theme.getColor(Theme.key_window_background)));

            txtTitle.setOnClickListener(view -> {
                edtSearchView.setIconified(false);
                edtSearchView.setIconifiedByDefault(true);
                txtTitle.setVisibility(View.GONE);
            });

            edtSearchView.setOnCloseListener(() -> {
                txtTitle.setVisibility(View.VISIBLE);
                return false;
            });

            ListView listView = dialogChooseCountry.findViewById(R.id.lstContent);
            ArrayList<StructCountry> structCountries = new ArrayList<>();
            CountryReader countryReade = new CountryReader();
            StringBuilder fileListBuilder = countryReade.readFromAssetsTextFile("country.txt", G.context);
            String list = fileListBuilder.toString();
            String[] listArray = list.split("\\r?\\n");
            for (String s : listArray) {
                StructCountry structCountry = new StructCountry();
                String[] listItem = s.split(";");
                structCountry.setCountryCode(listItem[0]);
                structCountry.setAbbreviation(listItem[1]);
                structCountry.setName(listItem[2]);
                if (listItem.length > 3) {
                    structCountry.setPhonePattern(listItem[3]);
                } else {
                    structCountry.setPhonePattern(" ");
                }
                structCountries.add(structCountry);
            }
            Collections.sort(structCountries, new CountryListComparator());
            AdapterDialog adapterDialog = new AdapterDialog(structCountries);
            listView.setAdapter(adapterDialog);
            listView.setOnItemClickListener((parent, view, position, id) -> {
                countryCode.setText(adapterDialog.getItem(position).getCountryCode());
                dialogChooseCountry.dismiss();
            });

            ViewGroup root = dialogChooseCountry.findViewById(android.R.id.content);
            InputMethodManager im = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
            SoftKeyboard softKeyboard = new SoftKeyboard(root, im);
            softKeyboard.setSoftKeyboardCallback(new SoftKeyboard.SoftKeyboardChanged() {
                @Override
                public void onSoftKeyboardHide() {
                    G.handler.post(() -> {
                        if (edtSearchView.getQuery().toString().length() > 0) {
                            edtSearchView.setIconified(false);
                            edtSearchView.clearFocus();
                            txtTitle.setVisibility(View.GONE);
                        } else {
                            edtSearchView.setIconified(true);
                            txtTitle.setVisibility(View.VISIBLE);
                        }
                        adapterDialog.notifyDataSetChanged();
                    });
                }

                @Override
                public void onSoftKeyboardShow() {
                    G.handler.post(() -> txtTitle.setVisibility(View.GONE));
                }
            });

            View border = dialogChooseCountry.findViewById(R.id.rg_borderButton);
            listView.setOnScrollListener(new AbsListView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(AbsListView absListView, int i) {

                }

                @Override
                public void onScroll(AbsListView absListView, int i, int i1, int i2) {
                    if (i > 0) {
                        border.setVisibility(View.VISIBLE);
                    } else {
                        border.setVisibility(View.GONE);
                    }
                }
            });

            adapterDialog.notifyDataSetChanged();

            edtSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String s) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String s) {
                    adapterDialog.getFilter().filter(s);
                    return false;
                }
            });

            dialogChooseCountry.findViewById(R.id.rg_txt_okDialog).setOnClickListener(v -> dialogChooseCountry.dismiss());
            dialogChooseCountry.show();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        referralCodeField.requestFocus();
        showKeyboard(referralCodeField);
    }

    private boolean checkReferralCode(String phoneNumber) {
        if (!TextUtils.isEmpty(phoneNumber)) {
            checkTextView.setVisibility(View.VISIBLE);
        } else {
            checkTextView.setVisibility(View.GONE);
        }
        if (phoneNumber.length() == 0) {
            return true;
        }
        if (phoneNumber == null || phoneNumber.length() < 11) {
            AlertsCreator.showSimpleAlert(this, context.getString(R.string.UsernameInvalidShort));
            return false;
        }
        return true;
    }

    private void saveReferralCode() {
        String referralCode = referralCodeField.getText().toString();

        if (!checkReferralCode(referralCode)) {
            return;
        }
        if (referralCode != null && referralCode.startsWith("0")) {
            referralCode = referralCode.substring(1);
        }
        referralCode = countryCode.getText().toString() + referralCode;
        if (getActivity() == null ) {
            return;
        }
        hideKeyboard();
        setRequestSetReferral(referralCode);
        progressDialog.show();
    }

    @Override
    public List<ThemeDescriptor> getThemeDescriptor() {
        List<ThemeDescriptor> themeDescriptors = new ArrayList<>();
        themeDescriptors.add(new ThemeDescriptor(checkTextView, ThemeDescriptor.FLAG_TEXTCOLOR, Theme.key_default_text));
        themeDescriptors.add(new ThemeDescriptor(helpTextView, ThemeDescriptor.FLAG_TEXTCOLOR, Theme.key_default_text));
        themeDescriptors.add(new ThemeDescriptor(referralCodeField, ThemeDescriptor.FLAG_TEXTCOLOR, Theme.key_default_text));
        themeDescriptors.add(new ThemeDescriptor(referralCodeField, ThemeDescriptor.FLAG_PROGRESSBAR, Theme.key_default_text));
        themeDescriptors.add(new ThemeDescriptor(referralCodeField, ThemeDescriptor.FLAG_CURSORCOLOR, Theme.key_default_text));
        themeDescriptors.add(new ThemeDescriptor(referralCodeField, ThemeDescriptor.FLAG_HINTTEXTCOLOR, Theme.key_default_text));
        themeDescriptors.add(new ThemeDescriptor(toolbar, ThemeDescriptor.FLAG_BACKGROUND, Theme.key_toolbar_background));
        return themeDescriptors;
    }


    @SuppressLint("ResourceType")
    @Override
    public View createToolBar(Context context) {
        toolbar = new Toolbar(context);
        toolbar.setTitle(context.getString(R.string.referral_code));
        toolbar.setBackIcon(R.drawable.ic_ab_back);
        ToolbarItems toolbarItems = toolbar.createToolbarItems();
        doneButton = toolbarItems.addItem(done_button, R.string.icon_check_ok, Color.WHITE);
        doneButton.setContentDescription(context.getString(R.string.Done));
        toolbar.setListener(id -> {
            if (id == -1) {
                finish();
            } else if (id == done_button) {
                saveReferralCode();
            }
        });
        return toolbar;
    }

    private void setRequestSetReferral(String phoneNumber) {
        new RequestUserProfileSetRepresentative().userProfileSetRepresentative(phoneNumber, new OnUserProfileSetRepresentative() {
            @Override
            public void onSetRepresentative(String phone) {
                G.handler.post(() -> {
                    progressDialog.dismiss();
                    finish();
                });
            }

            @Override
            public void onErrorSetRepresentative(int majorCode, int minorCode) {
                G.handler.post(() -> {
                    progressDialog.dismiss();
                    referralCodeField.setText("");
                    referralCodeField.requestFocus();
                    showKeyboard(referralCodeField);
                    if (majorCode == 10177) {
                        if (minorCode == 2) {
                            AlertsCreator.showSimpleAlert(ReferralCodeFragment.this, context.getString(R.string.referral_error_yourself));
                        } else {
                            AlertsCreator.showSimpleAlert(ReferralCodeFragment.this, context.getString(R.string.phone_number_is_not_valid));
                        }
                    } else if (majorCode == 10178) {
                        if (minorCode == 2) {
                            AlertsCreator.showSimpleAlert(ReferralCodeFragment.this, context.getString(R.string.already_registered));
                        } else {
                            AlertsCreator.showSimpleAlert(ReferralCodeFragment.this, context.getString(R.string.server_error));
                        }
                    }
                });
            }
        });
    }
}









