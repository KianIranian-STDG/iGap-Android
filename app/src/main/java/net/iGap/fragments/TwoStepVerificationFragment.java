package net.iGap.fragments;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import net.iGap.R;
import net.iGap.activities.ActivityRegistration;
import net.iGap.databinding.FragmentTwoStepVerificationBinding;
import net.iGap.helper.HelperError;
import net.iGap.helper.HelperToolbar;
import net.iGap.helper.HelperTracker;
import net.iGap.observers.interfaces.ToolbarListener;
import net.iGap.viewmodel.TwoStepVerificationViewModel;

import java.util.Locale;

public class TwoStepVerificationFragment extends BaseFragment {

    private static String USER_ID = "userId";

    private FragmentTwoStepVerificationBinding binding;
    private TwoStepVerificationViewModel viewModel;

    public static TwoStepVerificationFragment newInstant(long userId) {
        TwoStepVerificationFragment fragment = new TwoStepVerificationFragment();
        Bundle bundle = new Bundle();
        bundle.putLong(USER_ID, userId);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(this).get(TwoStepVerificationViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_two_step_verification, container, false);
        binding.setViewModel(viewModel);
        binding.setLifecycleOwner(this);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        HelperTracker.sendTracker(HelperTracker.TRACKER_TWO_STEP);
        HelperToolbar toolbar = HelperToolbar.create()
                .setContext(getContext())
                .setLifecycleOwner(getViewLifecycleOwner())
                .setLeftIcon(R.string.empty_error_message)
                .setRightIcons(R.string.check_icon)
                .setLogoShown(true)
                .setShowConnectionState(false)
                .setDefaultTitle(getString(R.string.two_step_verification_title))
                .setListener(new ToolbarListener() {
                    @Override
                    public void onRightIconClickListener(View view) {
                        viewModel.onSubmitPasswordClick();
                    }
                });

        binding.toolbar.addView(toolbar.getView());

        viewModel.showErrorMessage.observe(getViewLifecycleOwner(), errorMessageRes -> {
            if (errorMessageRes != null) {
                HelperError.showSnackMessage(getString(errorMessageRes), true);
            }
        });

        viewModel.isHideKeyword.observe(getViewLifecycleOwner(), isHide -> {
            if (isHide != null) {
                if (isHide) {
                    hideKeyboard();
                } else {
                    openKeyBoard();
                }
            }
        });

        viewModel.showDialogWaitTime.observe(getViewLifecycleOwner(), time -> {
            if (getActivity() != null && time != null) {
                MaterialDialog dialogWait = new MaterialDialog.Builder(getActivity()).title(R.string.error_check_password).customView(R.layout.dialog_remind_time, true).positiveText(R.string.B_ok).autoDismiss(true).canceledOnTouchOutside(true).onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                    }
                }).show();

                View v = dialogWait.getCustomView();

                TextView remindTime = v.findViewById(R.id.remindTime);
                CountDownTimer countWaitTimer = new CountDownTimer(time * 1000, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        int seconds = (int) ((millisUntilFinished) / 1000);
                        int minutes = seconds / 60;
                        seconds = seconds % 60;
                        remindTime.setText(String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds));
                    }

                    @Override
                    public void onFinish() {
                        remindTime.setText("00:00");
                    }
                };
                countWaitTimer.start();
            }
        });

        viewModel.showDialogForgotPassword.observe(getViewLifecycleOwner(), listResId -> {
            if (getActivity() != null && listResId != null) {
                new MaterialDialog.Builder(getActivity()).title(R.string.set_recovery_dialog_title).items(listResId).itemsCallback((dialog, view1, which, text) -> {
                    viewModel.selectedRecoveryType(text.equals(getString(R.string.recovery_by_email_dialog)));
                }).show();
            }
        });

        viewModel.goToSecurityRecoveryPage.observe(getViewLifecycleOwner(), data -> {
            if (getActivity() instanceof ActivityRegistration && data != null) {
                FragmentSecurityRecovery fragmentSecurityRecovery = new FragmentSecurityRecovery();
                Bundle bundle = new Bundle();
                bundle.putSerializable("PAGE", data.getSecurity());
                bundle.putString("QUESTION_ONE", data.getQuestionOne());
                bundle.putString("QUESTION_TWO", data.getQuestionTwo());
                bundle.putString("PATERN_EMAIL", data.getEmailPattern());
                bundle.putBoolean("IS_EMAIL", data.isEmail());
                bundle.putBoolean("IS_CONFIRM_EMAIL", data.isConfirmEmail());
                fragmentSecurityRecovery.setArguments(bundle);
                ((ActivityRegistration) getActivity()).loadFragment(fragmentSecurityRecovery, true);
            }
        });
    }
}
