/*
 * This is the source code of iGap for Android
 * It is licensed under GNU AGPL v3.0
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright © 2017 , iGap - www.iGap.net
 * iGap Messenger | Free, Fast and Secure instant messaging application
 * The idea of the Kianiranian Company - www.kianiranian.com
 * All rights reserved.
 */

package net.iGap.fragments;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

import net.iGap.G;
import net.iGap.R;
import net.iGap.adapter.AdapterDialog;
import net.iGap.databinding.FragmentAddContactBinding;
import net.iGap.helper.HelperAddContact;
import net.iGap.helper.HelperError;
import net.iGap.helper.HelperPermission;
import net.iGap.helper.HelperPreferences;
import net.iGap.helper.HelperToolbar;
import net.iGap.messenger.theme.Theme;
import net.iGap.module.SoftKeyboard;
import net.iGap.observers.interfaces.OnUserContactEdit;
import net.iGap.observers.interfaces.ToolbarListener;
import net.iGap.module.CountryReader;
import net.iGap.module.SHP_SETTING;
import net.iGap.module.structs.StructListOfContact;
import net.iGap.request.RequestUserContactImport;
import net.iGap.request.RequestUserContactsEdit;
import net.iGap.viewmodel.FragmentAddContactViewModel;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static net.iGap.module.Contacts.showLimitDialog;

public class FragmentAddContact extends BaseFragment implements ToolbarListener, OnUserContactEdit {

    public static final String NAME = "name";
    public static final String PHONE = "PHONE";
    private static final String CONTACT_MODE = "MODE";
    private static final String CONTACT_ID = "CONTACT_ID";
    private static final String CONTACT_NAME = "NAME";
    private static final String CONTACT_FAMILY = "FAMILY";
    private OnContactUpdate onContactUpdate;
    private HelperToolbar mHelperToolbar;
    private long mContactId = 0;
    private long mContactPhone = 0;
    private String mContactName = "", mContactFamily = "";
    private ContactMode pageMode;
    private FragmentAddContactViewModel viewModel;
    private FragmentAddContactBinding binding;

    public static FragmentAddContact newInstance(String phone, ContactMode mode) {
        FragmentAddContact fragment = new FragmentAddContact();
        Bundle bundle = new Bundle();
        bundle.putString(CONTACT_MODE, mode.name());
        bundle.putString(PHONE, phone);
        fragment.setArguments(bundle);
        return fragment;
    }

