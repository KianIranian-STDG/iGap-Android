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

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
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
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.SearchView;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import net.iGap.Config;
import net.iGap.G;
import net.iGap.R;
import net.iGap.activities.ActivityRegistration;
import net.iGap.adapter.AdapterDialog;
import net.iGap.databinding.ActivityRegisterBinding;
import net.iGap.dialog.DefaultRoundDialog;
import net.iGap.helper.HelperError;
import net.iGap.module.AndroidUtils;
import net.iGap.module.CountryReader;
import net.iGap.module.SoftKeyboard;
import net.iGap.viewmodel.FragmentRegisterViewModel;
import net.iGap.viewmodel.WaitTimeModel;

import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public class FragmentRegister extends BaseFragment {

    private FragmentRegisterViewModel fragmentRegisterViewModel;
    private ActivityRegisterBinding fragmentRegisterBinding;
    private MaterialDialog dialogQrCode;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fragmentRegisterViewModel = ViewModelProviders.of(this, new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                return (T) new FragmentRegisterViewModel(new CountryReader().readFromAssetsTextFile("country.txt", getContext()));
            }
        }).get(FragmentRegisterViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentRegisterBinding = DataBindingUtil.inflate(inflater, R.layout.activity_register, container, false);
        fragmentRegisterBinding.setFragmentRegisterViewModel(fragmentRegisterViewModel);
        fragmentRegisterBinding.setLifecycleOwner(this);
        return fragmentRegisterBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NotNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        String t = String.format(getString(R.string.terms_and_condition), getString(R.string.terms_and_condition_clickable));
        SpannableString ss = new SpannableString(t);
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NotNull View textView) {
                showDialogTermAndCondition();
            }

            @Override
            public void updateDrawState(@NotNull TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false);
            }
        };
        ss.setSpan(clickableSpan, t.indexOf(getString(R.string.terms_and_condition_clickable)), t.indexOf(getString(R.string.terms_and_condition_clickable)) + getString(R.string.terms_and_condition_clickable).length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        fragmentRegisterBinding.conditionText.setText(ss);
        fragmentRegisterBinding.conditionText.setMovementMethod(LinkMovementMethod.getInstance());
        fragmentRegisterBinding.conditionText.setHighlightColor(Color.TRANSPARENT);

        fragmentRegisterViewModel.showConditionErrorDialog.observe(getViewLifecycleOwner(), aBoolean -> {
            if (aBoolean != null && aBoolean) {
                showDialogConditionError();
            }
        });

        fragmentRegisterViewModel.goNextStep.observe(getViewLifecycleOwner(), aBoolean -> {
            if (getActivity() instanceof ActivityRegistration && aBoolean != null && aBoolean) {
                ((ActivityRegistration) getActivity()).loadFragment(new FragmentActivation(), true);
            }
        });

        fragmentRegisterViewModel.closeKeyword.observe(getViewLifecycleOwner(), aBoolean -> {
            if (aBoolean != null && aBoolean) {
                hideKeyboard();
            }
        });

        fragmentRegisterViewModel.showEnteredPhoneNumberStartWithZeroError.observe(getViewLifecycleOwner(), aBoolean -> {
            if (aBoolean != null && aBoolean) {
                Toast.makeText(getContext(), R.string.Toast_First_0, Toast.LENGTH_SHORT).show();
            }
        });

        fragmentRegisterViewModel.showChooseCountryDialog.observe(getViewLifecycleOwner(), aBoolean -> {
            if (aBoolean != null && aBoolean) {
                showCountryDialog();
            }
        });

        fragmentRegisterViewModel.showConfirmPhoneNumberDialog.observe(getViewLifecycleOwner(), phoneNumber -> {
            if (getActivity() != null && phoneNumber != null) {
                new DefaultRoundDialog(getActivity()).setMessage(getString(R.string.Re_dialog_verify_number_part1) + "\n" +
                        phoneNumber + "\n" + getString(R.string.Re_dialog_verify_number_part2))
                        .setPositiveButton(R.string.B_ok, (dialog, which) -> fragmentRegisterViewModel.confirmPhoneNumber())
                        .setNegativeButton(R.string.B_edit, null)
                        .show();
            }
        });

        fragmentRegisterViewModel.showEnteredPhoneNumberError.observe(getViewLifecycleOwner(), aBoolean -> {
            if (getContext() != null && aBoolean != null) {
                if (aBoolean) {
                    new DefaultRoundDialog(getContext()).setTitle(R.string.phone_number).setMessage(R.string.please_enter_correct_phone_number).setPositiveButton(R.string.B_ok, null).show();
                } else {
                    new DefaultRoundDialog(getContext()).setTitle(R.string.phone_number).setMessage(R.string.Toast_Minimum_Characters).setPositiveButton(R.string.B_ok, null).show();
                }
            }
        });

        fragmentRegisterViewModel.showConnectionErrorDialog.observe(getViewLifecycleOwner(), aBoolean -> {
            if (getContext() != null && aBoolean != null && aBoolean) {
                new DefaultRoundDialog(getContext()).setTitle(R.string.error).setMessage(R.string.please_check_your_connenction).setPositiveButton(R.string.ok, null).show();
            }
        });

        fragmentRegisterViewModel.showDialogWaitTime.observe(getViewLifecycleOwner(), data -> {
            if (data != null) {
                dialogWaitTime(data);
            }
        });

        fragmentRegisterViewModel.showErrorMessageEmptyErrorPhoneNumberDialog.observe(getViewLifecycleOwner(), isShow -> {
            if (getContext() != null && isShow != null && isShow) {
                new DefaultRoundDialog(getContext()).setTitle(R.string.error).setMessage(R.string.phone_number_is_not_valid).setPositiveButton(R.string.ok, null).show();
            }
        });

        fragmentRegisterViewModel.showDialogQrCode.observe(getViewLifecycleOwner(), integer -> {
            if (integer != null) {
                showQrCodeDialog(integer);
            }
        });

        fragmentRegisterViewModel.shareQrCodeIntent.observe(getViewLifecycleOwner(), uri -> {
            if (getActivity() != null && uri != null) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_STREAM, uri);
                getActivity().startActivity(Intent.createChooser(intent, getString(R.string.share_image_from_igap)));
            }
        });

        fragmentRegisterViewModel.hideDialogQRCode.observe(getViewLifecycleOwner(), aBoolean -> {
            if (aBoolean != null && aBoolean) {
                if (dialogQrCode != null && dialogQrCode.isShowing())
                    dialogQrCode.dismiss();
            }
        });

        fragmentRegisterViewModel.goToTwoStepVerificationPage.observe(getViewLifecycleOwner(), userId -> {
            if (getActivity() instanceof ActivityRegistration && userId != null) {
                if (dialogQrCode != null && dialogQrCode.isShowing()) {
                    dialogQrCode.dismiss();
                }
                ((ActivityRegistration) getActivity()).loadFragment(TwoStepVerificationFragment.newInstant(userId), true);
            }
        });

        fragmentRegisterViewModel.showDialogUserBlock.observe(getViewLifecycleOwner(), isShow -> {
            if (getActivity() != null && isShow != null && isShow) {
                new DefaultRoundDialog(getActivity()).setTitle(R.string.USER_VERIFY_BLOCKED_USER).setMessage(R.string.Toast_Number_Block).setPositiveButton(R.string.B_ok, null).show();
            }
        });

        fragmentRegisterViewModel.showError.observe(getViewLifecycleOwner(), messageRes -> {
            if (messageRes != null) {
                HelperError.showSnackMessage(getString(messageRes), false);
            }
        });
    }

    private void showCountryDialog() {
        if (getActivity() != null) {
            Dialog dialogChooseCountry = new Dialog(getActivity());
            dialogChooseCountry.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialogChooseCountry.setContentView(R.layout.rg_dialog);
            dialogChooseCountry.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            int setWidth = (int) (getResources().getDisplayMetrics().widthPixels * 0.9);
            int setHeight = (int) (getResources().getDisplayMetrics().heightPixels * 0.9);
            dialogChooseCountry.getWindow().setLayout(setWidth, setHeight);
            //
            final TextView txtTitle = dialogChooseCountry.findViewById(R.id.rg_txt_titleToolbar);
            SearchView edtSearchView = dialogChooseCountry.findViewById(R.id.rg_edtSearch_toolbar);

            txtTitle.setOnClickListener(view -> {
                edtSearchView.setIconified(false);
                edtSearchView.setIconifiedByDefault(true);
                txtTitle.setVisibility(View.GONE);
            });

            // close SearchView and show title again
            edtSearchView.setOnCloseListener(() -> {
                txtTitle.setVisibility(View.VISIBLE);
                return false;
            });

            final ListView listView = dialogChooseCountry.findViewById(R.id.lstContent);
            AdapterDialog adapterDialog = new AdapterDialog(getActivity(), fragmentRegisterViewModel.structCountryArrayList);
            listView.setAdapter(adapterDialog);
            listView.setOnItemClickListener((parent, view, position, id) -> {
                fragmentRegisterViewModel.setCountry(adapterDialog.getItem(position));
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

        if (getActivity() != null && getActivity().isFinishing()) {
            return;
        }

        MaterialDialog dialogWait = new MaterialDialog.Builder(getActivity()).title(data.getTitle()).customView(R.layout.dialog_remind_time, true)
                .positiveText(R.string.B_ok).autoDismiss(false).canceledOnTouchOutside(false).cancelable(false).onPositive((dialog, which) -> {
                    fragmentRegisterViewModel.timerFinished();
                    dialog.dismiss();
                }).show();

        View v = dialogWait.getCustomView();

        final TextView remindTime = v.findViewById(R.id.remindTime);
        CountDownTimer countWaitTimer = new CountDownTimer(data.getTime() * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long seconds = millisUntilFinished / 1000 % 60;
                long minutes = millisUntilFinished / (60 * 1000) % 60;
                long hour = millisUntilFinished / (3600 * 1000);

                remindTime.setText(String.format(Locale.getDefault(), "%02d:%02d:%02d", hour, minutes, seconds));
                dialogWait.getActionButton(DialogAction.POSITIVE).setEnabled(false);
            }

            @Override
            public void onFinish() {
                dialogWait.getActionButton(DialogAction.POSITIVE).setEnabled(true);
                remindTime.setText("00:00");
            }
        };
        countWaitTimer.start();
    }

    private void showDialogTermAndCondition() {
        if (getActivity() != null) {
            Dialog dialogTermsAndCondition = new Dialog(getActivity());
            dialogTermsAndCondition.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialogTermsAndCondition.setContentView(R.layout.terms_condition_dialog);
            AppCompatTextView termsText = dialogTermsAndCondition.findViewById(R.id.termAndConditionTextView);
            termsText.setText(fragmentRegisterViewModel.getAgreementDescription());
            dialogTermsAndCondition.findViewById(R.id.okButton).setOnClickListener(v -> dialogTermsAndCondition.dismiss());
            dialogTermsAndCondition.show();
        }
    }

    private void showDialogConditionError() {
        if (getContext() != null) {
            new DefaultRoundDialog(getContext())
                    .setTitle(R.string.warning)
                    .setMessage(R.string.accept_terms_and_condition_error_message)
                    // Specifying a listener allows you to take an action before dismissing the dialog.
                    // The dialog is automatically dismissed when a dialog button is clicked.
                    .setPositiveButton(R.string.dialog_ok, null)
                    .show();
        }
    }

    private void showQrCodeDialog(int expireTime) {
        if (getActivity() != null) {
            dialogQrCode = new MaterialDialog.Builder(getActivity()).title(getString(R.string.Login_with_QrCode)).customView(R.layout.dialog_qrcode, true)
                    .positiveText(R.string.share_item_dialog).onPositive((dialog, which) -> fragmentRegisterViewModel.shareQr())
                    .negativeText(R.string.save).onNegative((dialog, which) -> fragmentRegisterViewModel.saveQr())
                    .neutralText(R.string.cancel).onNeutral((dialog, which) -> dialog.dismiss()).build();

            AppCompatImageView imgQrCodeNewDevice = (AppCompatImageView) dialogQrCode.findViewById(R.id.imgQrCodeNewDevice);
            AppCompatTextView expireTimeTextView = (AppCompatTextView) dialogQrCode.findViewById(R.id.expireTime);

            int time = (expireTime - 100) * 1000;
            CountDownTimer CountDownTimerQrCode = new CountDownTimer(time, Config.COUNTER_TIMER_DELAY) { // wait for verify sms
                public void onTick(long millisUntilFinished) {
                    long seconds = millisUntilFinished / 1000 % 60;
                    long minutes = millisUntilFinished / (60 * 1000) % 60;
                    expireTimeTextView.setText(String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds));
                }

                public void onFinish() {
                    dialogQrCode.dismiss();
                    fragmentRegisterViewModel.onClickQrCode();
                }
            };

            CountDownTimerQrCode.start();


            if (!(getActivity()).isFinishing()) {
                dialogQrCode.show();
            }

            dialogQrCode.setOnDismissListener(dialog -> CountDownTimerQrCode.cancel());

            if (imgQrCodeNewDevice != null) {
                G.imageLoader.clearMemoryCache();
                G.imageLoader.displayImage(AndroidUtils.suitablePath(fragmentRegisterViewModel._resultQrCode), imgQrCodeNewDevice);
            }
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
