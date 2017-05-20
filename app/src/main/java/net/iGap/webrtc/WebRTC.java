/*
* This is the source code of iGap for Android
* It is licensed under GNU AGPL v3.0
* You should have received a copy of the license in this archive (see LICENSE).
* Copyright © 2017 , iGap - www.iGap.net
* iGap Messenger | Free, Fast and Secure instant messaging application
* The idea of the RooyeKhat Media Company - www.RooyeKhat.co
* All rights reserved.
*/

package net.iGap.webrtc;


import android.util.Log;
import io.realm.Realm;
import java.util.ArrayList;
import java.util.List;
import net.iGap.G;
import net.iGap.proto.ProtoSignalingGetConfiguration;
import net.iGap.proto.ProtoSignalingOffer;
import net.iGap.realm.RealmCallConfig;
import net.iGap.request.RequestSignalingAccept;
import net.iGap.request.RequestSignalingLeave;
import net.iGap.request.RequestSignalingOffer;
import org.webrtc.AudioSource;
import org.webrtc.AudioTrack;
import org.webrtc.MediaConstraints;
import org.webrtc.MediaStream;
import org.webrtc.PeerConnection;
import org.webrtc.PeerConnectionFactory;
import org.webrtc.SdpObserver;
import org.webrtc.SessionDescription;

public class WebRTC {
    private static PeerConnection peerConnection;
    private static PeerConnectionFactory peerConnectionFactory;
    private MediaConstraints mediaConstraints;
    private MediaConstraints audioConstraints;
    private AudioTrack audioTrack;
    private AudioSource audioSource;

    public WebRTC() {
        peerConnectionInstance();
    }

    private void addAudioTrack(MediaStream mediaStream) {
        audioSource = peerConnectionFactoryInstance().createAudioSource(audioConstraintsGetInstance());
        audioTrack = peerConnectionFactoryInstance().createAudioTrack("ARDAMSa0", audioSource);
        audioTrack.setEnabled(true);
        mediaStream.addTrack(audioTrack);
    }

    /**
     * First, we initiate the PeerConnectionFactory with our application context and some options.
     */
    private PeerConnectionFactory peerConnectionFactoryInstance() {
        if (peerConnectionFactory == null) {
            PeerConnectionFactory.initializeAndroidGlobals(G.context,  // Context
                    true,  // Audio Enabled
                    true,  // Video Enabled
                    true, null); // Hardware Acceleration Enabled

            peerConnectionFactory = new PeerConnectionFactory();
        }
        return peerConnectionFactory;
    }

    PeerConnection peerConnectionInstance() {
        if (peerConnection == null) {
            List<PeerConnection.IceServer> iceServers = new ArrayList<>();
            Realm realm = Realm.getDefaultInstance();
            RealmCallConfig realmCallConfig = realm.where(RealmCallConfig.class).findFirst();
            for (ProtoSignalingGetConfiguration.SignalingGetConfigurationResponse.IceServer ice : realmCallConfig.getIceServer()) {
                iceServers.add(new PeerConnection.IceServer(ice.getUrl(), ice.getUsername(), ice.getCredential()));
            }
            realm.close();

            PeerConnection.RTCConfiguration configuration = new PeerConnection.RTCConfiguration(iceServers);
            configuration.bundlePolicy = PeerConnection.BundlePolicy.MAXBUNDLE;
            configuration.rtcpMuxPolicy = PeerConnection.RtcpMuxPolicy.REQUIRE;
            configuration.iceTransportsType = PeerConnection.IceTransportsType.RELAY;
            peerConnection = peerConnectionFactoryInstance().createPeerConnection(iceServers, mediaConstraintsGetInstance(), new PeerConnectionObserver());

            MediaStream mediaStream = peerConnectionFactoryInstance().createLocalMediaStream("ARDAMS");
            addAudioTrack(mediaStream);
            peerConnection.addStream(mediaStream);
        }

        return peerConnection;
    }

    public void createOffer(final long userIdCallee) {
        peerConnectionInstance().createOffer(new SdpObserver() {
            @Override
            public void onCreateSuccess(SessionDescription sessionDescription) {
                setLocalDescription(SessionDescription.Type.OFFER, sessionDescription.description);
                new RequestSignalingOffer().signalingOffer(userIdCallee, ProtoSignalingOffer.SignalingOffer.Type.VOICE_CALLING, sessionDescription.description);
            }

            @Override
            public void onSetSuccess() {

            }

            @Override
            public void onCreateFailure(String s) {

            }

            @Override
            public void onSetFailure(String s) {

            }
        }, mediaConstraintsGetInstance());
    }

    public void createAnswer() {

        peerConnectionInstance().createAnswer(new SdpObserver() {
            @Override
            public void onCreateSuccess(SessionDescription sessionDescription) {
                setLocalDescription(SessionDescription.Type.ANSWER, sessionDescription.description);
                Log.i("WWW", "onCreateSuccess sessionDescription.description : " + sessionDescription.description);
                Log.i("WWW", "onCreateSuccess sessionDescription.type : " + sessionDescription.type);
                acceptCall(sessionDescription.description);
            }

            @Override
            public void onSetSuccess() {

            }

            @Override
            public void onCreateFailure(String s) {

            }

            @Override
            public void onSetFailure(String s) {

            }
        }, mediaConstraintsGetInstance());
    }

    private void setLocalDescription(final SessionDescription.Type type, String sdp) {
        peerConnectionInstance().setLocalDescription(new SdpObserver() {
            @Override
            public void onCreateSuccess(SessionDescription sessionDescription) {
            }

            @Override
            public void onSetSuccess() {
                Log.i("WWW", "onSetSuccess");
            }

            @Override
            public void onCreateFailure(String s) {

            }

            @Override
            public void onSetFailure(String s) {

            }
        }, new SessionDescription(type, sdp));
    }

    private MediaConstraints mediaConstraintsGetInstance() {
        if (mediaConstraints == null) {
            mediaConstraints = new MediaConstraints();
            mediaConstraints.optional.add(new MediaConstraints.KeyValuePair("DtlsSrtpKeyAgreement", "true"));
        }
        return mediaConstraints;
    }

    private MediaConstraints audioConstraintsGetInstance() {
        if (audioConstraints == null) {
            audioConstraints = new MediaConstraints();
            audioConstraints.optional.add(new MediaConstraints.KeyValuePair("DtlsSrtpKeyAgreement", "true"));
        }
        return audioConstraints;
    }

    private void acceptCall(String sdp) {
        new RequestSignalingAccept().signalingAccept(sdp);
    }

    public void leaveCall() {
        close();
        dispose();
        /**
         * set peer connection null for try again
         */
        clearConnection();
        new RequestSignalingLeave().signalingLeave();
    }

    public void close() {
        peerConnectionInstance().close();
    }

    public void dispose() {
        peerConnectionInstance().dispose();
    }

    public void clearConnection() {
        peerConnection = null;
    }
}