    public static FragmentAddContact newInstance(long contactId, String phone, String name, String lastName, ContactMode mode, OnContactUpdate onContactUpdate) {
        FragmentAddContact fragment = new FragmentAddContact();
        fragment.onContactUpdate = onContactUpdate;
        Bundle bundle = new Bundle();
        bundle.putLong(CONTACT_ID, contactId);
        bundle.putString(CONTACT_MODE, mode.name());
        bundle.putString(CONTACT_NAME, name);
        bundle.putString(CONTACT_FAMILY, lastName);
        bundle.putString(PHONE, phone);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(this, new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                return (T) new FragmentAddContactViewModel(new CountryReader().readFromAssetsTextFile("country.txt", getContext()));
            }
        }).get(FragmentAddContactViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_add_contact, container, false);
        binding.setViewModel(viewModel);
        binding.setLifecycleOwner(this);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NotNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            pageMode = ContactMode.valueOf(bundle.getString(CONTACT_MODE, ContactMode.ADD.name()));
            String contactName = bundle.getString(NAME, "");
            binding.acEdtFirstName.setText(contactName);
            binding.acEdtFirstName.setTextColor(Theme.getColor(Theme.key_title_text));
            binding.acEdtFirstName.setHintTextColor(Theme.getColor(Theme.key_theme_color));
            binding.acEdtLastName.setHintTextColor(Theme.getColor(Theme.key_theme_color));
            binding.acEdtLastName.setTextColor(Theme.getColor(Theme.key_title_text));
            binding.acTxtCodeCountry.setHintTextColor(Theme.getColor(Theme.key_theme_color));
            binding.acTxtCodeCountry.setTextColor(Theme.getColor(Theme.key_title_text));
            binding.acEdtPhoneNumber.setHintTextColor(Theme.getColor(Theme.key_theme_color));
            binding.acEdtPhoneNumber.setTextColor(Theme.getColor(Theme.key_title_text));
            if (pageMode == ContactMode.EDIT) {
                mContactId = bundle.getLong(CONTACT_ID, 0);
                mContactName = bundle.getString(CONTACT_NAME, "");
                mContactFamily = bundle.getString(CONTACT_FAMILY, "");
                mContactPhone = Long.valueOf(bundle.getString(PHONE, "+0").substring(1));
                binding.acEdtFirstName.setText(mContactName);
                binding.acEdtLastName.setText(mContactFamily);
                binding.acEdtPhoneNumber.setEnabled(false);
                binding.acTxtCodeCountry.setEnabled(false);
            }
        }
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        binding.acLayoutParent.setBackgroundColor(Theme.getColor(Theme.key_window_background));

        binding.acEdtFirstName.setTextColor(Theme.getColor(Theme.key_default_text));
        binding.acEdtFirstName.setHintTextColor(Theme.getColor(Theme.key_default_text));
        ColorStateList colorStateList = ColorStateList.valueOf(Theme.getColor(Theme.key_default_text));
        ViewCompat.setBackgroundTintList(binding.acEdtFirstName, colorStateList);
        binding.acEdtLastName.setTextColor(Theme.getColor(Theme.key_default_text));
        binding.acEdtLastName.setHintTextColor(Theme.getColor(Theme.key_default_text));
        ViewCompat.setBackgroundTintList(binding.acEdtLastName, colorStateList);
        binding.acTxtCodeCountry.setTextColor(Theme.getColor(Theme.key_default_text));
        binding.acTxtCodeCountry.setHintTextColor(Theme.getColor(Theme.key_default_text));
        ViewCompat.setBackgroundTintList(binding.acTxtCodeCountry, colorStateList);
        binding.acEdtPhoneNumber.setTextColor(Theme.getColor(Theme.key_default_text));
        binding.acEdtPhoneNumber.setHintTextColor(Theme.getColor(Theme.key_default_text));
        ViewCompat.setBackgroundTintList(binding.acEdtPhoneNumber, colorStateList);
        initComponent();
    }

    private void initComponent() {
        setupToolbar();
        String phoneFromUrl = "";
        String countryCodee = "";
        try {
            phoneFromUrl = getArguments().getString(PHONE);
            if (phoneFromUrl != null && phoneFromUrl.length() > 0) {
                if (phoneFromUrl.startsWith("+")) {
                    PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
                    try {
                        Phonenumber.PhoneNumber numberProto = phoneUtil.parse(phoneFromUrl, "");
                        phoneFromUrl = numberProto.getNationalNumber() + "";
                        countryCodee = numberProto.getCountryCode() + "";
                    } catch (NumberParseException e) {
                        phoneFromUrl = phoneFromUrl.substring(1);
                    }
                } else if (phoneFromUrl.startsWith("0")) {
                    phoneFromUrl = phoneFromUrl.substring(1);
                }
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        binding.acTxtCodeCountry.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                closeKeyboard(binding.acTxtCodeCountry);
            }
        });

        if (phoneFromUrl != null && phoneFromUrl.length() > 0) {
            binding.acEdtPhoneNumber.setText(phoneFromUrl);
        }

        binding.acEdtFirstName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                isEnableSetButton();
            }
        });

        binding.acEdtLastName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                isEnableSetButton();
            }
        });

        binding.acEdtPhoneNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                isEnableSetButton();
            }
        });

        binding.acEdtPhoneNumber.setOnFocusChangeListener((v, hasFocus) -> {
            binding.phoneNumberError.setText(R.string.empty_error_message);
            binding.phoneNumberError.setTextColor(Theme.getColor(Theme.key_red));
            if (binding.acEdtPhoneNumber.getText().toString().startsWith("0")) {
                binding.phoneNumberError.setText(R.string.Toast_First_0);
                binding.acEdtPhoneNumber.setText("");
            }
        });

        G.fragmentActivity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        if (countryCodee.length() > 0) {
            binding.acTxtCodeCountry.setText("+" + countryCodee);
            CountryReader countryReade = new CountryReader();
            StringBuilder fileListBuilder = countryReade.readFromAssetsTextFile("country.txt", G.fragmentActivity);
            String[] listArray = fileListBuilder.toString().split("\\r?\\n");

            for (String aListArray : listArray) {
                String[] listItem = aListArray.split(";");
                if (countryCodee.equals(listItem[0])) {
                    binding.acTxtCodeCountry.setText(listItem[2]);
                    if (listItem.length > 3) {
                        if (!listItem[3].equals(" ")) {
                            binding.acEdtPhoneNumber.setMask(listItem[3].replace("X", "#").replace(" ", "-"));
                        }
                    }
                    break;
                }
            }
        }

        viewModel.getCodeCountryClick().observe(getViewLifecycleOwner(), isClick -> {
            if (isClick && getActivity() != null) {
                showCountryDialog();
                closeKeyboard(getView());
            }
        });

        viewModel.getHasError().observe(getViewLifecycleOwner(), hasError -> {
            if (hasError) {
                HelperError.showSnackMessage(context.getResources().getString(R.string.error), true);
            }
        });
    }

    private void showCountryDialog() {
        if (getActivity() != null) {
            Dialog dialog = new Dialog(getActivity());
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.rg_dialog);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            int setWidth = (int) (getResources().getDisplayMetrics().widthPixels * 0.9);
            int setHeight = (int) (getResources().getDisplayMetrics().heightPixels * 0.9);
            dialog.getWindow().setLayout(setWidth, setHeight);
            final TextView txtTitle = dialog.findViewById(R.id.rg_txt_titleToolbar);
            SearchView edtSearchView = dialog.findViewById(R.id.rg_edtSearch_toolbar);
            txtTitle.setTextColor(Theme.getColor(Theme.key_icon));

            txtTitle.setOnClickListener(view -> {
                edtSearchView.setIconified(false);
                edtSearchView.setIconifiedByDefault(true);
                txtTitle.setVisibility(View.GONE);
            });

            edtSearchView.setOnCloseListener(() -> {
                txtTitle.setVisibility(View.VISIBLE);
                return false;
            });

            final ListView listView = dialog.findViewById(R.id.lstContent);
            AdapterDialog adapterDialog = new AdapterDialog(viewModel.structCountryList);
            listView.setAdapter(adapterDialog);
            listView.setOnItemClickListener((parent, view, position, id) -> {
                viewModel.setCountry(adapterDialog.getItem(position));
                dialog.dismiss();
            });

            final ViewGroup root = dialog.findViewById(android.R.id.content);
            InputMethodManager im = (InputMethodManager) getContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
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

            final View border = dialog.findViewById(R.id.rg_borderButton);
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

            AdapterDialog.mSelectedVariation = -1;

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
            dialog.findViewById(R.id.rg_txt_okDialog).setOnClickListener(v -> dialog.dismiss());

            if (!(getActivity()).isFinishing()) {
                dialog.show();
            }
        }
    }

    private void setupToolbar() {
        String toolbarTitle = pageMode == ContactMode.ADD ? getString(R.string.menu_add_contact) : getString(R.string.edit);
        mHelperToolbar = HelperToolbar.create()
                .setContext(getContext())
                .setLifecycleOwner(getViewLifecycleOwner())
                .setLeftIcon(R.string.icon_back)
                .setRightIcons(R.string.icon_sent)
                .setDefaultTitle(toolbarTitle)
                .setLogoShown(true)
                .setListener(this);
        binding.frgAddContactToolbar.addView(mHelperToolbar.getView());
    }

    private void isEnableSetButton() {
        if ((binding.acEdtFirstName.getText().toString().length() > 0 || binding.acEdtLastName.getText().toString().length() > 0) && binding.acEdtPhoneNumber.getText().toString().length() > 0) {
            mHelperToolbar.getRightButton().setEnabled(true);
        } else {
            mHelperToolbar.getRightButton().setEnabled(false);
        }
    }

    private void changePage(View view) {
        InputMethodManager imm = (InputMethodManager) G.context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        removeFromBaseFragment(FragmentAddContact.this);
    }

    private void addContactToServer() {
        if (HelperPreferences.getInstance().readBoolean(SHP_SETTING.FILE_NAME, SHP_SETTING.EXCEED_CONTACTS_DIALOG)) {
            showLimitDialog();
            return;
        }
        String _phone = binding.acEdtPhoneNumber.getText().toString();
        String codeCountry = binding.acTxtCodeCountry.getText().toString();
        String saveNumber = codeCountry + _phone;
        List<StructListOfContact> contacts = new ArrayList<>();
        StructListOfContact contact = new StructListOfContact();
        contact.firstName = binding.acEdtFirstName.getText().toString();
        contact.lastName = binding.acEdtLastName.getText().toString();
        contact.phone = saveNumber;
        contacts.add(contact);
        new RequestUserContactImport().contactImport(contacts, true, RequestUserContactImport.KEY);
    }

    private void addToContactList(View view) {
        if (binding.acEdtFirstName.getText().toString().length() > 0 || binding.acEdtLastName.getText().toString().length() > 0) {
            if (binding.acEdtPhoneNumber.getText().toString().length() > 0) {
                final String phone = binding.acEdtPhoneNumber.getText().toString();
                final String firstName = binding.acEdtFirstName.getText().toString();
                final String lastName = binding.acEdtLastName.getText().toString();
                final String codeNumber = binding.acTxtCodeCountry.getText().toString();
                String displayName = firstName + " " + lastName;

                if (pageMode == ContactMode.ADD) {
                    changePage(view);
                    HelperAddContact.addContact(displayName, codeNumber, phone);
                } else if (pageMode == ContactMode.EDIT) {
                    closeKeyboard(view);
                    binding.loader.setVisibility(View.VISIBLE);
                    G.onUserContactEdit = this;
                    new RequestUserContactsEdit().contactsEdit(mContactId, mContactPhone, firstName, lastName);
                }

            } else {
                Toast.makeText(G.context, R.string.please_enter_phone_number, Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(G.context, R.string.please_enter_firstname_or_lastname, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onLeftIconClickListener(View view) {
        changePage(view);
    }

    @Override
    public void onRightIconClickListener(View view) {
        binding.acEdtPhoneNumber.clearFocus();
        if (!TextUtils.isEmpty(binding.acEdtPhoneNumber.getText())) {
            if (pageMode == ContactMode.ADD) {
                new MaterialDialog.Builder(G.fragmentActivity).title(R.string.add_to_list_contact).content(R.string.text_add_to_list_contact).positiveText(R.string.yes).onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        addContactToServer();
                        final int permissionWriteContact = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_CONTACTS);
                        if (permissionWriteContact != PackageManager.PERMISSION_GRANTED) {
                            try {
                                HelperPermission.getContactPermision(G.fragmentActivity, null);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else {
                            addToContactList(view);
                        }
                    }
                }).negativeText(R.string.no).onNegative((dialog, which) -> {
                    addContactToServer();
                    dialog.dismiss();
                    G.fragmentActivity.onBackPressed();
                }).show();

            } else if (pageMode == ContactMode.EDIT) {
                new MaterialDialog.Builder(G.fragmentActivity)
                        .title(R.string.edit_contact)
                        .content(R.string.are_you_sure_edit_contact)
                        .positiveText(R.string.yes)
                        .negativeText(R.string.no)
                        .onNegative((dialog, which) -> {
                            dialog.dismiss();
                        })
                        .onPositive((dialog, which) -> {
                            final int permissionWriteContact = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_CONTACTS);
                            if (permissionWriteContact != PackageManager.PERMISSION_GRANTED) {
                                try {
                                    HelperPermission.getContactPermision(G.fragmentActivity, null);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                addToContactList(view);
                            }
                            dialog.dismiss();
                        })
                        .show();
            }
        }
    }

    @Override
    public void onContactEdit(String firstName, String lastName, String initials) {
        G.handler.postDelayed(() -> {
            binding.loader.setVisibility(View.GONE);
            //HelperError.showSnackMessage(getString(R.string.user_edited), false);
            if (onContactUpdate != null)
                onContactUpdate.updateContact(firstName, lastName);
            popBackStackFragment();
        }, 100);
    }

    @Override
    public void onContactEditTimeOut() {
        G.handler.post(() -> {
            binding.loader.setVisibility(View.GONE);
            Toast.makeText(binding.loader.getContext(), R.string.server_do_not_response, Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public void onContactEditError(int majorCode, int minorCode) {
        G.handler.post(() -> {
            binding.loader.setVisibility(View.GONE);
            Toast.makeText(binding.loader.getContext(), R.string.server_error, Toast.LENGTH_SHORT).show();
        });
    }

    public enum ContactMode {
        ADD, EDIT
    }

    @FunctionalInterface
    public interface OnContactUpdate {
        void updateContact(String name, String family);
    }

}
