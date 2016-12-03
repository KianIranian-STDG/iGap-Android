package io.github.meness.audioplayerview.listeners;

import android.media.MediaPlayer;

/**
 * Created by Alireza Eskandarpour Shoferi (meNESS) on 11/29/2016.
 */

public interface IAnotherPlayOrPause {
    void onAnotherPlay(MediaPlayer player);

    void onAnotherPause(MediaPlayer player);
}
