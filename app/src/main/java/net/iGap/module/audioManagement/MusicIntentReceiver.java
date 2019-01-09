package net.iGap.module.audioManagement;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;

import net.iGap.G;
import net.iGap.R;
import net.iGap.proto.ProtoSignalingOffer;

public class MusicIntentReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_HEADSET_PLUG)) {
            int state = intent.getIntExtra("state", -1);
            AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            switch (state) {
                case 0:
                    am.setSpeakerphoneOn(true);
                    G.isHandsFreeConnected = false;
                    if (G.speakerControlListener != null) {
                        G.speakerControlListener.setOnChangeSpeaker(R.string.md_unMuted);

                    }
                    break;
                case 1:
                    am.setSpeakerphoneOn(false);
                    G.isHandsFreeConnected = true;
                    if (G.speakerControlListener != null) {
                        if (G.isBluetoothConnected)
                            G.speakerControlListener.setOnChangeSpeaker(R.string.md_igap_bluetooth);
                        else
                            G.speakerControlListener.setOnChangeSpeaker(R.string.md_Mute);
                    }
                    break;
                default:
                    am.setSpeakerphoneOn(true);
                    G.isHandsFreeConnected = false;
                    if (G.speakerControlListener != null) {
                        G.speakerControlListener.setOnChangeSpeaker(R.string.md_unMuted);
                    }
            }
        }
    }
}
