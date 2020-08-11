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

import android.app.ProgressDialog;
import android.content.Context;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.auth.api.phone.SmsRetrieverClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import net.iGap.G;
import net.iGap.R;
import net.iGap.helper.HelperString;
import net.iGap.helper.HelperToolbar;
import net.iGap.module.EditTextAdjustPan;
import net.iGap.module.SmsRetriver.SMSReceiver;
import net.iGap.observers.interfaces.ToolbarListener;

import org.jetbrains.annotations.NotNull;

public abstract class FragmentOtpAuthentication extends BaseFragment implements ToolbarListener {

    protected String regex = null;
    private String smsMessage = null;
    EditTextAdjustPan smsCodeEditText;

    private CountDownTimer countDownTimer;
    public static final String TAG = FragmentOtpAuthentication.class.getSimpleName();
    private SMSReceiver smsReceiver;
    private ProgressDialog dialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return attachToSwipeBack(inflater.inflate(R.layout.fragment_opt_authentication, container, false));
    }

    @Override
    public void onViewCreated(@NotNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        HelperToolbar toolbar = HelperToolbar.create()
                .setContext(getContext())
                .setLifecycleOwner(getViewLifecycleOwner())
                .setLogoShown(true)
                .setRightIcons(R.string.check_icon)
                .setLeftIcon(R.string.back_icon)
                .setDefaultTitle(requireArguments().getString("title", getString(R.string.unknown)))
                .setListener(this);

        ((ViewGroup) view.findViewById(R.id.toolbar)).addView(toolbar.getView());

        smsCodeEditText = view.findViewById(R.id.smsCodeEditText);

        ViewGroup ltTime = view.findViewById(R.id.stda_layout_time);
        TextView txtPhoneNumber = view.findViewById(R.id.stda_txt_phoneNumber);
        TextView descriptionTextView = view.findViewById(R.id.description_authentication);
        TextView txtTimerLand = view.findViewById(R.id.stda_txt_time);

        txtPhoneNumber.setText(requireArguments().getString("phone", getString(R.string.unknown)));
        descriptionTextView.setText(requireArguments().getString("description"));
        smsCodeEditText.setHint(requireArguments().getString("editTextHint"));

        countDownTimer = new CountDownTimer(1000 * 60, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                int seconds = (int) ((millisUntilFinished) / 1000);
                int minutes = seconds / 60;
                seconds = seconds % 60;
                txtTimerLand.setText("" + String.format("%02d", minutes) + ":" + String.format("%02d", seconds));
            }

            @Override
            public void onFinish() {
                ltTime.setVisibility(View.GONE);
            }
        };

        start();
    }

    private void start() {
        getAuthenticationToken();
        countDownTimer.start();
        startSMSListener();
    }


    private void startSMSListener() {
        try {
            smsReceiver = new SMSReceiver();
            smsReceiver.setOTPListener(new SMSReceiver.OTPReceiveListener() {
                @Override
                public void onOTPReceived(String message) {

                    try {
                        G.handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                smsMessage = message;
                                trySetCode();
                            }
                        }, 500);
                    } catch (Exception e1) {
                        e1.getStackTrace();
                    }

                    unregisterReceiver();

                }

                @Override
                public void onOTPTimeOut() {
                    Log.e(TAG, "OTP Time out");
                }

                @Override
                public void onOTPReceivedError(String error) {
                    Log.e(TAG, error);
                }
            });

            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(SmsRetriever.SMS_RETRIEVED_ACTION);
            requireActivity().registerReceiver(smsReceiver, intentFilter);

            SmsRetrieverClient client = SmsRetriever.getClient(requireActivity());

            Task<Void> task = client.startSmsRetriever();
            task.addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.e(TAG, "sms API successfully started   ");
                }
            });

            task.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e(TAG, "sms Fail to start API   ");
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void unregisterReceiver() {
        try {
            if (smsReceiver != null) {
                requireActivity().unregisterReceiver(smsReceiver);
                smsReceiver = null;
            }
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
    }

    void trySetCode() {
        if (smsMessage != null && regex != null) {
            countDownTimer.cancel();
            String verificationCode = HelperString.regexExtractValue(smsMessage, regex);
            if (verificationCode.length() > 0) {
                smsCodeEditText.setText(verificationCode);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver();
    }

    void showProgressBar() {
        if (getActivity() == null || getActivity().isFinishing() || (dialog != null && dialog.isShowing()))
            return;

        dialog = ProgressDialog.show(getActivity(), "", getString(R.string.please_wait), true);
    }

    void hideProgressBar() {
        if (getActivity() == null || getActivity().isFinishing() || dialog == null || !dialog.isShowing())
            return;

        dialog.dismiss();
    }

    void dialogWaitTime(int title, long time, int majorCode) {
        if (getActivity() == null || getActivity().isFinishing())
            return;

        MaterialDialog dialog = new MaterialDialog.Builder(getActivity())
                .title(title)
                .positiveText(R.string.B_ok)
                .negativeText(R.string.B_cancel)
                .customView(R.layout.dialog_remind_time, true)
                .canceledOnTouchOutside(false)
                .autoDismiss(false)
                .onNegative((dialog12, which) -> {
                    dialog12.dismiss();
                    removeFromBaseFragment(FragmentOtpAuthentication.this);

                })
                .onPositive((dialog12, which) -> {
                    dialog12.dismiss();
                    getAuthenticationToken();
                })
                .show();

        View v = dialog.getCustomView();

        final TextView remindTime = v.findViewById(R.id.remindTime);
        CountDownTimer countWaitTimer = new CountDownTimer(time * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                int seconds = (int) ((millisUntilFinished) / 1000);
                int minutes = seconds / 60;
                seconds = seconds % 60;
                remindTime.setText(String.format("%02d", minutes) + ":" + String.format("%02d", seconds));
                dialog.getActionButton(DialogAction.POSITIVE).setEnabled(false);
            }

            @Override
            public void onFinish() {
                dialog.getActionButton(DialogAction.POSITIVE).setEnabled(true);
                remindTime.setText("00:00");
            }
        };
        countWaitTimer.start();
    }

    @Override
    public void onLeftIconClickListener(View view) {
        InputMethodManager imm = (InputMethodManager) G.context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        removeFromBaseFragment(FragmentOtpAuthentication.this);
    }

    @Override
    public abstract void onRightIconClickListener(View view);

    protected abstract void getAuthenticationToken();

}
