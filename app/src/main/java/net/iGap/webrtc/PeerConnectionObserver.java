/*
* This is the source code of iGap for Android
* It is licensed under GNU AGPL v3.0
* You should have received a copy of the license in this archive (see LICENSE).
* Copyright © 2017 , iGap - www.iGap.net
* iGap Messenger | Free, Fast and Secure instant messaging application
* The idea of the RooyeKhat Media Company - www.RooyeKhat.co
* All rights reserved.
*/


//public static ISignalingOffer iSignalingOffer;
//public static ISignalingRinging iSignalingRinging;
//public static ISignalingAccept iSignalingAccept;
//public static ISignalingCandidate iSignalingCandidate;
//public static ISignalingLeave iSignalingLeave;
//public static ISignalingSessionHold iSignalingSessionHold;
//public static ISignalingGetCallLog iSignalingGetCallLog;

package net.iGap.webrtc;

import android.util.Log;
import android.widget.Toast;
import net.iGap.G;
import net.iGap.request.RequestSignalingCandidate;
import org.webrtc.AudioTrack;
import org.webrtc.DataChannel;
import org.webrtc.IceCandidate;
import org.webrtc.MediaStream;
import org.webrtc.PeerConnection;
import org.webrtc.RtpReceiver;
import org.webrtc.VideoRenderer;
import org.webrtc.VideoTrack;

public class PeerConnectionObserver implements PeerConnection.Observer, VideoRenderer.Callbacks {


    @Override
    public void onSignalingChange(PeerConnection.SignalingState signalingState) {
        Log.i("WWW", "onSignalingChange : " + signalingState);
    }

    @Override
    public void onIceConnectionChange(final PeerConnection.IceConnectionState iceConnectionState) {

        if (iceConnectionState == PeerConnection.IceConnectionState.CONNECTED) {
            G.handler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(G.context, "Call is active !!! ", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            G.handler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(G.context, iceConnectionState.toString(), Toast.LENGTH_SHORT).show();
                }
            });
        }
        Log.i("WWW", "onIceConnectionChange : " + iceConnectionState);
    }

    @Override
    public void onIceConnectionReceivingChange(boolean b) {
        Log.i("WWW", "onIceConnectionReceivingChange : " + b);
    }

    @Override
    public void onIceGatheringChange(PeerConnection.IceGatheringState iceGatheringState) {
        Log.i("WWW", "onIceGatheringChange : " + iceGatheringState);
    }

    @Override
    public void onIceCandidate(IceCandidate iceCandidate) {
        Log.i("WWW", "onIceCandidate");
        Log.i("WWW", "WebRtc onIceCandidate : " + iceCandidate.toString());
        Log.i("WWW", "WebRtc onIceCandidate.sdp : " + iceCandidate.sdp);
        Log.i("WWW", "WebRtc onIceCandidate.sdpMid : " + iceCandidate.sdpMid);
        Log.i("WWW", "WebRtc onIceCandidate.sdpMLineIndex : " + iceCandidate.sdpMLineIndex);
        new RequestSignalingCandidate().signalingCandidate(iceCandidate.sdp);

    }

    @Override
    public void onIceCandidatesRemoved(IceCandidate[] iceCandidates) {
        Log.i("WWW", "onIceCandidatesRemoved : " + iceCandidates);
    }

    @Override
    public void onAddStream(MediaStream stream) {
        Log.i("WWW", "onAddStream : " + stream);

        for (AudioTrack audioTrack : stream.audioTracks) {
            audioTrack.setEnabled(true);
        }

        if (stream.videoTracks != null && stream.videoTracks.size() == 1) {
            VideoTrack videoTrack = stream.videoTracks.getFirst();
            videoTrack.setEnabled(true);
            videoTrack.addRenderer(new VideoRenderer(this));
        }
    }

    @Override
    public void onRemoveStream(MediaStream mediaStream) {
        Log.i("WWW", "onRemoveStream : " + mediaStream);
    }

    @Override
    public void onDataChannel(DataChannel dataChannel) {
        Log.i("WWW", "onDataChannel : " + dataChannel);
    }

    @Override
    public void onRenegotiationNeeded() {
        Log.i("WWW", "onRenegotiationNeeded");
    }

    @Override
    public void onAddTrack(RtpReceiver rtpReceiver, MediaStream[] mediaStreams) {
        Log.i("WWW", "onAddTrack RtpReceiver : " + rtpReceiver + "  ||  MediaStream[] : " + mediaStreams);
    }

    @Override
    public void renderFrame(VideoRenderer.I420Frame i420Frame) {
        Log.i("WWW", "renderFrame : " + i420Frame);
    }

}
