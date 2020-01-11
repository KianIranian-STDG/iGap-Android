/*
 * This is the source code of iGap for Android
 * It is licensed under GNU AGPL v3.0
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright © 2017 , iGap - www.iGap.net
 * iGap Messenger | Free, Fast and Secure instant messaging application
 * The idea of the Kianiranian Company - www.kianiranian.com
 * All rights reserved.
 */

package net.iGap.fragments;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.ViewPager;

import net.iGap.G;
import net.iGap.R;
import net.iGap.activities.ActivityRegistration;
import net.iGap.adapter.IntroduceViewPagerAdapter;
import net.iGap.databinding.FragmentIntroduceBinding;
import net.iGap.helper.HelperError;
import net.iGap.helper.HelperTracker;
import net.iGap.helper.ParallaxPageTransformer;
import net.iGap.viewmodel.IntroductionViewModel;

import org.jetbrains.annotations.NotNull;

public class FragmentIntroduce extends BaseFragment {

    private final int INTRODUCE_SLIDE_COUNT = 4;

    private FragmentIntroduceBinding binding;
    private IntroductionViewModel viewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(this).get(IntroductionViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_introduce, container, false);
        binding.setLifecycleOwner(this);
        binding.setViewModel(viewModel);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NotNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        HelperTracker.sendTracker(HelperTracker.TRACKER_INSTALL_USER);

        viewModel.getShowErrorMessage().observe(getViewLifecycleOwner(), errorMessageId -> {
            if (errorMessageId != null) {
                HelperError.showSnackMessage(getString(errorMessageId), false);
            }
        });

        viewModel.getGoToRegistrationPage().observe(getViewLifecycleOwner(), isGo -> {
            if (getActivity() instanceof ActivityRegistration && isGo != null && isGo) {
                ((ActivityRegistration) getActivity()).loadFragment(new FragmentRegister(), true);
            }
        });

        viewModel.getGoToChangeLanguagePage().observe(getViewLifecycleOwner(), canSwipe -> {
            if (getActivity() instanceof ActivityRegistration && canSwipe != null) {
                ((ActivityRegistration) getActivity()).loadFragment(new FragmentLanguage(), true);
            }
        });

        binding.viewPagerIndicator.circleButtonCount(INTRODUCE_SLIDE_COUNT);

        ViewPager viewPager = view.findViewById(R.id.int_viewPager_introduce);
        viewPager.setPageTransformer(true, new ParallaxPageTransformer());
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(final int position, final float positionOffset, final int positionOffsetPixels) { //set animation for all page
                binding.viewPagerIndicator.percentScroll(positionOffset, position);
            }

            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        viewPager.setAdapter(new IntroduceViewPagerAdapter(INTRODUCE_SLIDE_COUNT));
        if (viewPager.getAdapter() != null) {
            viewPager.getAdapter().notifyDataSetChanged();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        boolean beforeState = G.isLandscape;
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            G.isLandscape = true;
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            G.isLandscape = false;
        }

        G.firstEnter = true;

        /*try {
            if (beforeState != G.isLandscape) {
                if (getActivity() != null) {
                    getActivity().getSupportFragmentManager().beginTransaction().remove(FragmentIntroduce.this).commitAllowingStateLoss();
                    FragmentIntroduce fragment = new FragmentIntroduce();
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .add(R.id.ar_layout_root, fragment)
                            .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_exit_in_right, R.anim.slide_exit_out_left)
                            .commitAllowingStateLoss();
                }
            }
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }*/
        super.onConfigurationChanged(newConfig);
    }
}
