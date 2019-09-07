package net.iGap.fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import net.iGap.R;
import net.iGap.activities.ActivityEnhanced;
import net.iGap.databinding.FragmentDarkThemeBinding;
import net.iGap.viewmodel.FragmentDarkThemeViewModel;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentDarkTheme extends BaseFragment {

    public static FragmentDarkTheme newInstance() {
        return new FragmentDarkTheme();
    }


    private FragmentDarkThemeViewModel fragmentDarkThemeViewModel;
    private FragmentDarkThemeBinding fragmentDarkThemeBinding;


    public FragmentDarkTheme() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fragmentDarkThemeBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_dark_theme, container, false);
        return attachToSwipeBack(fragmentDarkThemeBinding.getRoot());
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initDataBinding();

        fragmentDarkThemeBinding.stnsRippleBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // mActivity.getSupportFragmentManager().popBackStack();
                popBackStackFragment();
            }
        });

        fragmentDarkThemeViewModel.resetApp.observe(this, isReset -> {
            if (getActivity() instanceof ActivityEnhanced && isReset != null && isReset) {
                ((ActivityEnhanced) getActivity()).onRefreshActivity(true, "");
            }
        });

    }

    private void initDataBinding() {
        fragmentDarkThemeViewModel = new FragmentDarkThemeViewModel();
        fragmentDarkThemeBinding.setFragmentDarkThemeViewModel(fragmentDarkThemeViewModel);

    }
}
