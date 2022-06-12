package net.iGap.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import com.afollestad.materialdialogs.MaterialDialog;

import net.iGap.G;
import net.iGap.R;
import net.iGap.adapter.AdapterDialog;
import net.iGap.databinding.FragmentChangePhoneNumberBinding;
import net.iGap.helper.HelperError;
import net.iGap.helper.HelperFragment;
import net.iGap.helper.HelperTracker;
import net.iGap.helper.LayoutCreator;
import net.iGap.messenger.theme.Theme;
import net.iGap.module.CountryReader;
import net.iGap.module.SoftKeyboard;
import net.iGap.module.dialog.WaitingDialog;
import net.iGap.viewmodel.FragmentChangePhoneNumberViewModel;
import net.iGap.viewmodel.WaitTimeModel;

import org.jetbrains.annotations.NotNull;

public class FragmentChangePhoneNumber extends BaseFragment {

    private FragmentChangePhoneNumberViewModel fragmentChangePhoneNumberViewModel;
    private FragmentChangePhoneNumberBinding fragmentChangePhoneNumberBinding;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fragmentChangePhoneNumberViewModel = ViewModelProviders.of(this, new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                return (T) new FragmentChangePhoneNumberViewModel(new CountryReader().readFromAssetsTextFile("country.txt", getContext()));
            }
        }).get(FragmentChangePhoneNumberViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentChangePhoneNumberBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_change_phone_number, container, false);
        fragmentChangePhoneNumberBinding.setFragmentChangePhoneNumberViewModel(fragmentChangePhoneNumberViewModel);
        fragmentChangePhoneNumberBinding.setLifecycleOwner(this);
        return fragmentChangePhoneNumberBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NotNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        HelperTracker.sendTracker(HelperTracker.TRACKER_ENTRY_PHONE);

        fragmentChangePhoneNumberBinding.mainFrame.setBackgroundColor(Theme.getColor(Theme.key_window_background));
        fragmentChangePhoneNumberBinding.phoneNumber.setTextColor(Theme.getColor(Theme.key_title_text));
        fragmentChangePhoneNumberBinding.phoneNumber.setHintTextColor(Theme.getColor(Theme.key_title_text));
        fragmentChangePhoneNumberBinding.title.setTextColor(Theme.getColor(Theme.key_title_text));

        fragmentChangePhoneNumberBinding.description.setTextColor(Theme.getColor(Theme.key_subtitle_text));
        fragmentChangePhoneNumberBinding.country.setTextColor(Theme.getColor(Theme.key_title_text));
        fragmentChangePhoneNumberBinding.t1.setTextColor(Theme.getColor(Theme.key_title_text));
        fragmentChangePhoneNumberBinding.countyCode.setTextColor(Theme.getColor(Theme.key_title_text));
        fragmentChangePhoneNumberBinding.retryView.setTextColor(Theme.getColor(Theme.key_subtitle_text));
        fragmentChangePhoneNumberBinding.rgBtnChoseCountry.setCardBackgroundColor(Theme.getColor(Theme.key_window_background));
        fragmentChangePhoneNumberBinding.backButton.setTextColor(Theme.getColor(Theme.key_theme_color));
        fragmentChangePhoneNumberBinding.backButton.setStrokeColor(ColorStateList.valueOf(Theme.getColor(Theme.key_theme_color)));
        fragmentChangePhoneNumberViewModel.closeKeyword.observe(getViewLifecycleOwner(), aBoolean -> {
            if (aBoolean != null && aBoolean) {
                hideKeyboard();
            }
        });

        fragmentChangePhoneNumberViewModel.showEnteredPhoneNumberStartWithZeroError.observe(getViewLifecycleOwner(), errorMessage -> {
            if (errorMessage != null) {
                Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
            }
        });

        fragmentChangePhoneNumberViewModel.showChooseCountryDialog.observe(getViewLifecycleOwner(), aBoolean -> {
            if (aBoolean != null && aBoolean) {
                showCountryDialog();
            }
        });

        fragmentChangePhoneNumberViewModel.showConfirmPhoneNumberDialog.observe(getViewLifecycleOwner(), phoneNumber -> {
            if (getActivity() != null && phoneNumber != null && !phoneNumber.isEmpty()) {
                new MaterialDialog.Builder(getActivity())
                        .titleColor(Theme.getColor(Theme.key_title_text))
                        .contentColor(Theme.getColor(Theme.key_default_text))
                        .backgroundColor(Theme.getColor(Theme.key_popup_background))
                        .negativeColor(Theme.getColor(Theme.key_button_background))
                        .positiveColor(Theme.getColor(Theme.key_button_background))
                        .content(getString(R.string.Re_dialog_verify_number_part1) + "\n" + phoneNumber + "\n" + getString(R.string.Re_dialog_verify_number_part2))
                        .positiveText(R.string.B_ok)
                        .negativeText(R.string.B_edit)
                        .onPositive((dialog, which) -> fragmentChangePhoneNumberViewModel.confirmPhoneNumber())
                        .show();
            }
        });

        fragmentChangePhoneNumberViewModel.showEnteredPhoneNumberError.observe(getViewLifecycleOwner(), aBoolean -> {
            if (getContext() != null && aBoolean != null) {
                if (aBoolean) {
                    showMessageDialog(R.string.phone_number, R.string.please_enter_correct_phone_number);
                } else {
                    showMessageDialog(R.string.phone_number, R.string.Toast_Minimum_Characters);
                }
            }
        });

        fragmentChangePhoneNumberViewModel.showConnectionErrorDialog.observe(getViewLifecycleOwner(), aBoolean -> {
            if (getContext() != null && aBoolean != null && aBoolean) {
                showMessageDialog(R.string.error, R.string.please_check_your_connenction);
            }
        });

        fragmentChangePhoneNumberViewModel.showDialogWaitTime.observe(getViewLifecycleOwner(), data -> {
            if (data != null) {
                dialogWaitTime(data);
            }
        });

        fragmentChangePhoneNumberViewModel.showErrorMessageEmptyErrorPhoneNumberDialog.observe(getViewLifecycleOwner(), isShow -> {
            if (getContext() != null && isShow != null && isShow) {
                showMessageDialog(R.string.error, R.string.phone_number_is_not_valid);
            }
        });

        fragmentChangePhoneNumberViewModel.showDialogUserBlock.observe(getViewLifecycleOwner(), isShow -> {
            if (getActivity() != null && isShow != null && isShow) {
                showMessageDialog(R.string.USER_VERIFY_BLOCKED_USER, R.string.Toast_Number_Block);
            }
        });

        fragmentChangePhoneNumberViewModel.showError.observe(getViewLifecycleOwner(), messageRes -> {
            if (messageRes != null) {
                HelperError.showSnackMessage(getString(messageRes), false);
            }
        });

        fragmentChangePhoneNumberViewModel.goToActivation.observe(getViewLifecycleOwner(), isShow -> {
            if (getContext() != null && isShow != null && isShow) {
                finish();
                new HelperFragment(getActivity().getSupportFragmentManager(), new FragmentChangePhoneNumberActivation()).setReplace(false).load();
            }
        });

        fragmentChangePhoneNumberViewModel.btnBackClick.observe(getViewLifecycleOwner(), aBoolean -> {
            if (aBoolean != null && aBoolean) {
                finish();
            }
        });
    }

    private void showMessageDialog(int title, int msg) {
        if (getActivity() == null) return;
        new MaterialDialog.Builder(getActivity())
                .titleColor(Theme.getColor(Theme.key_title_text))
                .contentColor(Theme.getColor(Theme.key_default_text))
                .backgroundColor(Theme.getColor(Theme.key_popup_background))
                .negativeColor(Theme.getColor(Theme.key_button_background))
                .positiveColor(Theme.getColor(Theme.key_button_background))
                .title(title)
                .content(msg)
                .positiveText(R.string.ok)
                .show();
    }

    private void showCountryDialog() {
        if (getActivity() != null) {
            Dialog dialogChooseCountry = new Dialog(getActivity());
            dialogChooseCountry.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialogChooseCountry.setContentView(R.layout.rg_dialog);
            dialogChooseCountry.getWindow().setBackgroundDrawable(new ColorDrawable(Theme.getColor(Theme.key_popup_background)));

            int setWidth = (int) (getResources().getDisplayMetrics().widthPixels * 0.9);
            int setHeight = (int) (getResources().getDisplayMetrics().heightPixels * 0.9);
            dialogChooseCountry.getWindow().setLayout(setWidth, setHeight);

            final TextView txtTitle = dialogChooseCountry.findViewById(R.id.rg_txt_titleToolbar);
            SearchView edtSearchView = dialogChooseCountry.findViewById(R.id.rg_edtSearch_toolbar);

            txtTitle.setOnClickListener(view -> {
                edtSearchView.setIconified(false);
                edtSearchView.setIconifiedByDefault(true);
                txtTitle.setVisibility(View.GONE);
            });

            edtSearchView.setOnCloseListener(() -> {
                txtTitle.setVisibility(View.VISIBLE);
                return false;
            });

            final ListView listView = dialogChooseCountry.findViewById(R.id.lstContent);
            AdapterDialog adapterDialog = new AdapterDialog(fragmentChangePhoneNumberViewModel.structCountryArrayList);
            listView.setAdapter(adapterDialog);
            listView.setOnItemClickListener((parent, view, position, id) -> {
                fragmentChangePhoneNumberViewModel.setCountry(adapterDialog.getItem(position));
                dialogChooseCountry.dismiss();
            });

            final ViewGroup root = dialogChooseCountry.findViewById(android.R.id.content);
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

            final View border = dialogChooseCountry.findViewById(R.id.rg_borderButton);
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

            dialogChooseCountry.findViewById(R.id.rg_txt_okDialog).setOnClickListener(v -> dialogChooseCountry.dismiss());

            if (!(getActivity()).isFinishing()) {
                dialogChooseCountry.show();
            }
        }
    }

    private void dialogWaitTime(WaitTimeModel data) {
        if (getActivity() != null && !getActivity().isFinishing()) {
            new WaitingDialog(getActivity(), data).show();
        }
    }

    @Override
    public void onConfigurationChanged(@NotNull Configuration newConfig) {
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            G.isLandscape = true;
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            G.isLandscape = false;
        }
        super.onConfigurationChanged(newConfig);
    }
}

