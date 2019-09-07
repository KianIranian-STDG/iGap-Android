package net.iGap.helper;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.ViewPager;

public class ParallaxPageTransformer implements ViewPager.PageTransformer {


    @Override
    public void transformPage(@NonNull View view, float v) {
        if (v < -1) { // [-Infinity,-1)
            // This page is way off-screen to the left.
            view.setAlpha(1f);

        } else if (v <= 1) { // [-1,1]
            if (v > 0) {
                view.setScaleX(1 - v);
                view.setScaleY(1 - v);
                view.setAlpha(1 - v);
            } else {
                view.setScaleX(1 + v);
                view.setScaleY(1 + v);
                view.setAlpha(1 + v);
            }

        } else { // (1,+Infinity]
            // This page is way off-screen to the right.
            view.setAlpha(1f);
        }
    }
}
