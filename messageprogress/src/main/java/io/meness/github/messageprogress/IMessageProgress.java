package io.meness.github.messageprogress;

import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;

/**
 * Created by Alireza Eskandarpour Shoferi (meNESS) on 10/10/2016.
 */
public interface IMessageProgress {
    void withDrawable(@DrawableRes int res);

    void withDrawable(Drawable drawable);

    void withProgress(int i);

    void withIndeterminate(boolean b);

    void withHideProgress();

    float getProgress();

    void withProgressFinishedDrawable(@DrawableRes int d);

    void withProgressFinishedDrawable(Drawable d);

    void withProgressFinishedHide();

    void reset();

    void withOnMessageProgress(OnMessageProgressClick listener);

    void withOnProgress(OnProgress listener);

    void performProgress();

    @ProgressProcess int getProcessType();
}
