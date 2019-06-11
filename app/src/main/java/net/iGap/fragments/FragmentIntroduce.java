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
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import net.iGap.G;
import net.iGap.R;
import net.iGap.helper.HelperError;
import net.iGap.helper.HelperTracker;
import net.iGap.helper.ParallaxPageTransformer;
import net.iGap.module.CustomCircleImage;

import org.jetbrains.annotations.NotNull;

public class FragmentIntroduce extends BaseFragment {

    private CustomCircleImage circleButton;

    @Nullable
    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_introduce, container, false);
    }

    @Override
    public void onViewCreated(@NotNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        HelperTracker.sendTracker(HelperTracker.TRACKER_INSTALL_USER);

        circleButton = view.findViewById(R.id.int_circleButton_introduce);
        circleButton.circleButtonCount(3);

        Button btnStart = view.findViewById(R.id.int_btnStart);

        btnStart.setOnClickListener(view1 -> startRegistration());

        view.findViewById(R.id.changeLanguage).setOnClickListener(v -> {
            if (!isAdded() || G.fragmentActivity.isFinishing()) {
                return;
            }
            if (G.socketConnection) {
                FragmentLanguage fragment = new FragmentLanguage();
                Bundle bundle = new Bundle();
                bundle.putBoolean("canSwipeBack", false);
                fragment.setArguments(bundle);
                G.fragmentActivity.getSupportFragmentManager().beginTransaction().add(R.id.ar_layout_root, fragment).setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_exit_in_right, R.anim.slide_exit_out_left).commitAllowingStateLoss();
            } else {
                G.handler.post(() -> HelperError.showSnackMessage(G.fragmentActivity.getResources().getString(R.string.waiting_for_connection), false));
            }
        });

        ViewPager viewPager = view.findViewById(R.id.int_viewPager_introduce);
        viewPager.setPageTransformer(true, new ParallaxPageTransformer());
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(final int position, final float positionOffset, final int positionOffsetPixels) { //set animation for all page
                circleButton.percentScroll(positionOffset, position);
            }

            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        AdapterViewPager adapterViewPager = new AdapterViewPager();
        viewPager.setAdapter(adapterViewPager);
        adapterViewPager.notifyDataSetChanged();
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

        try {
            if (beforeState != G.isLandscape) {
                G.handler.post(() -> {
                    if (!isAdded() || G.fragmentActivity.isFinishing()) {
                        return;
                    }

                    G.fragmentActivity.getSupportFragmentManager().beginTransaction().remove(FragmentIntroduce.this).commitAllowingStateLoss();
                    FragmentIntroduce fragment = new FragmentIntroduce();
                    G.fragmentActivity.getSupportFragmentManager().beginTransaction().add(R.id.ar_layout_root, fragment).setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_exit_in_right, R.anim.slide_exit_out_left).commitAllowingStateLoss();
                });
            }
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
        super.onConfigurationChanged(newConfig);
    }

    private void startRegistration() {
        if (!isAdded() || G.fragmentActivity.isFinishing()) {
            return;
        }
        if (G.socketConnection) {
            FragmentRegister fragment = new FragmentRegister();
            G.fragmentActivity.getSupportFragmentManager().beginTransaction().add(R.id.ar_layout_root, fragment).setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_exit_in_right, R.anim.slide_exit_out_left).commitAllowingStateLoss();
            G.fragmentActivity.getSupportFragmentManager().beginTransaction().remove(FragmentIntroduce.this).commitAllowingStateLoss();
        } else {
            G.handler.post(() -> HelperError.showSnackMessage(G.fragmentActivity.getResources().getString(R.string.waiting_for_connection), false));
        }
    }

    public class AdapterViewPager extends PagerAdapter {

        AdapterViewPager() {
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public boolean isViewFromObject(@NotNull View view, @NotNull Object object) {
            return view.equals(object);
        }

        @NotNull
        @Override
        public Object instantiateItem(@NotNull ViewGroup container, int position) {
            View view = G.inflater.inflate(R.layout.view_pager_introduce_1, container, false);
            AppCompatImageView introImage = view.findViewById(R.id.introImage);
            introImage.setImageResource(getIntroImage(position));
            AppCompatTextView title = view.findViewById(R.id.introTitle);
            title.setText(G.fragmentActivity.getResources().getString(getTitle(position)));
            AppCompatTextView description = view.findViewById(R.id.introDescription);
            description.setText(G.fragmentActivity.getResources().getString(getDescription(position)));
            container.addView(view);
            view.setTag(position);
            return view;
        }

        @Override
        public void destroyItem(@NotNull ViewGroup container, int position, @NotNull Object object) {
            container.removeView((View) object);
        }

        private int getIntroImage(int position) {
            switch (position) {
                case 0:
                    return R.drawable.ic_int1;
                case 1:
                    return R.drawable.ic_int2;
                case 2:
                    return R.drawable.ic_int3;
                default:
                    return R.drawable.ic_int1;
            }
        }

        private int getTitle(int position) {
            switch (position) {
                case 0:
                    return R.string.text_line_1_introduce_page5;
                case 1:
                    return R.string.text_line_1_introduce_page7;
                case 2:
                    return R.string.text_line_1_introduce_page3;
                default:
                    return R.string.text_line_1_introduce_page5;
            }
        }

        private int getDescription(int position) {
            switch (position) {
                case 0:
                    return R.string.text_line_2_introduce_page5;
                case 1:
                    return R.string.text_line_2_introduce_page7;
                case 2:
                    return R.string.text_line_2_introduce_page3;
                default:
                    return R.string.text_line_2_introduce_page5;
            }
        }
    }
}
