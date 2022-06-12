package net.iGap.fragments;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.andrognito.patternlockview.PatternLockView;

import net.iGap.G;
import net.iGap.R;
import net.iGap.activities.ActivityMain;
import net.iGap.databinding.FragmentPassCodeBinding;
import net.iGap.helper.LayoutCreator;
import net.iGap.messenger.ui.toolBar.BackDrawable;
import net.iGap.messenger.ui.toolBar.Toolbar;
import net.iGap.messenger.ui.toolBar.ToolbarItem;
import net.iGap.module.AppUtils;
import net.iGap.module.SHP_SETTING;
import net.iGap.viewmodel.FragmentPassCodeViewModel;

import static android.content.Context.MODE_PRIVATE;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentPassCode extends BaseFragment {

    private FragmentPassCodeViewModel fragmentPassCodeViewModel;
    private FragmentPassCodeBinding fragmentPassCodeBinding;
    private boolean isPattern;
    private Toolbar passCodeToolbar;
    private final int rippleOkTag = 1;



    public FragmentPassCode() {
        // Required empty public constructor
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        fragmentPassCodeBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_pass_code, container, false);
        return attachToSwipeBack(fragmentPassCodeBinding.getRoot());
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initDataBinding();

        passCodeToolbar = new Toolbar(getContext());
        passCodeToolbar.setBackIcon(new BackDrawable(false));
        passCodeToolbar.setTitle(getString(R.string.two_step_pass_code));
        ToolbarItem toolbarItem;
        toolbarItem = passCodeToolbar.addItem(rippleOkTag, R.string.icon_sent, Color.WHITE);
        passCodeToolbar.setListener(i -> {
            switch (i) {
                case -1:
                    popBackStackFragment();
                    AppUtils.closeKeyboard(view);
                    break;
                case rippleOkTag:
                    fragmentPassCodeViewModel.onClickRippleOk(view);
                    break;
            }
        });
        fragmentPassCodeBinding.fpcLayoutToolbar.addView(passCodeToolbar, LayoutCreator.createLinear(LayoutCreator.MATCH_PARENT, LayoutCreator.dp(56), Gravity.TOP));
        toolbarItem.setVisibility(View.GONE);

        //observe to show tick (ok) button or not
        fragmentPassCodeViewModel.rippleOkVisibility.observe(this, visibility -> {

            if (visibility != null) {
                if (visibility == View.VISIBLE) {
                    toolbarItem.setVisibility(View.VISIBLE);
                } else {
                    toolbarItem.setVisibility(View.GONE);
                }
            }
        });

        fragmentPassCodeViewModel.passCodeStateChangeListener.observe(getViewLifecycleOwner(), aBoolean -> {
            if (getActivity() != null) {
                ((ActivityMain) getActivity()).updatePassCodeState();
            }
        });

        boolean isLinePattern;
        if (isPattern) {
            SharedPreferences sharedPreferences = G.currentActivity.getSharedPreferences(SHP_SETTING.FILE_NAME, MODE_PRIVATE);
            isLinePattern = sharedPreferences.getBoolean(SHP_SETTING.KEY_PATTERN_TACTILE_DRAWN, true);
        } else {
            isLinePattern = true;
        }

        fragmentPassCodeBinding.patternLockView.setViewMode(PatternLockView.PatternViewMode.WRONG);       // Set the current viee more
        fragmentPassCodeBinding.patternLockView.setInStealthMode(!isLinePattern);                                     // Set the pattern in stealth mode (pattern drawing is hidden)
        fragmentPassCodeBinding.patternLockView.setTactileFeedbackEnabled(true);                            // Enables vibration feedback when the pattern is drawn
        fragmentPassCodeBinding.patternLockView.setInputEnabled(true);                                     // Disables any input from the pattern lock view completely

    }

    private void initDataBinding() {
        fragmentPassCodeViewModel = new FragmentPassCodeViewModel(this, fragmentPassCodeBinding);
        isPattern = fragmentPassCodeViewModel.isPattern;
        fragmentPassCodeBinding.setFragmentPassCodeViewModel(fragmentPassCodeViewModel);
    }

}
