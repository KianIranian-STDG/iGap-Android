package net.iGap.fragments;

import android.content.SharedPreferences;
import androidx.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.andrognito.patternlockview.PatternLockView;
import com.andrognito.patternlockview.utils.ResourceUtils;

import net.iGap.G;
import net.iGap.R;
import net.iGap.activities.ActivityMain;
import net.iGap.databinding.FragmentPassCodeBinding;
import net.iGap.helper.HelperToolbar;
import net.iGap.interfaces.ToolbarListener;
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


        final HelperToolbar toolbar = HelperToolbar.create()
                .setContext(getContext())
                .setDefaultTitle(G.context.getResources().getString(R.string.two_step_pass_code))
                .setLeftIcon(R.string.back_icon)
                .setRightIcons(R.string.check_icon)
                .setLogoShown(true)
                .setListener(new ToolbarListener() {
                    @Override
                    public void onLeftIconClickListener(View view) {
                        popBackStackFragment();

                        AppUtils.closeKeyboard(view);

                    }

                    @Override
                    public void onRightIconClickListener(View view) {
                        fragmentPassCodeViewModel.onClickRippleOk(view);
                    }
                });

        fragmentPassCodeBinding.fpcLayoutToolbar.addView(toolbar.getView());
        toolbar.getRightButton().setVisibility(View.GONE);

        //observe to show tick (ok) button or not
        fragmentPassCodeViewModel.rippleOkVisibility.observe(this , visibility -> {

            if (visibility != null){
                if (visibility == View.VISIBLE){
                    toolbar.getRightButton().setVisibility(View.VISIBLE);
                }else {
                    toolbar.getRightButton().setVisibility(View.GONE);
                }
            }
        });

        fragmentPassCodeViewModel.passCodeStateChangeListener.observe(getViewLifecycleOwner() , aBoolean -> {
            if (getActivity() != null){
                ((ActivityMain) getActivity()).updatePassCodeState();
            }
        });

        boolean isLinePattern;
        if (isPattern){
            SharedPreferences sharedPreferences = G.currentActivity.getSharedPreferences(SHP_SETTING.FILE_NAME, MODE_PRIVATE);
            isLinePattern = sharedPreferences.getBoolean(SHP_SETTING.KEY_PATTERN_TACTILE_DRAWN, true);
        }else {
            isLinePattern = true;
        }

        fragmentPassCodeBinding.patternLockView.setViewMode(PatternLockView.PatternViewMode.WRONG);       // Set the current viee more
        fragmentPassCodeBinding.patternLockView.setInStealthMode(!isLinePattern);                                     // Set the pattern in stealth mode (pattern drawing is hidden)
        fragmentPassCodeBinding.patternLockView.setTactileFeedbackEnabled(true);                            // Enables vibration feedback when the pattern is drawn
        fragmentPassCodeBinding.patternLockView.setInputEnabled(true);                                     // Disables any input from the pattern lock view completely

        fragmentPassCodeBinding.patternLockView.setDotCount(4);
        fragmentPassCodeBinding.patternLockView.setDotNormalSize((int) ResourceUtils.getDimensionInPx(G.fragmentActivity, R.dimen.dp22));
        fragmentPassCodeBinding.patternLockView.setDotSelectedSize((int) ResourceUtils.getDimensionInPx(G.fragmentActivity, R.dimen.dp32));
        fragmentPassCodeBinding.patternLockView.setPathWidth((int) ResourceUtils.getDimensionInPx(G.fragmentActivity, R.dimen.pattern_lock_path_width));
        fragmentPassCodeBinding.patternLockView.setAspectRatioEnabled(true);
        fragmentPassCodeBinding.patternLockView.setAspectRatio(PatternLockView.AspectRatio.ASPECT_RATIO_HEIGHT_BIAS);
        if (G.isDarkTheme) {
            fragmentPassCodeBinding.patternLockView.setNormalStateColor(ResourceUtils.getColor(G.fragmentActivity, R.color.white));
        }else {
            fragmentPassCodeBinding.patternLockView.setNormalStateColor(Color.parseColor(G.appBarColor));
        }
        fragmentPassCodeBinding.patternLockView.setCorrectStateColor(ResourceUtils.getColor(G.fragmentActivity, R.color.green));
        fragmentPassCodeBinding.patternLockView.setWrongStateColor(ResourceUtils.getColor(G.fragmentActivity, R.color.red));
        fragmentPassCodeBinding.patternLockView.setDotAnimationDuration(150);
        fragmentPassCodeBinding.patternLockView.setPathEndAnimationDuration(100);

    }

    private void initDataBinding() {
        fragmentPassCodeViewModel = new FragmentPassCodeViewModel(fragmentPassCodeBinding);
        isPattern = fragmentPassCodeViewModel.isPattern;
        fragmentPassCodeBinding.setFragmentPassCodeViewModel(fragmentPassCodeViewModel);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        fragmentPassCodeViewModel.onDestroy();
    }


}
