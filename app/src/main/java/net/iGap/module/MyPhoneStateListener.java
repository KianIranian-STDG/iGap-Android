package net.iGap.module;

import android.content.Context;
import android.media.AudioManager;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import net.iGap.G;

public class MyPhoneStateListener extends PhoneStateListener {

    public static int lastPhoneState = TelephonyManager.CALL_STATE_IDLE;
    public static boolean isBlutoothOn = false;

    public void onCallStateChanged(int state, String incomingNumber) {

        if (lastPhoneState == state || !MusicPlayer.isMusicPlyerEnable) {
            return;
        } else {

            lastPhoneState = state;

            if (state == TelephonyManager.CALL_STATE_RINGING) {

                if (MusicPlayer.mp != null && MusicPlayer.mp.isPlaying()) {

                    MusicPlayer.pauseSound();
                    MusicPlayer.pauseSoundFromCall = true;

                    AudioManager am = (AudioManager) G.context.getSystemService(Context.AUDIO_SERVICE);

                    if (am.isBluetoothScoOn()) {
                        isBlutoothOn = true;
                        am.setBluetoothScoOn(false);
                    }
                }
            } else if (state == TelephonyManager.CALL_STATE_IDLE) {

                if (MusicPlayer.pauseSoundFromCall) {
                    MusicPlayer.pauseSoundFromCall = false;
                    MusicPlayer.playAndPause();

                    if (isBlutoothOn) {
                        isBlutoothOn = false;

                        AudioManager am = (AudioManager) G.context.getSystemService(Context.AUDIO_SERVICE);
                        am.setBluetoothScoOn(true);
                    }

                }
            } else if (state == TelephonyManager.CALL_STATE_OFFHOOK) {

            }
        }
    }
}