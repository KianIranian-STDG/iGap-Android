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
import net.iGap.interfaces.ISignalingAccept;
import net.iGap.interfaces.ISignalingCandidate;
import net.iGap.interfaces.ISignalingGetCallLog;
import net.iGap.interfaces.ISignalingLeave;
import net.iGap.interfaces.ISignalingOffer;
import net.iGap.interfaces.ISignalingRinging;
import net.iGap.interfaces.ISignalingSessionHold;
import net.iGap.proto.ProtoSignalingLeave;
import net.iGap.proto.ProtoSignalingOffer;
import net.iGap.request.RequestSignalingRinging;
import org.webrtc.IceCandidate;
import org.webrtc.SdpObserver;
import org.webrtc.SessionDescription;

import static net.iGap.G.iSignalingAccept;
import static net.iGap.G.iSignalingCandidate;
import static net.iGap.G.iSignalingLeave;
import static net.iGap.G.iSignalingOffer;
import static net.iGap.G.iSignalingRinging;
import static net.iGap.G.iSignalingSessionHold;
import static net.iGap.webrtc.WebRTC.peerConnection;
import static net.iGap.webrtc.WebRTC.peerConnectionInstance;
import static org.webrtc.SessionDescription.Type.ANSWER;
import static org.webrtc.SessionDescription.Type.OFFER;

public class CallObserver implements ISignalingOffer, ISignalingRinging, ISignalingAccept, ISignalingCandidate, ISignalingLeave, ISignalingSessionHold, ISignalingGetCallLog {

    public CallObserver() {
        iSignalingOffer = this;
        iSignalingRinging = this;
        iSignalingAccept = this;
        iSignalingCandidate = this;
        iSignalingLeave = this;
        iSignalingSessionHold = this;
    }

    @Override
    public void onOffer(long called_userId, ProtoSignalingOffer.SignalingOffer.Type type, final String callerSdp) {
        new RequestSignalingRinging().signalingRinging();

        G.handler.post(new Runnable() {
            @Override
            public void run() {
                peerConnectionInstance().setRemoteDescription(new SdpObserver() {
                    @Override
                    public void onCreateSuccess(SessionDescription sessionDescription) {
                    }

                    @Override
                    public void onSetSuccess() {
                        G.handler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(G.context, "You Have A Call ... ", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onCreateFailure(String s) {
                        Log.i("WWW", "onOffer onCreateFailure : " + s);
                    }

                    @Override
                    public void onSetFailure(String s) {
                        Log.i("WWW", "onOffer onSetFailure : " + s);
                    }
                }, new SessionDescription(OFFER, callerSdp));

                Log.i("WWW", "onOffer WebRtc.peerConnectionInstance().iceConnectionState() : " + peerConnectionInstance().iceConnectionState());
                Log.i("WWW", "onOffer WebRtc.peerConnectionInstance().iceGatheringState() : " + peerConnectionInstance().iceGatheringState());
                Log.i("WWW", "onOffer WebRtc.peerConnectionInstance().signalingState() : " + peerConnectionInstance().signalingState());
            }
        });
    }

    @Override
    public void onAccept(final String called_sdp) {
        Log.i("WWW", "onAccept sdp : " + called_sdp);

        G.handler.post(new Runnable() {
            @Override
            public void run() {
                peerConnectionInstance().setRemoteDescription(new SdpObserver() {
                    @Override
                    public void onCreateSuccess(SessionDescription sessionDescription) {
                        Log.i("WWW", "onAccept onCreateSuccess sessionDescription : " + sessionDescription);
                        Log.i("WWW", "onAccept onCreateSuccess sessionDescription.description : " + sessionDescription.description);
                        Log.i("WWW", "onAccept onCreateSuccess sessionDescription.type : " + sessionDescription.type);
                    }

                    @Override
                    public void onSetSuccess() {
                        G.handler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(G.context, "Callee Accepted You ! ", Toast.LENGTH_SHORT).show();
                            }
                        });
                        Log.i("WWW", "onAccept onSetSuccess");
                    }

                    @Override
                    public void onCreateFailure(String s) {
                        Log.i("WWW", "onAccept onCreateFailure : " + s);
                    }

                    @Override
                    public void onSetFailure(String s) {
                        Log.i("WWW", "onAccept onSetFailure : " + s);
                    }
                }, new SessionDescription(ANSWER, called_sdp));

                Log.i("WWW", "onAccept WebRtc.peerConnectionInstance().iceConnectionState() : " + peerConnectionInstance().iceConnectionState());
                Log.i("WWW", "onAccept WebRtc.peerConnectionInstance().iceGatheringState() : " + peerConnectionInstance().iceGatheringState());
                Log.i("WWW", "onAccept WebRtc.peerConnectionInstance().signalingState() : " + peerConnectionInstance().signalingState());
            }
        });



    }

    @Override
    public void onCandidate(final String iceCandidate) {
        G.handler.post(new Runnable() {
            @Override
            public void run() {
                Log.i("WWW_Candidate", "onCandidate server : " + iceCandidate);
                //String[] identityParams = iceCandidate.split(":");
                //String sdpMid = identityParams[0];
                //int sdpMLineIndex = Integer.parseInt(identityParams[1]);
                //String sdp1 = identityParams[2];
                //String sdp2 = identityParams[3];
                //Log.i("WWW_Candidate", "onCandidate sdpMid : " + sdpMid);
                //Log.i("WWW_Candidate", "onCandidate sdpMLineIndex : " + sdpMLineIndex);
                //Log.i("WWW_Candidate", "onCandidate sdp : " + sdp1 + ":" + sdp2);
                //WebRTC.peerConnectionInstance().addIceCandidate(new IceCandidate(sdpMid, sdpMLineIndex, sdp1 + ":" + sdp2));
                peerConnectionInstance().addIceCandidate(new IceCandidate("", 0, iceCandidate));
            }
        });
    }


    @Override
    public void onGetList(int size) {

    }

    @Override
    public void onLeave(final ProtoSignalingLeave.SignalingLeaveResponse.Type type) {
        G.handler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(G.context, type.toString() + " ... ", Toast.LENGTH_SHORT).show();
            }
        });

        peerConnectionInstance().close();
        peerConnectionInstance().dispose();
        /**
         * set peer connection null for try again
         */
        peerConnection = null;
    }

    @Override
    public void onRinging() {

    }

    @Override
    public void onHold(Boolean hold) {

    }
}
