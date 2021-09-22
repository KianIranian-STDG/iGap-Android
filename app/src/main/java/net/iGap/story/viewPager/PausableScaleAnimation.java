package net.iGap.story.viewPager;

import android.view.animation.ScaleAnimation;
import android.view.animation.Transformation;

public class PausableScaleAnimation extends ScaleAnimation {

    private long elapsedAtPause = 0;
    public boolean isPaused = false;

    public PausableScaleAnimation(float fromX, float toX, float fromY, float toY, int pivotXType, float pivotXValue, int pivotYType, float pivotYValue) {
        super(fromX, toX, fromY, toY, pivotXType, pivotXValue, pivotYType, pivotYValue);
    }

    @Override
    public boolean getTransformation(long currentTime, Transformation outTransformation, float scale) {
        if (isPaused && elapsedAtPause == 0L) {
            elapsedAtPause = currentTime - getStartTime();
        }
        if (isPaused) {
            setStartTime(currentTime - elapsedAtPause);
        }

        return super.getTransformation(currentTime, outTransformation, scale);
    }

    public void pause() {
        if (isPaused)
            return;
        elapsedAtPause = 0;
        isPaused = true;
    }

    public void resume() {
        isPaused = false;
    }
}
