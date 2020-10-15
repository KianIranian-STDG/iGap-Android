package net.iGap.module;

import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

import net.iGap.module.webrtc.WebRTC;
import net.iGap.viewmodel.controllers.CallManager;


public class MyPhoneStateListener extends PhoneStateListener {

    public static int lastPhoneState = TelephonyManager.CALL_STATE_IDLE;
    public static boolean isBlutoothOn = false;

    /**
     * in this function we observe phone's state changes. and we manage two things:
     * 1- manage music player state when phone state changes
     * 2- manage call state (video or voice) when phone state changes
     *
     * @param state
     * @param incomingNumber
     */
    public void onCallStateChanged(int state, String incomingNumber) {

        // managing music player state
        if (lastPhoneState != state && MusicPlayer.isMusicPlyerEnable) {
            if (state == TelephonyManager.CALL_STATE_RINGING) {
                pauseSoundIfPlay();
            } else if (state == TelephonyManager.CALL_STATE_IDLE) {
                if (MusicPlayer.pauseSoundFromCall) {
                    MusicPlayer.pauseSoundFromCall = false;
                    MusicPlayer.playSound();
                }
            } else if (state == TelephonyManager.CALL_STATE_OFFHOOK) {
                pauseSoundIfPlay();
            }
        }
        // if last phone state does not change so there is nothing to do: preventing from multiple calls to proto and server
        if (lastPhoneState == state)
            return;
        else
            lastPhoneState = state;

        if (state == TelephonyManager.CALL_STATE_OFFHOOK) {
            CallManager.getInstance().holdCall(true);
            WebRTC.getInstance().toggleSound(false);
            WebRTC.getInstance().pauseVideoCapture();
            CallManager.getInstance().setUserInCall(true);
        } else if (state == TelephonyManager.CALL_STATE_RINGING) {
            CallManager.getInstance().setUserInCall(false);
        } else if (state == TelephonyManager.CALL_STATE_IDLE) {
            CallManager.getInstance().holdCall(false);
            WebRTC.getInstance().toggleSound(true);
            WebRTC.getInstance().startVideoCapture();
            CallManager.getInstance().setUserInCall(false);
        }
    }

    private void pauseSoundIfPlay() {

        if (MusicPlayer.mp != null && MusicPlayer.mp.isPlaying()) {

            MusicPlayer.pauseSound();
            MusicPlayer.pauseSoundFromCall = true;

        }
    }
}