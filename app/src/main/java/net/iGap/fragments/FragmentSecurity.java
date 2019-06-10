package net.iGap.fragments;

import android.arch.lifecycle.Observer;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.afollestad.materialdialogs.MaterialDialog;

import net.iGap.G;
import net.iGap.R;
import net.iGap.databinding.FragmentFragmentSecurityBinding;
import net.iGap.helper.HelperFragment;
import net.iGap.helper.HelperToolbar;
import net.iGap.interfaces.ToolbarListener;
import net.iGap.viewmodel.FragmentSecurityViewModel;

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


    public FragmentSecurity() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        fragmentSecurityBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_fragment_security, container, false);
        return attachToSwipeBack(fragmentSecurityBinding.getRoot());
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initDataBinding();

        mHelperToolbar = HelperToolbar.create()
                .setContext(getContext())
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

        fragmentSecurityViewModel.titleToolbar.observe(G.fragmentActivity, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                mHelperToolbar.setDefaultTitle(s);
            }
        });

        fragmentSecurityViewModel.rippleOkVisibility.observe(G.fragmentActivity, new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer visibility) {
                mHelperToolbar.getRightButton().setVisibility(visibility);
            }
        });

        fragmentSecurityViewModel.goToSetSecurityPassword.observe(this, password -> {
            if (getActivity() != null && password != null) {
                FragmentSetSecurityPassword fragmentSetSecurityPassword = new FragmentSetSecurityPassword();
                Bundle bundle = new Bundle();
                bundle.putString("OLD_PASSWORD", password);
                fragmentSetSecurityPassword.setArguments(bundle);
                new HelperFragment(getActivity().getSupportFragmentManager(), new FragmentSetSecurityPassword()).load();
            }
        });

        fragmentSecurityViewModel.showForgetPasswordDialog.observe(this, listRes -> {
            if (getActivity() != null && listRes != null) {
                new MaterialDialog.Builder(getActivity()).title(R.string.set_recovery_dialog_title).items(listRes).itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        fragmentSecurityViewModel.forgetPassword(text.equals(getString(R.string.recovery_by_email_dialog)));
                    }
                }).show();
            }
        });

        fragmentSecurityViewModel.goToSecurityRecoveryPage.observe(this, data -> {
            if (getActivity() != null && data != null) {
                FragmentSecurityRecovery fragmentSecurityRecovery = new FragmentSecurityRecovery();
                Bundle bundle = new Bundle();
                bundle.putSerializable("PAGE", data.getSecurity());
                bundle.putString("QUESTION_ONE", data.getQuestionOne());
                bundle.putString("QUESTION_TWO", data.getQuestionTwo());
                bundle.putString("PATERN_EMAIL", data.getEmailPatern());
                bundle.putBoolean("IS_EMAIL", data.isEmail());
                bundle.putBoolean("IS_CONFIRM_EMAIL", data.isConfirmEmail());
                fragmentSecurityRecovery.setArguments(bundle);
                new HelperFragment(getActivity().getSupportFragmentManager(), fragmentSecurityRecovery).load();
            }
        });

        onPopBackStackFragment = new OnPopBackStackFragment() {
            @Override
            public void onBack() {
                popBackStackFragment();
            }
        };
    }

    private void initDataBinding() {
        fragmentSecurityViewModel = new FragmentSecurityViewModel();
        fragmentSecurityBinding.setFragmentSecurityViewModel(fragmentSecurityViewModel);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (getView() == null) {
            return;
        }
        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                    fragmentSecurityViewModel.rippleBack(v);
                    return true;
                }
                return false;
            }
        });
    }

    public interface OnPopBackStackFragment {
        void onBack();
    }
}
