package io.meness.github.messageprogress;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by Alireza Eskandarpour Shoferi (meNESS) on 10/29/2016.
 */

@Retention(RetentionPolicy.SOURCE)
@IntDef({ ProgressProcess.PROCESSING, ProgressProcess.NOT_PROCESSING })
public @interface ProgressProcess {
    int PROCESSING = 1;
    int NOT_PROCESSING = -1;
}
