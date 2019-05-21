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

import android.app.Dialog;
import android.arch.lifecycle.Observer;
import android.content.Context;
import android.content.DialogInterface;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.AppCompatTextView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;

import com.vicmikhailau.maskededittext.MaskedEditText;

import net.iGap.G;
import net.iGap.R;
import net.iGap.databinding.ActivityRegisterBinding;
import net.iGap.dialog.DefaultRoundDialog;
import net.iGap.viewmodel.FragmentRegisterViewModel;

import org.jetbrains.annotations.NotNull;

public class FragmentRegister extends BaseFragment {

    public static final String TAG = FragmentRegister.class.getSimpleName();
    private static final String KEY_SAVE_CODENUMBER = "SAVE_CODENUMBER";
    private static final String KEY_SAVE_PHONENUMBER_MASK = "SAVE_PHONENUMBER_MASK";
    private static final String KEY_SAVE_PHONENUMBER_NUMBER = "SAVE_PHONENUMBER_NUMBER";
    private static final String KEY_SAVE_NAMECOUNTRY = "SAVE_NAMECOUNTRY";
    private static final String KEY_SAVE_REGEX = "KEY_SAVE_REGEX";
    private static final String KEY_SAVE_AGREEMENT = "KEY_SAVE_REGISTER";
    public static EditText edtCodeNumber;
    public static MaskedEditText edtPhoneNumber;
    public static TextView btnOk;
    public static int positionRadioButton = -1;
    private TextView txtAgreement_register;
    private FragmentActivity mActivity;
    /*private LinearLayout headerLayout;*/
    private FragmentRegisterViewModel fragmentRegisterViewModel;
    private ActivityRegisterBinding fragmentRegisterBinding;

    @Nullable
    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        fragmentRegisterBinding = DataBindingUtil.inflate(inflater, R.layout.activity_register, container, false);
        return fragmentRegisterBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NotNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initBindView();

        /*TextView txtTitleToolbar = fragmentRegisterBinding.rgTxtTitleToolbar;
        Typeface titleTypeface;
        if (!HelperCalander.isPersianUnicode) {
            titleTypeface = G.typeface_neuropolitical;
        } else {
            titleTypeface = G.typeface_IRANSansMobile;
        }

        txtTitleToolbar.setTypeface(titleTypeface);*/

        edtCodeNumber = fragmentRegisterBinding.countyCode;
        edtPhoneNumber = fragmentRegisterBinding.phoneNumber;

        String t = String.format(getString(R.string.terms_and_condition),getString(R.string.terms_and_condition_clickable));
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

        fragmentRegisterViewModel.saveInstance(savedInstanceState, getArguments());

        fragmentRegisterViewModel.showConditionErrorDialog.observe(this, aBoolean -> {
            if (aBoolean) {
                showDialogConditionError();
            }
        });

        fragmentRegisterViewModel.goNextStep.observe(this, aBoolean -> {
            if (aBoolean != null && aBoolean) {
                FragmentActivation fragment = new FragmentActivation();
                Bundle bundle = new Bundle();
                bundle.putString("userName", fragmentRegisterViewModel.userName);
                bundle.putLong("userId", fragmentRegisterViewModel.userId);
                bundle.putString("authorHash", fragmentRegisterViewModel.authorHash);
                bundle.putString("phoneNumber", fragmentRegisterViewModel.phoneNumber);
                fragment.setArguments(bundle);
                G.fragmentActivity.getSupportFragmentManager().beginTransaction().add(R.id.ar_layout_root, fragment).setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_exit_in_right, R.anim.slide_exit_out_left).commitAllowingStateLoss();
                G.fragmentActivity.getSupportFragmentManager().beginTransaction().remove(FragmentRegister.this).commitAllowingStateLoss();
            }
        });

        fragmentRegisterViewModel.saveInstance(savedInstanceState, getArguments());
    }

    private void showDialogTermAndCondition() {
        Dialog dialogTermsAndCondition = new Dialog(G.fragmentActivity);
        dialogTermsAndCondition.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogTermsAndCondition.setContentView(R.layout.terms_condition_dialog);
        dialogTermsAndCondition.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        AppCompatTextView termsText = dialogTermsAndCondition.findViewById(R.id.termAndConditionTextView);
        termsText.setText(fragmentRegisterViewModel.callbackTxtAgreement.get());
        dialogTermsAndCondition.findViewById(R.id.okButton).setOnClickListener(v -> dialogTermsAndCondition.dismiss());
        dialogTermsAndCondition.show();
    }

    private void showDialogConditionError() {
        new DefaultRoundDialog(getContext())
                .setTitle(R.string.warning)
                .setMessage(R.string.accept_terms_and_condition_error_message)
                // Specifying a listener allows you to take an action before dismissing the dialog.
                // The dialog is automatically dismissed when a dialog button is clicked.
                .setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .show();
    }

    private void initBindView() {
        fragmentRegisterViewModel = new FragmentRegisterViewModel(this, fragmentRegisterBinding.getRoot(), mActivity);
        fragmentRegisterBinding.setFragmentRegisterViewModel(fragmentRegisterViewModel);
        fragmentRegisterBinding.setLifecycleOwner(this);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save the user's current game state
        savedInstanceState.putString(KEY_SAVE_CODENUMBER, edtCodeNumber.getText().toString());
        savedInstanceState.putString(KEY_SAVE_PHONENUMBER_MASK, edtPhoneNumber.getMask());
        savedInstanceState.putString(KEY_SAVE_PHONENUMBER_NUMBER, edtPhoneNumber.getEditableText().toString());
        savedInstanceState.putString(KEY_SAVE_NAMECOUNTRY, fragmentRegisterBinding.country.getText().toString());
        savedInstanceState.putString(KEY_SAVE_REGEX, fragmentRegisterViewModel.regex);
        savedInstanceState.putString(KEY_SAVE_AGREEMENT, txtAgreement_register.getText().toString());
        savedInstanceState.putInt(FragmentRegisterViewModel.KEY_SAVE, FragmentRegisterViewModel.ONETIME);

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {

        boolean beforeState = G.isLandscape;

        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            G.isLandscape = true;
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            G.isLandscape = false;
        }

        /*if (G.isLandscape && fragmentRegisterViewModel.isVerify) {

            ViewTreeObserver observer = headerLayout.getViewTreeObserver();
            observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

                @Override
                public void onGlobalLayout() {
                    headerLayoutHeight = headerLayout.getHeight();
                    scrollView.scrollTo(0, headerLayoutHeight);
                    headerLayout.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }
            });
        }*/

        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (FragmentActivity) context;
    }

    @Override
    public void onStop() {
        super.onStop();
        fragmentRegisterViewModel.onStop();
        super.onStop();
    }

    public enum Reason {
        SOCKET, TIME_OUT, INVALID_CODE
    }

    public interface OnStartAnimationRegister {
        void start();
    }
}
