package net.iGap.module;

import android.content.Context;
import android.media.AudioManager;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

import net.iGap.G;
import net.iGap.webrtc.WebRTC;



public class MyPhoneStateListener extends PhoneStateListener {

    public static int lastPhoneState = TelephonyManager.CALL_STATE_IDLE;
    public static boolean isBlutoothOn = false;


    public void onCallStateChanged(int state, String incomingNumber) {
        if (state == TelephonyManager.CALL_STATE_OFFHOOK) {
          /*  if (G.onRejectCallStatus != null)
                G.onRejectCallStatus.setReject(true);*/
            try {
                WebRTC.getInstance().leaveCall();
            }catch (Exception e){}

            G.isCalling = true;
        } else if (state == TelephonyManager.CALL_STATE_RINGING) {

            if (G.isVideoCallRinging) {
                try {
                    WebRTC.getInstance().leaveCall();
                }catch (Exception e){}

              /*  if (G.onRejectCallStatus != null)
                    G.onRejectCallStatus.setReject(true);*/
            }
            G.isCalling = true;
            G.isVideoCallRinging = false;
        } else if (state == TelephonyManager.CALL_STATE_IDLE) {
            G.isCalling = false;
        }


        if (lastPhoneState == state || !MusicPlayer.isMusicPlyerEnable) {

            return;
        } else {

            lastPhoneState = state;

            if (state == TelephonyManager.CALL_STATE_RINGING) {
                pauseSoundIfPlay();

            } else if (state == TelephonyManager.CALL_STATE_IDLE) {

                if (MusicPlayer.pauseSoundFromCall) {
                    MusicPlayer.pauseSoundFromCall = false;
                    MusicPlayer.playSound();

                    //if (isBlutoothOn) {
                    //    isBlutoothOn = false;
                    //
                    //    AudioManager am = (AudioManager) G.context.getSystemService(Context.AUDIO_SERVICE);
                    //    am.setBluetoothScoOn(true);
                    //}
                }
            } else if (state == TelephonyManager.CALL_STATE_OFFHOOK) {
                pauseSoundIfPlay();
            }
        }
    }

    private void pauseSoundIfPlay() {

        if (MusicPlayer.mp != null && MusicPlayer.mp.isPlaying()) {

            MusicPlayer.pauseSound();
            MusicPlayer.pauseSoundFromCall = true;

            //AudioManager am = (AudioManager) G.context.getSystemService(Context.AUDIO_SERVICE);

            //if (am.isBluetoothScoOn()) {
            //    isBlutoothOn = true;
            //    am.setBluetoothScoOn(false);
            //
            //    try {
            //        am.stopBluetoothSco();
            //    } catch (Exception e) {
            //        HelperLog.setErrorLog("myPhoneStateListener    " + e.toString());
            //    }
            //}
        }
    }
}