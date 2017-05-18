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


import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import io.realm.Realm;
import java.util.ArrayList;
import java.util.List;
import net.iGap.G;
import net.iGap.R;
import net.iGap.activities.ActivityEnhanced;
import net.iGap.helper.HelperString;
import net.iGap.proto.ProtoSignalingGetConfiguration;
import net.iGap.proto.ProtoSignalingOffer;
import net.iGap.realm.RealmCallConfig;
import net.iGap.realm.RealmUserInfo;
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

public class WebRTC extends ActivityEnhanced {
    public static PeerConnection peerConnection;
    public static PeerConnectionFactory peerConnectionFactory;
    public static MediaConstraints mediaConstraints;
    public static MediaConstraints audioConstraints;
    private static AudioTrack audioTrack;
    private static AudioSource audioSource;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.web_rtc);
        setVolumeControlStream(AudioManager.STREAM_VOICE_CALL);
        Button btnCall = (Button) findViewById(R.id.btnCall);
        Button btnAnswer = (Button) findViewById(R.id.btnAnswer);
        Button btnCancel = (Button) findViewById(R.id.btnCancel);
        Button btnUserId = (Button) findViewById(R.id.btnUserId);
        final EditText editText = (EditText) findViewById(R.id.edtUserId);

        btnCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String userIdString = editText.getText().toString();
                long userId = 0;
                if (HelperString.isNumeric(userIdString)) {
                    userId = Integer.parseInt(userIdString);
                } else {
                    Toast.makeText(G.context, "Just Enter Number !", Toast.LENGTH_LONG).show();
                }
                if (userId != 0) {
                    createOffer(userId);
                } else {
                    Toast.makeText(G.context, "User Id Is Wrong !", Toast.LENGTH_LONG).show();
                }
            }
        });
        btnAnswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createAnswer();
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                leaveCall();
            }
        });

        btnUserId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Realm realm = Realm.getDefaultInstance();
                Toast.makeText(G.context, "UserId : " + realm.where(RealmUserInfo.class).findFirst().getUserId(), Toast.LENGTH_LONG).show();
                realm.close();
            }
        });

        start();
    }


    public void start() {
        peerConnectionInstance();
    }

    private static void addAudioTrack(MediaStream mediaStream) {
        audioSource = peerConnectionFactoryInstance().createAudioSource(audioConstraintsGetInstance());
        audioTrack = peerConnectionFactoryInstance().createAudioTrack("ARDAMSa0", audioSource);
        audioTrack.setEnabled(true);
        mediaStream.addTrack(audioTrack);
    }

    /**
     * First, we initiate the PeerConnectionFactory with our application context and some options.
     */
    private static PeerConnectionFactory peerConnectionFactoryInstance() {
        if (peerConnectionFactory == null) {
            PeerConnectionFactory.initializeAndroidGlobals(G.context,  // Context
                    true,  // Audio Enabled
                    true,  // Video Enabled
                    true); // Hardware Acceleration Enabled

            peerConnectionFactory = new PeerConnectionFactory(new PeerConnectionFactoryOptions());
        }
        return peerConnectionFactory;
    }

    public static PeerConnection peerConnectionInstance() {
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

            Toast.makeText(G.context, "WebRtc Connected!", Toast.LENGTH_SHORT).show();
        }

        return peerConnection;
    }

    public void createOffer(final long userIdCallee) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(G.context, "Dialling ... ", Toast.LENGTH_SHORT).show();
            }
        });

        peerConnectionInstance().createOffer(new SdpObserver() {
            @Override
            public void onCreateSuccess(SessionDescription sessionDescription) {
                setLocalDescription(SessionDescription.Type.OFFER, sessionDescription.description);
                new RequestSignalingOffer().signalingOffer(userIdCallee, ProtoSignalingOffer.SignalingOffer.Type.VIDEO_CALLING, sessionDescription.description);
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

    public void setLocalDescription(final SessionDescription.Type type, String sdp) {
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

    public static MediaConstraints mediaConstraintsGetInstance() {
        if (mediaConstraints == null) {
            mediaConstraints = new MediaConstraints();
            mediaConstraints.optional.add(new MediaConstraints.KeyValuePair("DtlsSrtpKeyAgreement", "true"));
        }
        return mediaConstraints;
    }

    public static MediaConstraints audioConstraintsGetInstance() {
        if (audioConstraints == null) {
            audioConstraints = new MediaConstraints();
            audioConstraints.optional.add(new MediaConstraints.KeyValuePair("DtlsSrtpKeyAgreement", "true"));
        }
        return audioConstraints;
    }

    private static void acceptCall(String sdp) {
        new RequestSignalingAccept().signalingAccept(sdp);
    }

    public static void leaveCall() {
        peerConnectionInstance().close();
        peerConnectionInstance().dispose();
        /**
         * set peer connection null for try again
         */
        peerConnection = null;
        new RequestSignalingLeave().signalingLeave();
    }
}