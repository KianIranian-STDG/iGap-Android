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

import net.iGap.G;
import net.iGap.R;
import net.iGap.databinding.FragmentFragmentSecurityBinding;
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
