package net.iGap.fragments;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.afollestad.materialdialogs.MaterialDialog;

import net.iGap.G;
import net.iGap.R;
import net.iGap.databinding.FragmentFragmentSecurityBinding;
import net.iGap.helper.HelperFragment;
import net.iGap.helper.HelperToolbar;
import net.iGap.observers.interfaces.ToolbarListener;
import net.iGap.viewmodel.FragmentSecurityViewModel;

import org.jetbrains.annotations.NotNull;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentSecurity extends BaseFragment {

    public static boolean isFirstSetPassword = true;
    public static boolean isSetRecoveryEmail = false;
    public static OnPopBackStackFragment onPopBackStackFragment;
    public FragmentSecurityViewModel fragmentSecurityViewModel;
    public FragmentFragmentSecurityBinding fragmentSecurityBinding;
    private HelperToolbar mHelperToolbar;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fragmentSecurityViewModel = ViewModelProviders.of(this).get(FragmentSecurityViewModel.class);
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fragmentSecurityBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_fragment_security, container, false);
        fragmentSecurityBinding.setFragmentSecurityViewModel(fragmentSecurityViewModel);
        fragmentSecurityBinding.setLifecycleOwner(this);
        return attachToSwipeBack(fragmentSecurityBinding.getRoot());
    }

    @Override
    public void onViewCreated(@NotNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mHelperToolbar = HelperToolbar.create()
                .setContext(getContext())
                .setLifecycleOwner(getViewLifecycleOwner())
                .setDefaultTitle(G.context.getResources().getString(R.string.two_step_verification_title))
                .setLeftIcon(R.string.back_icon)
                .setRightIcons(R.string.check_icon)
                .setLogoShown(true)
                .setListener(new ToolbarListener() {
                    @Override
                    public void onLeftIconClickListener(View view) {
                        fragmentSecurityViewModel.onClickRippleBack(view);
                    }

                    @Override
                    public void onRightIconClickListener(View view) {
                        fragmentSecurityViewModel.onClickRippleOk(view);
                    }
                });

        fragmentSecurityBinding.ffsLayoutToolbar.addView(mHelperToolbar.getView());
        mHelperToolbar.getRightButton().setVisibility(View.GONE);

        fragmentSecurityViewModel.titleToolbar.observe(G.fragmentActivity, s -> mHelperToolbar.setDefaultTitle(s));

        fragmentSecurityViewModel.rippleOkVisibility.observe(G.fragmentActivity, visibility -> mHelperToolbar.getRightButton().setVisibility(visibility));

        fragmentSecurityViewModel.goToSetSecurityPassword.observe(getViewLifecycleOwner(), password -> {
            if (getActivity() != null && password != null) {
                FragmentSetSecurityPassword fragmentSetSecurityPassword = new FragmentSetSecurityPassword();
                Bundle bundle = new Bundle();
                bundle.putString("OLD_PASSWORD", password);
                fragmentSetSecurityPassword.setArguments(bundle);
                new HelperFragment(getActivity().getSupportFragmentManager(), new FragmentSetSecurityPassword()).load();
            }
        });

        fragmentSecurityViewModel.showForgetPasswordDialog.observe(getViewLifecycleOwner(), listRes -> {
            if (getActivity() != null && listRes != null) {
                new MaterialDialog.Builder(getActivity())
                        .title(R.string.set_recovery_dialog_title)
                        .items(listRes)
                        .itemsCallback((dialog, view1, which, text) -> fragmentSecurityViewModel.forgetPassword(text.equals(getString(R.string.recovery_by_email_dialog))))
                        .show();
            }
        });

        fragmentSecurityViewModel.goToSecurityRecoveryPage.observe(getViewLifecycleOwner(), data -> {
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
                new HelperFragment(getActivity().getSupportFragmentManager(), fragmentSecurityRecovery).load();
            }
        });

        onPopBackStackFragment = this::popBackStackFragment;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (getView() == null) {
            return;
        }
        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener((v, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                fragmentSecurityViewModel.rippleBack(v);
                return true;
            }
            return false;
        });
    }

    public interface OnPopBackStackFragment {
        void onBack();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        onPopBackStackFragment = null;
    }
}
