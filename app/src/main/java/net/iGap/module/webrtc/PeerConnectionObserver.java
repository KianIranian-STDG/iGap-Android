/*
 * Thf is the source code of iGap for Android
 * It is licensed under GNU AGPL v3.0
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright © 2017 , iGap - www.iGap.net
 * iGap Messenger | Free, Fast and Secure instant messaging application
 * The idea of the Kianiranian Company - www.kianiranian.com
 * All rights reserved.
 */


//public static ISignalingOffer iSignalingOffer;
//public static ISignalingRinging iSignalingRinging;
//public static ISignalingAccept iSignalingAccept;
//public static ISignalingCandidate iSignalingCandidate;
//public static ISignalingLeave iSignalingLeave;
//public static ISignalingSessionHold iSignalingSessionHold;
//public static ISignalingGetCallLog iSignalingGetCallLog;

package net.iGap.module.webrtc;

import net.iGap.module.enums.CallState;
import net.iGap.viewmodel.controllers.CallManager;

import org.webrtc.AudioTrack;
import org.webrtc.DataChannel;
import org.webrtc.IceCandidate;
import org.webrtc.MediaStream;
import org.webrtc.PeerConnection;
import org.webrtc.RtpReceiver;
import org.webrtc.VideoTrack;

import static org.webrtc.PeerConnection.IceConnectionState.CHECKING;
import static org.webrtc.PeerConnection.IceConnectionState.CONNECTED;
import static org.webrtc.PeerConnection.IceConnectionState.DISCONNECTED;
import static org.webrtc.PeerConnection.IceConnectionState.FAILED;

public class PeerConnectionObserver implements PeerConnection.Observer {

    private String TAG = "iGapCall " + getClass().getSimpleName();

    @Override
    public void onSignalingChange(PeerConnection.SignalingState signalingState) {

    }

    @Override
    public void onIceConnectionChange(final PeerConnection.IceConnectionState iceConnectionState) {
        if (iceConnectionState == DISCONNECTED) {
            CallManager.getInstance().changeState(CallState.DISCONNECTED);
        } else if (iceConnectionState == FAILED) {
            CallManager.getInstance().changeState(CallState.FAILD);
        } else if (iceConnectionState == CHECKING) {
            CallManager.getInstance().changeState(CallState.CONNECTING);
        } else if (iceConnectionState == CONNECTED) {
            CallManager.getInstance().changeState(CallState.CONNECTED);
        }
    }

    @Override
    public void onIceConnectionReceivingChange(boolean b) {

    }

    @Override
    public void onIceGatheringChange(PeerConnection.IceGatheringState iceGatheringState) {

    }

    @Override
    public void onIceCandidate(IceCandidate iceCandidate) {
        CallManager.getInstance().exchangeCandidate(iceCandidate.sdpMid, iceCandidate.sdpMLineIndex, iceCandidate.sdp);
    }

    @Override
    public void onIceCandidatesRemoved(IceCandidate[] iceCandidates) {

    }


    @Override
    public void onAddStream(MediaStream stream) {

        if (stream.audioTracks.size() > 1 || stream.videoTracks.size() > 1) {
            return;
        }

        for (AudioTrack audioTrack : stream.audioTracks) {
            audioTrack.setEnabled(true);
        }

        if (stream.videoTracks.size() == 1) {
            VideoTrack videoTrack = stream.videoTracks.get(0);
            videoTrack.setEnabled(true);

            videoTrack.addSink(videoFrame -> {
                if (WebRTC.getInstance().getFrameListener() != null) {
                    WebRTC.getInstance().getFrameListener().onRemoteFrame(videoFrame);
                }
//                    if (G.onVideoCallFrame != null) {
//                        G.onVideoCallFrame.onRemoteFrame(videoFrame);
//                    }
            });

        }

    }

    @Override
    public void onRemoveStream(MediaStream mediaStream) {

    }

    @Override
    public void onDataChannel(DataChannel dataChannel) {

    }

    @Override
    public void onRenegotiationNeeded() {

    }

    @Override
    public void onAddTrack(RtpReceiver rtpReceiver, MediaStream[] mediaStreams) {

    }

}
