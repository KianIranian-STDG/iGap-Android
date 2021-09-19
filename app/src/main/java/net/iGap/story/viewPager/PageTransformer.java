package net.iGap.story.viewPager;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.ViewPager;

public class PageTransformer implements ViewPager.PageTransformer {
    private int distanceMultiplier = 20;

    @Override
    public void transformPage(@NonNull View page, float position) {
        float clampedPosition = clampPosition(position);
        onPreTransform(page, clampedPosition);
        onTransform(page, clampedPosition);
    }

    private float clampPosition(float position) {
        if (position < -1f) {
            return -1f;
        }
        if (position > 1f) {
            return 1f;
        } else {
            return position;
        }
    }

    private void onPreTransform(View page, float position) {
        page.setRotationX(0f);
        page.setRotationY(0f);
        page.setRotation(0f);
        page.setScaleX(1f);
        page.setScaleY(1f);
        page.setPivotX(0f);
        page.setPivotY(0f);
        page.setTranslationY(0f);
        page.setTranslationX(0f);
        page.setAlpha((position <= -1f || position >= 1f) ? 0f : 1f);
        page.setEnabled(false);
    }

    private void onTransform(View page, float position) {
        page.setCameraDistance((float) page.getWidth() * distanceMultiplier);
        page.setPivotX(position < 0f ? (float) page.getWidth() : 0f);
        page.setPivotY(page.getHeight() * 0.5f);
        page.setRotationY(90f * position);
    }
}
