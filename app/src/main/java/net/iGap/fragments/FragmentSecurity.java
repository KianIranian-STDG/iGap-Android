package net.iGap.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
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
import net.iGap.helper.LayoutCreator;
import net.iGap.messenger.ui.toolBar.BackDrawable;
import net.iGap.messenger.ui.toolBar.Toolbar;
import net.iGap.messenger.ui.toolBar.ToolbarItem;
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
    private Toolbar securityToolbar;
    private final int rippleOkTag = 1;
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

        securityToolbar = new Toolbar(getContext());
        securityToolbar.setBackIcon(new BackDrawable(false));
        securityToolbar.setTitle(getString(R.string.two_step_pass_code));
        ToolbarItem toolbarItem;
        toolbarItem = securityToolbar.addItem(rippleOkTag, R.string.icon_sent, Color.WHITE);
        securityToolbar.setListener(i -> {
            switch (i) {
                case -1:
                    fragmentSecurityViewModel.onClickRippleBack(view);
                    break;
                case rippleOkTag:
                    fragmentSecurityViewModel.onClickRippleOk(view);
                    break;
            }
        });
        fragmentSecurityBinding.ffsLayoutToolbar.addView(securityToolbar, LayoutCreator.createLinear(LayoutCreator.MATCH_PARENT, LayoutCreator.dp(56), Gravity.TOP));
        toolbarItem.setVisibility(View.GONE);

        fragmentSecurityViewModel.titleToolbar.observe(G.fragmentActivity, s -> securityToolbar.setTitle(s));

        fragmentSecurityViewModel.rippleOkVisibility.observe(G.fragmentActivity, visibility -> toolbarItem.setVisibility(visibility));

        fragmentSecurityViewModel.goToSetSecurityPassword.observe(getViewLifecycleOwner(), password -> {
            if (getActivity() != null && password != null) {
                FragmentSetSecurityPassword fragmentSetSecurityPassword = new FragmentSetSecurityPassword();
                Bundle bundle = new Bundle();
                bundle.putString("OLD_PASSWORD", password);
                fragmentSetSecurityPassword.setArguments(bundle);
                new HelperFragment(getActivity().getSupportFragmentManager(), fragmentSetSecurityPassword).load();
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
