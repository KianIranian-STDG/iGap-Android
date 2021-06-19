package net.iGap.fragments;

import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.transition.ChangeBounds;
import androidx.transition.TransitionManager;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.auth.api.phone.SmsRetrieverClient;
import com.google.android.gms.tasks.Task;

import net.iGap.R;
import net.iGap.activities.ActivityRegistration;
import net.iGap.databinding.FragmentActivationBinding;
import net.iGap.helper.HelperError;
import net.iGap.module.SmsRetriver.SMSReceiver;
import net.iGap.viewmodel.FragmentActivationViewModel;
import net.iGap.viewmodel.WaitTimeModel;

import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public class FragmentActivation extends BaseFragment{

    private final static String TAG = FragmentActivation.class.getName();

    private FragmentActivationBinding binding;
    private FragmentActivationViewModel viewModel;
    private SMSReceiver smsReceiver;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(this).get(FragmentActivationViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_activation, container, false);
        binding.setViewModel(viewModel);
        binding.setLifecycleOwner(getViewLifecycleOwner());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        startSMSListener();

        binding.timerView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                // Ensure you call it only once
                binding.timerView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                // Here you can get the size :)
                ConstraintSet set = new ConstraintSet();
                set.clone(binding.root);
                set.constrainCircle(binding.timerPosition.getId(), binding.timerView.getId(), binding.timerView.getWidth() / 2, 0);
                set.constrainCircle(binding.timerTextView.getId(), binding.timerPosition.getId(), (int) getResources().getDimension(R.dimen.dp20), 0);
                set.applyTo(binding.root);
            }
        });

        viewModel.verifyCode.observe(getViewLifecycleOwner(), s -> {
            if (s != null) {
                setActivationCode(s);
            }
        });
        viewModel.showEnteredCodeError.observe(getViewLifecycleOwner(), aBoolean -> {
            if (aBoolean != null && getContext() != null && aBoolean) {
                showMessageDialog(R.string.error, R.string.Toast_Enter_Code);
            }
        });
        viewModel.currentTimePosition.observe(getViewLifecycleOwner(), integer -> {
            if (integer != null) {
                ConstraintSet set1 = new ConstraintSet();
                set1.clone(binding.root);
                set1.constrainCircle(binding.timerPosition.getId(), binding.timerView.getId(), binding.timerView.getWidth() / 2, -integer);
                set1.constrainCircle(binding.timerTextView.getId(), binding.timerPosition.getId(), (int) getResources().getDimension(R.dimen.dp20), -integer);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    TransitionManager.beginDelayedTransition(binding.root, new ChangeBounds());
                    set1.applyTo(binding.root);
                } else {
                    set1.applyTo(binding.root);
                }
            }
        });
        viewModel.showWaitDialog.observe(getViewLifecycleOwner(), waitTimeModel -> {
            if (waitTimeModel != null) {
                dialogWaitTime(waitTimeModel);
            }
        });
        viewModel.closeKeyword.observe(getViewLifecycleOwner(), aBoolean -> {
            if (aBoolean != null && aBoolean) {
                hideKeyboard();
            }
        });
        viewModel.showEnteredCodeErrorServer.observe(getViewLifecycleOwner(), integer -> {
            if (integer != null && integer > 0 && getContext() != null) {
                showMessageDialog(R.string.error, integer);
            }
        });
        viewModel.clearActivationCode.observe(getViewLifecycleOwner(), aBoolean -> {
            if (aBoolean != null && aBoolean) {
                clearActivationCode();
            }
        });

        viewModel.goToTwoStepVerificationPage.observe(getViewLifecycleOwner(), userId -> {
            if (getActivity() instanceof ActivityRegistration && userId != null) {
                ((ActivityRegistration) getActivity()).loadFragment(TwoStepVerificationFragment.newInstant(userId), true);
            }
        });

        viewModel.showDialogUserBlocked.observe(getViewLifecycleOwner(), isShow -> {
            if (getActivity() != null && isShow != null && isShow) {
                showMessageDialog(R.string.USER_VERIFY_BLOCKED_USER, R.string.Toast_Number_Block);
            }
        });

        viewModel.showDialogVerificationCodeExpired.observe(getViewLifecycleOwner(), isShow -> {
            if (getActivity() != null && isShow != null && isShow) {
                showMessageDialog(R.string.USER_VERIFY_EXPIRED, R.string.Toast_Number_Block);
            }
        });

        viewModel.showConnectionError.observe(getViewLifecycleOwner(), isShow -> {
            if (isShow != null && isShow) {
                HelperError.showSnackMessage(getString(R.string.connection_error), false);
            }
        });

        initialActivationCodeEditor();
    }

    private void showMessageDialog(int title, int msg) {
        if (getActivity() == null) return;
        new MaterialDialog.Builder(getActivity())
                .title(title)
                .content(msg)
                .positiveText(R.string.ok)
                .show();
    }

    @Override
    public void onStop() {
        super.onStop();
        unregisterReceiver();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getActivity() != null) {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(SmsRetriever.SMS_RETRIEVED_ACTION);
            getActivity().registerReceiver(smsReceiver, intentFilter);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        Log.wtf(this.getClass().getName(), "onConfigurationChanged");
        super.onConfigurationChanged(newConfig);
    }

    private void startSMSListener() {
        try {
            smsReceiver = new SMSReceiver();
            smsReceiver.setOTPListener(new SMSReceiver.OTPReceiveListener() {
                @Override
                public void onOTPReceived(String message) {
                    try {
                        if (message != null && message.length() > 0) {
                            setActivationCode(viewModel.receiveVerifySms(message));
                        }
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

            SmsRetrieverClient client = SmsRetriever.getClient(getActivity());
            Task<Void> task = client.startSmsRetriever();
            task.addOnSuccessListener(aVoid -> Log.e(TAG, "sms API successfully started   "));
            task.addOnFailureListener(e -> { Log.e(TAG, "sms Fail to start API   ");});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void unregisterReceiver() {
        if (smsReceiver != null && getActivity() != null) {
            getActivity().unregisterReceiver(smsReceiver);
            smsReceiver = null;
        }
    }

    private void setActivationCode(@NotNull String code) {
        if (code.length() == 5) {
            binding.activationCodeEditText1.setText(String.valueOf(code.charAt(0)));
            binding.activationCodeEditText2.setText(String.valueOf(code.charAt(1)));
            binding.activationCodeEditText3.setText(String.valueOf(code.charAt(2)));
            binding.activationCodeEditText4.setText(String.valueOf(code.charAt(3)));
            binding.activationCodeEditText5.setText(String.valueOf(code.charAt(4)));
        }
    }

    private void initialActivationCodeEditor() {
        binding.activationCodeEditText1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 1) {
                    binding.activationCodeEditText2.requestFocus();
                }
            }
        });

        binding.activationCodeEditText2.setOnKeyListener((v, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_DEL) {
                if (((AppCompatEditText) v).getEditableText().length() == 0) {
                    binding.activationCodeEditText1.requestFocus();
                    return true;
                }
            }
            return false;
        });
        binding.activationCodeEditText2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 1) {
                    binding.activationCodeEditText3.requestFocus();
                } else {
                    binding.activationCodeEditText1.requestFocus();
                }
            }
        });

        binding.activationCodeEditText3.setOnKeyListener((v, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_DEL) {
                if (((AppCompatEditText) v).getEditableText().length() == 0) {
                    binding.activationCodeEditText2.requestFocus();
                    return true;
                }
            }
            return false;
        });
        binding.activationCodeEditText3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 1) {
                    binding.activationCodeEditText4.requestFocus();
                } else {
                    binding.activationCodeEditText2.requestFocus();
                }
            }
        });

        binding.activationCodeEditText4.setOnKeyListener((v, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_DEL) {
                if (((AppCompatEditText) v).getEditableText().length() == 0) {
                    binding.activationCodeEditText3.requestFocus();
                    return true;
                }
            }
            return false;
        });
        binding.activationCodeEditText4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 1) {
                    binding.activationCodeEditText5.requestFocus();
                } else {
                    binding.activationCodeEditText3.requestFocus();
                }
            }
        });
        binding.activationCodeEditText5.setOnKeyListener((v, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_DEL) {
                if (((AppCompatEditText) v).getEditableText().length() == 0) {
                    binding.activationCodeEditText4.requestFocus();
                    return true;
                }
            }
            return false;
        });
        binding.activationCodeEditText5.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 1) {
                    String message = binding.activationCodeEditText1.getEditableText().toString() +
                            binding.activationCodeEditText2.getEditableText().toString() +
                            binding.activationCodeEditText3.getEditableText().toString() +
                            binding.activationCodeEditText4.getEditableText().toString() +
                            binding.activationCodeEditText5.getEditableText().toString();
                    viewModel.loginButtonOnClick(message);
                } else {
                    binding.activationCodeEditText4.requestFocus();
                }
            }
        });
    }

    private void dialogWaitTime(WaitTimeModel data) {

        if (getActivity() != null && !getActivity().isFinishing()) {
            MaterialDialog dialogWait = new MaterialDialog.Builder(getActivity()).title(data.getTitle()).customView(R.layout.dialog_remind_time, true).positiveText(R.string.B_ok).autoDismiss(false).canceledOnTouchOutside(false).cancelable(false).onPositive(new MaterialDialog.SingleButtonCallback() {
                @Override
                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                    viewModel.timerFinished();
                    dialog.dismiss();
                }
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
    }

    private void clearActivationCode() {
        binding.activationCodeEditText1.setText("");
        binding.activationCodeEditText2.setText("");
        binding.activationCodeEditText3.setText("");
        binding.activationCodeEditText4.setText("");
        binding.activationCodeEditText5.setText("");
        binding.activationCodeEditText1.requestFocus();
    }
}
