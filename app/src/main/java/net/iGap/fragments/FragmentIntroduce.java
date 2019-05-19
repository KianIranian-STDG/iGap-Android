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

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import net.iGap.G;
import net.iGap.R;
import net.iGap.helper.HelperError;
import net.iGap.module.CustomCircleImage;

import org.jetbrains.annotations.NotNull;

public class FragmentIntroduce extends BaseFragment {

    private ViewPager viewPager;
    private CustomCircleImage circleButton;
    private boolean isOne0 = true;
    private boolean isOne1 = true;
    private boolean isOne6 = true;
    private ImageView logoIgap, logoSecurity, boy;
    private TextView txt_p1_l2;
    private TextView txt_p1_l3;
    private TextView txt_p2_l1;
    private TextView txt_p2_l2;
    private TextView txt_p6_l1;
    private TextView txt_p6_l2;
    private Button btnStart;

    @Nullable
    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_introduce, container, false);
    }

    @Override
    public void onViewCreated(@NotNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        goToProgram(view);
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

    private void goToProgram(View view) {

        viewPager = view.findViewById(R.id.int_viewPager_introduce);

        circleButton = view.findViewById(R.id.int_circleButton_introduce);
        if (circleButton != null) {
            circleButton.circleButtonCount(3);
        }

        logoIgap = view.findViewById(R.id.int_img_logo_introduce);

        txt_p1_l2 = view.findViewById(R.id.int_txt_p1_l2);
        txt_p1_l3 = view.findViewById(R.id.int_txt_p1_l3);
        txt_p1_l3.setText(R.string.text_line_2_introduce_page5);

        txt_p1_l2.setText(R.string.text_line_1_introduce_page5);

        logoSecurity = view.findViewById(R.id.int_img_security_introduce);
        txt_p2_l1 = view.findViewById(R.id.int_txt_p2_l1);
        txt_p2_l2 = view.findViewById(R.id.int_txt_p2_l2);

        txt_p2_l1.setText(R.string.text_line_1_introduce_page7);
        txt_p2_l2.setText(R.string.text_line_2_introduce_page7);


        boy = view.findViewById(R.id.int_img_boy_introduce);
        txt_p6_l1 = view.findViewById(R.id.int_txt_p6_l1);
        txt_p6_l2 = view.findViewById(R.id.int_txt_p6_l2);
        txt_p6_l2.setText(R.string.text_line_1_introduce_page3);
        txt_p6_l2.setText(R.string.text_line_2_introduce_page3);

        btnStart = view.findViewById(R.id.int_btnStart);

        btnStart.setOnClickListener(view1 -> startRegistration());

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(final int position, final float positionOffset, final int positionOffsetPixels) { //set animation for all page

                circleButton.percentScroll(positionOffset, position);

                switch (position) {

                    case 0://Igap 1
//                        txtSkip.bringToFront();
                        if (positionOffset == 0) {

                            isOne1 = true;
                            isOne6 = true;

                            if (logoSecurity.getVisibility() == View.VISIBLE) {
                                animationOut(logoSecurity, txt_p2_l1, txt_p2_l2);
                            }

                            if (isOne0) {
                                animationInPage1(logoIgap, txt_p1_l2, txt_p1_l3);
                                isOne0 = false;
                            }
                        }

                        break;

                    case 1://Security 2
//                        txtSkip.bringToFront();
                        if (positionOffset == 0) {
                            isOne0 = true;
                            isOne6 = true;

                            if (logoIgap.getVisibility() == View.VISIBLE) {

                                animationOutPage1(logoIgap, txt_p1_l2, txt_p1_l3);
                            }
                            if (boy.getVisibility() == View.VISIBLE) {
                                animationOut(boy, txt_p6_l1, txt_p6_l2);
                            }

                            if (isOne1) {

                                animationIn(logoSecurity, txt_p2_l1, txt_p2_l2);
                                isOne1 = false;
                            }
                        }
                        break;

                    case 2://transfer1 6
//                        txtSkip.bringToFront();
                        btnStart.bringToFront();
                        btnStart.getParent().requestLayout();

                        if (positionOffset == 0) {
                            isOne0 = true;
                            isOne1 = true;

                            if (logoSecurity.getVisibility() == View.VISIBLE) {

                                animationOut(logoSecurity, txt_p2_l1, txt_p2_l2);
                            }
                            if (isOne6) {
                                animationInBoy(boy, txt_p6_l1, txt_p6_l2);
                                isOne6 = false;
                            }
                        }
                        break;
                }
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

    private void animationInPage1(final ImageView logo, final TextView txt2, final TextView txt3) {

        ObjectAnimator scaleX = ObjectAnimator.ofFloat(logo, "scaleX", 0, 1);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(logo, "scaleY", 0, 1);
        ObjectAnimator fade = ObjectAnimator.ofFloat(logo, "alpha", 0, 1);

        ObjectAnimator txt_fade2 = ObjectAnimator.ofFloat(txt2, "alpha", 0, 1);
        ObjectAnimator txt_fade3 = ObjectAnimator.ofFloat(txt3, "alpha", 0, 1);

        ObjectAnimator txt_scaleX2 = ObjectAnimator.ofFloat(txt2, "scaleX", 0, 1);
        ObjectAnimator txt_scaleX3 = ObjectAnimator.ofFloat(txt3, "scaleX", 0, 1);
        ObjectAnimator txt_scaleY2 = ObjectAnimator.ofFloat(txt2, "scaleY", 0, 1);
        ObjectAnimator txt_scaleY3 = ObjectAnimator.ofFloat(txt3, "scaleY", 0, 1);
        final AnimatorSet scaleDown = new AnimatorSet();
        scaleDown.play(scaleX).with(scaleY).with(fade).with(txt_scaleX2).with(txt_scaleY2).with(txt_scaleX3).with(txt_scaleY3).with(txt_fade2).with(txt_fade3);
        scaleDown.setDuration(500);
        scaleDown.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                logo.setVisibility(View.VISIBLE);
                txt2.setVisibility(View.VISIBLE);
                if (txt3 != null) {
                    txt3.setVisibility(View.VISIBLE);
                }

                invisibleItems(logo);
            }

            @Override
            public void onAnimationEnd(Animator animation) {

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        G.handler.postDelayed(scaleDown::start, 500);
    }

    private void animationOutPage1(final ImageView logo, final TextView txt2, final TextView txt3) {
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(logo, "scaleX", 1, 0);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(logo, "scaleY", 1, 0);
        ObjectAnimator fade = ObjectAnimator.ofFloat(logo, "alpha", 1, 0);
        ObjectAnimator txt_fade2 = ObjectAnimator.ofFloat(txt2, "alpha", 1, 0);
        ObjectAnimator txt_fade3 = ObjectAnimator.ofFloat(txt3, "alpha", 1, 0);
        ObjectAnimator txt_scaleX2 = ObjectAnimator.ofFloat(txt2, "scaleX", 1, 0);
        ObjectAnimator txt_scaleX3 = ObjectAnimator.ofFloat(txt3, "scaleX", 1, 0);
        ObjectAnimator txt_scaleY2 = ObjectAnimator.ofFloat(txt2, "scaleY", 1, 0);
        ObjectAnimator txt_scaleY3 = ObjectAnimator.ofFloat(txt3, "scaleY", 1, 0);

        final AnimatorSet scaleDown = new AnimatorSet();
        scaleDown.play(scaleX).with(scaleY).with(fade).with(txt_scaleX2).with(txt_scaleY2).with(txt_scaleX3).with(txt_scaleY3).with(txt_fade2).with(txt_fade3);
        scaleDown.setDuration(500);
        scaleDown.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                //logo.setVisibility(View.VISIBLE);
                //txt1.setVisibility(View.VISIBLE);
                //txt2.setVisibility(View.VISIBLE);
                //if (txt3 != null) {
                //    txt3.setVisibility(View.VISIBLE);
                //}

                invisibleItems(logo);
            }

            @Override
            public void onAnimationEnd(Animator animation) {

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        scaleDown.start();
    }

    private void animationIn(final ImageView logo, final TextView txt1, final TextView txt2) {

        if (!logo.equals(boy)) {

            ObjectAnimator scaleX = ObjectAnimator.ofFloat(logo, "scaleX", 0, 1);
            ObjectAnimator scaleY = ObjectAnimator.ofFloat(logo, "scaleY", 0, 1);
            ObjectAnimator fade = ObjectAnimator.ofFloat(logo, "alpha", 0, 1);
            ObjectAnimator txt_fade1 = ObjectAnimator.ofFloat(txt1, "alpha", 0, 1);
            ObjectAnimator txt_fade2 = ObjectAnimator.ofFloat(txt2, "alpha", 0, 1);
            ObjectAnimator txt_scaleX1 = ObjectAnimator.ofFloat(txt1, "scaleX", 0, 1);
            ObjectAnimator txt_scaleX2 = ObjectAnimator.ofFloat(txt2, "scaleX", 0, 1);
            ObjectAnimator txt_scaleY1 = ObjectAnimator.ofFloat(txt1, "scaleY", 0, 1);
            ObjectAnimator txt_scaleY2 = ObjectAnimator.ofFloat(txt2, "scaleY", 0, 1);
            final AnimatorSet scaleDown = new AnimatorSet();
            scaleDown.play(scaleX).with(scaleY).with(fade).with(txt_scaleX1).with(txt_scaleY1).with(txt_scaleX2).with(txt_scaleY2).with(txt_fade1).with(txt_fade2);

            scaleDown.setDuration(500);
            scaleDown.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                    logo.setVisibility(View.VISIBLE);
                    txt1.setVisibility(View.VISIBLE);
                    txt2.setVisibility(View.VISIBLE);

                    invisibleItems(logo);
                }

                @Override
                public void onAnimationEnd(Animator animation) {

                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });

            G.handler.postDelayed(scaleDown::start, 500);
        }
    }

    private void animationOut(final ImageView logo, final TextView txt1, final TextView txt2) {

        viewPager.setEnabled(false);

        ObjectAnimator scaleX = ObjectAnimator.ofFloat(logo, "scaleX", 1, 0);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(logo, "scaleY", 1, 0);
        final ObjectAnimator fade = ObjectAnimator.ofFloat(logo, "alpha", 1, 0);
        ObjectAnimator txt_fade1 = ObjectAnimator.ofFloat(txt1, "alpha", 1, 0);
        ObjectAnimator txt_fade2 = ObjectAnimator.ofFloat(txt2, "alpha", 1, 0);
        ObjectAnimator txt_scaleX1 = ObjectAnimator.ofFloat(txt1, "scaleX", 1, 0);
        ObjectAnimator txt_scaleX2 = ObjectAnimator.ofFloat(txt2, "scaleX", 1, 0);
        ObjectAnimator txt_scaleY1 = ObjectAnimator.ofFloat(txt1, "scaleY", 1, 0);
        ObjectAnimator txt_scaleY2 = ObjectAnimator.ofFloat(txt2, "scaleY", 1, 0);

        final AnimatorSet scaleDown = new AnimatorSet();
        scaleDown.play(scaleX).with(scaleY).with(fade).with(txt_scaleX1).with(txt_scaleY1).with(txt_scaleX2).with(txt_scaleY2).with(txt_fade1).with(txt_fade2);

        scaleDown.setDuration(500);
        scaleDown.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

                //invisibleItems(logo);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                logo.setVisibility(View.GONE);
                txt1.setVisibility(View.GONE);
                txt2.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        scaleDown.start();
    }

    private void animationInBoy(final ImageView logo, final TextView txt1, final TextView txt2) {

        ObjectAnimator scaleX = ObjectAnimator.ofFloat(logo, "scaleX", 0, 1);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(logo, "scaleY", 0, 1);
        final ObjectAnimator fade = ObjectAnimator.ofFloat(logo, "alpha", 0, 1);

        ObjectAnimator txt_fade1 = ObjectAnimator.ofFloat(txt1, "alpha", 0, 1);
        ObjectAnimator txt_fade2 = ObjectAnimator.ofFloat(txt2, "alpha", 0, 1);
        ObjectAnimator txt_scaleX1 = ObjectAnimator.ofFloat(txt1, "scaleX", 0, 1);
        ObjectAnimator txt_scaleX2 = ObjectAnimator.ofFloat(txt1, "scaleY", 0, 1);
        ObjectAnimator txt_scaleY1 = ObjectAnimator.ofFloat(txt2, "scaleX", 0, 1);
        ObjectAnimator txt_scaleY2 = ObjectAnimator.ofFloat(txt2, "scaleY", 0, 1);
        final AnimatorSet scaleDown = new AnimatorSet();
        scaleDown.play(scaleX).with(scaleY).with(fade).with(txt_scaleX1).with(txt_scaleY1).with(txt_scaleX2).with(txt_scaleY2).with(txt_fade1).with(txt_fade2);
        scaleDown.setDuration(500);
        scaleDown.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                logo.setVisibility(View.VISIBLE);
                txt1.setVisibility(View.VISIBLE);
                txt2.setVisibility(View.VISIBLE);

                invisibleItems(logo);

            }

            @Override
            public void onAnimationEnd(Animator animation) {

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        G.handler.postDelayed(scaleDown::start, 500);
    }

    private void animationOutBoy(final ImageView logo, final TextView txt1, final TextView txt2) {

        ObjectAnimator scaleX = ObjectAnimator.ofFloat(logo, "scaleX", 1, 0);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(logo, "scaleY", 1, 0);
        ObjectAnimator fade = ObjectAnimator.ofFloat(logo, "alpha", 1, 0);

        ObjectAnimator fade2 = ObjectAnimator.ofFloat(txt1, "alpha", 1, 0);
        ObjectAnimator fade3 = ObjectAnimator.ofFloat(txt2, "alpha", 1, 0);
        ObjectAnimator txt_scaleX = ObjectAnimator.ofFloat(txt1, "scaleX", 1, 0);
        ObjectAnimator txt_scaleY = ObjectAnimator.ofFloat(txt1, "scaleY", 1, 0);
        ObjectAnimator txt_scaleX2 = ObjectAnimator.ofFloat(txt2, "scaleX", 1, 0);
        ObjectAnimator txt_scaleY2 = ObjectAnimator.ofFloat(txt2, "scaleY", 1, 0);

        AnimatorSet scaleDown = new AnimatorSet();
        scaleDown.play(scaleX).with(scaleY).with(fade).with(txt_scaleX).with(txt_scaleY).with(fade2).with(fade3).with(txt_scaleX2).with(txt_scaleY2);
        scaleDown.setDuration(500);
        scaleDown.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

                invisibleItems(logo);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                logo.setVisibility(View.GONE);
                txt1.setVisibility(View.GONE);
                txt2.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        scaleDown.start();
    }

    private void invisibleItems(ImageView logo) {

        if (logo.equals(logoIgap)) { // 1

            logoSecurity.setVisibility(View.INVISIBLE);
            boy.setVisibility(View.GONE);

            txt_p2_l1.setVisibility(View.GONE);
            txt_p2_l2.setVisibility(View.GONE);

            txt_p6_l1.setVisibility(View.GONE);
            txt_p6_l2.setVisibility(View.GONE);
//            btnStart.setVisibility(View.GONE);
        }
        if (logo.equals(logoSecurity)) { //2
            logoIgap.setVisibility(View.INVISIBLE);
            boy.setVisibility(View.GONE);

            txt_p1_l2.setVisibility(View.GONE);
            txt_p1_l3.setVisibility(View.GONE);

            txt_p6_l1.setVisibility(View.GONE);
            txt_p6_l2.setVisibility(View.GONE);
//            btnStart.setVisibility(View.GONE);
        }
        if (logo.equals(boy)) { //6

            logoIgap.setVisibility(View.INVISIBLE);
            logoSecurity.setVisibility(View.INVISIBLE);

            txt_p1_l2.setVisibility(View.GONE);
            txt_p1_l3.setVisibility(View.GONE);

            txt_p2_l1.setVisibility(View.GONE);
            txt_p2_l2.setVisibility(View.GONE);

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
            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(@NotNull ViewGroup container, int position, @NotNull Object object) {
            container.removeView((View) object);
        }
    }
}
