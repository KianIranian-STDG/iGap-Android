package net.iGap.story.viewPager;

import android.os.Handler;
import android.util.Log;

import androidx.viewpager.widget.ViewPager;

import static android.widget.NumberPicker.OnScrollListener.SCROLL_STATE_FLING;
import static android.widget.NumberPicker.OnScrollListener.SCROLL_STATE_IDLE;
import static android.widget.NumberPicker.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL;

public abstract class PageChangeListener implements ViewPager.OnPageChangeListener {

    private int pageBeforeDragging = 0;
    private int currentPage = 0;
    private static long DEBOUNCE_TIMES = 500L;
    private long lastTime = DEBOUNCE_TIMES + 1L;

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        currentPage = position;
    }

    @Override
    public void onPageScrollStateChanged(int state) {

        switch (state) {

            case SCROLL_STATE_IDLE:
                long now = System.currentTimeMillis();
                if (now - lastTime < DEBOUNCE_TIMES) {
                    return;
                }
                lastTime = now;
                new Handler().postDelayed(() -> {
                    if (pageBeforeDragging == currentPage) {
                        onPageScrollCanceled();
                    }
                }, 300L);

            case SCROLL_STATE_TOUCH_SCROLL:
                pageBeforeDragging = currentPage;

            case SCROLL_STATE_FLING:
                Log.i("nazanin", "onPageScrollStateChanged: SCROLL_STATE_FLING");


        }
    }

    abstract void onPageScrollCanceled();
}
