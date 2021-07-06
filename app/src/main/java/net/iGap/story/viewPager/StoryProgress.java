package net.iGap.story.viewPager;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import net.iGap.R;

public class StoryProgress extends FrameLayout {

    public final static long DEFAULT_PROGRESS_DURATION = 5000L;
    private long duration = DEFAULT_PROGRESS_DURATION;
    private boolean isStarted = false;
    private View frontProgressView;
    private View maxProgressView;
    private PausableScaleAnimation animation;
    private Callback callback;

    public StoryProgress(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(getContext()).inflate(R.layout.pausable_progress, this);
        frontProgressView = findViewById(R.id.front_progress);
        maxProgressView = findViewById(R.id.max_progress);
    }

    public void setDuration(long duration) {
        this.duration = duration;
        if (animation != null) {
            animation = null;
            startProgress();
        }
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public void setMax() {
        finishProgress(true);
    }

    public void setMin() {
        finishProgress(false);
    }

    public void setMinWithoutCallback() {
        if (maxProgressView != null) {
            maxProgressView.setBackgroundResource(R.color.gray);
            maxProgressView.setVisibility(VISIBLE);
            if (animation != null) {
                animation.setAnimationListener(null);
                animation.cancel();
            }
        }
    }

    public void setMaxWithoutCallback() {
        if (maxProgressView != null) {
            maxProgressView.setBackgroundResource(R.color.white);
            maxProgressView.setVisibility(VISIBLE);
            if (animation != null) {
                animation.setAnimationListener(null);
                animation.cancel();
            }
        }
    }

    private void finishProgress(boolean isMax) {
        if (isMax)
            maxProgressView.setBackgroundResource(R.color.white);
        maxProgressView.setVisibility(isMax ? VISIBLE : GONE);
        if (animation != null) {
            animation.setAnimationListener(null);
            animation.cancel();
            if (callback != null) {
                callback.onFinishProgress();
            }
        }
    }

    public void startProgress() {
        maxProgressView.setVisibility(GONE);
        if (duration <= 0)
            duration = DEFAULT_PROGRESS_DURATION;

        animation = new PausableScaleAnimation(
                0f,
                1f,
                1f,
                1f,
                Animation.ABSOLUTE,
                0f,
                Animation.RELATIVE_TO_SELF,
                0f
        );
        animation.setDuration(duration);
        animation.setInterpolator(new LinearInterpolator());
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                if (isStarted) {
                    return;
                }
                isStarted = true;
                frontProgressView.setVisibility(VISIBLE);
                if (callback != null) callback.onStartProgress();
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                isStarted = false;
                if (callback != null) callback.onFinishProgress();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        animation.setFillAfter(true);
        frontProgressView.startAnimation(animation);

    }

    public void pauseProgress() {
        if (animation != null)
            animation.pause();
    }

    public void resumeProgress() {
        if (animation != null)
            animation.resume();
    }

    public void clear() {
        if (animation != null) {
            animation.setAnimationListener(null);
            animation.cancel();
            animation = null;
        }
    }

    public interface Callback {
        void onStartProgress();

        void onFinishProgress();
    }
}
