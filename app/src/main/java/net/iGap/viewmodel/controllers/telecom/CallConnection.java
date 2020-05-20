package net.iGap.viewmodel.controllers.telecom;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Build;
import android.telecom.CallAudioState;
import android.telecom.Connection;
import android.telecom.DisconnectCause;

import androidx.annotation.RequiresApi;

import net.iGap.R;

@RequiresApi(api = Build.VERSION_CODES.M)
public class CallConnection extends Connection {

    public static final String EXTRA_PHONE_ACCOUNT_HANDLE = "com.android.server.telecom.testapps.extra.PHONE_ACCOUNT_HANDLE";

    private final MediaPlayer mMediaPlayer;

    public CallConnection(Context context) {
        mMediaPlayer = createMediaPlayer(context);
    }

    public void setConnectionActive() {
        mMediaPlayer.start();
        setActive();
    }

    public void setConnectionHeld() {
        mMediaPlayer.pause();
        setOnHold();
    }

    public void setConnectionDisconnected(int cause) {
        mMediaPlayer.stop();
        setDisconnected(new DisconnectCause(cause));
        destroy();
    }

    @Override
    public void onShowIncomingCallUi() {
        super.onShowIncomingCallUi();
    }

    @Override
    public void onCallAudioStateChanged(CallAudioState state) {
        super.onCallAudioStateChanged(state);
    }

    @Override
    public void onHold() {
        super.onHold();
    }

    @Override
    public void onUnhold() {
        super.onUnhold();
    }

    @Override
    public void onAnswer() {
        super.onAnswer();
    }

    @Override
    public void onReject() {
        super.onReject();
    }

    @Override
    public void onDisconnect() {
        super.onDisconnect();
    }

    private MediaPlayer createMediaPlayer(Context context) {
        int audioToPlay = (Math.random() > 0.5f) ? R.raw.igap_ringing : R.raw.igap_ringing;
        MediaPlayer mediaPlayer = MediaPlayer.create(context, audioToPlay);
        mediaPlayer.setLooping(true);
        return mediaPlayer;
    }
}
