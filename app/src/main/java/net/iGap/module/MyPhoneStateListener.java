package net.iGap.module;

import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

public class MyPhoneStateListener extends PhoneStateListener {

    public static int lastPhoneState = TelephonyManager.CALL_STATE_IDLE;

    public void onCallStateChanged(int state, String incomingNumber) {

        if (lastPhoneState == state || !MusicPlayer.isMusicPlyerEnable) {
            return;
        } else {

            lastPhoneState = state;

            if (state == TelephonyManager.CALL_STATE_RINGING) {

                if (MusicPlayer.mp != null && MusicPlayer.mp.isPlaying()) {

                    MusicPlayer.pauseSound();
                    MusicPlayer.pauseSoundFromCall = true;
                }
            } else if (state == TelephonyManager.CALL_STATE_IDLE) {

                if (MusicPlayer.pauseSoundFromCall) {
                    MusicPlayer.pauseSoundFromCall = false;
                    MusicPlayer.playAndPause();
                }
            } else if (state == TelephonyManager.CALL_STATE_OFFHOOK) {

            }
        }
    }
}