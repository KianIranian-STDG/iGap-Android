package net.iGap.fragments;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import net.iGap.R;
import net.iGap.activities.ActivityMain;
import net.iGap.databinding.FragmentTwoStepVerificationBinding;
import net.iGap.dialog.DefaultRoundDialog;
import net.iGap.helper.HelperError;
import net.iGap.helper.HelperFragment;
import net.iGap.helper.HelperToolbar;
import net.iGap.interfaces.ToolbarListener;
import net.iGap.viewmodel.TwoStepVerificationViewModel;

import java.util.Locale;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        viewModel = new TwoStepVerificationViewModel(getArguments().getLong(USER_ID));
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_two_step_verification, container, false);
        binding.setViewModel(viewModel);
        binding.setLifecycleOwner(getActivity());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        HelperToolbar toolbar = HelperToolbar.create()
                .setContext(getContext())
                .setLeftIcon(R.string.empty_error_message)
                .setRightIcons(R.string.check_icon)
                .setLogoShown(true)
                .setDefaultTitle(getString(R.string.two_step_verification_title))
                .setListener(new ToolbarListener() {
                    @Override
                    public void onRightIconClickListener(View view) {
                        viewModel.onSubmitPasswordClick();
                    }
                });

        binding.toolbar.addView(toolbar.getView());

        viewModel.showErrorMessage.observe(this, errorMessageRes -> {
            if (errorMessageRes != null) {
                HelperError.showSnackMessage(getString(errorMessageRes), true);
            }
        });

        viewModel.isHideKeyword.observe(this, isHide -> {
            if (isHide != null) {
                if (isHide) {
                    hideKeyboard();
                } else {
                    openKeyBoard();
                }
            }
        });

        //ToDo:
        if (getActivity() != null) {
            viewModel.goToMainPage.observe(getActivity(), go -> {
                if (getActivity() != null && go != null) {
                    if (go) {
                        goMainPage(getActivity());
                    } else {
                        new DefaultRoundDialog(getActivity()).setTitle(R.string.warning).setMessage(R.string.two_step_verification_disable).setPositiveButton(R.string.dialog_ok, (dialog, which) -> {
                            goMainPage(getActivity());
                        }).show();
                    }
                }
            });
        }

        viewModel.showDialogWaitTime.observe(this, time -> {
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

        viewModel.showDialogForgotPassword.observe(this, listResId -> {
            if (getActivity() != null && listResId != null) {
                new MaterialDialog.Builder(getActivity()).title(R.string.set_recovery_dialog_title).items(listResId).itemsCallback((dialog, view1, which, text) -> {
                    viewModel.selectedRecoveryType(text.equals(getString(R.string.recovery_by_email_dialog)));
                }).show();
            }
        });

        viewModel.goToSecurityRecoveryPage.observe(this, data -> {
            if (getActivity() != null && data != null) {
                FragmentSecurityRecovery fragmentSecurityRecovery = new FragmentSecurityRecovery();
                Bundle bundle = new Bundle();
                bundle.putSerializable("PAGE", data.getSecurity());
                bundle.putString("QUESTION_ONE", data.getQuestionOne());
                bundle.putString("QUESTION_TWO", data.getQuestionTwo());
                bundle.putString("PATERN_EMAIL", data.getEmailPattern());
                bundle.putBoolean("IS_EMAIL", data.isEmail());
                bundle.putBoolean("IS_CONFIRM_EMAIL", data.isConfirmEmail());
                fragmentSecurityRecovery.setArguments(bundle);

                new HelperFragment(getActivity().getSupportFragmentManager(), fragmentSecurityRecovery).setResourceContainer(R.id.ar_layout_root).setAddToBackStack(true).load(false);
            }
        });
    }

    private void goMainPage(FragmentActivity activity) {
        Intent intent = new Intent(getActivity(), ActivityMain.class);
        intent.putExtra(FragmentRegistrationNickname.ARG_USER_ID, viewModel.userId);
        intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
        activity.finish();
    }
}
